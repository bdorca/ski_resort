package websocket;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import javax.json.Json;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;

import management.lift.LiftHolderLocal;
import management.lift.LiftType;

/**
 * Session Bean implementation class CommunicatorSocket
 */
@ServerEndpoint("/lift/{liftId}")
@Stateless
@LocalBean
public class CommunicatorSocket implements CommunicatorSocketRemote, CommunicatorSocketLocal {

	private static Map<Session, String> availableSessions = new HashMap<>();

	private LiftHolderLocal liftHolder;

	/**
	 * Default constructor.
	 */
	public CommunicatorSocket() {
		InitialContext ic;
		try {
			ic = new InitialContext();
			liftHolder = (LiftHolderLocal) (ic
					.lookup("java:global/SkiServer/SkiServerEJB/LiftHolder!management.lift.LiftHolderLocal"));
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@PostConstruct
	public void postContr() {

	}

	@OnOpen
	public void onOpen(Session session, @PathParam("liftId") String liftId) {
		availableSessions.put(session, liftId);
		liftHolder.addNewLift(liftId);
		System.out.println("new session, lift: " + liftId);
	}

	@OnClose
	public void onClose(Session session) {
		System.out.println("session removed: " + availableSessions.remove(session));
	}

	@OnError
	public void onError(Session session, Throwable t) {
		t.printStackTrace();
	}

	@OnMessage
	public void onMessage(Session session, String message) {
		String liftId = availableSessions.get(session);
		System.out.println("message: " + message + " , lift: " + liftId);
		JsonObject jObject = null;
		String messageType = null;
		try {
			JsonReader jsonReader = Json.createReader(new StringReader(message));
			jObject = jsonReader.readObject();
			messageType = jObject.getString("action");
			jsonReader.close();
		} catch (NullPointerException e) {
			e.printStackTrace();
			sendMessageToSession(session, "The message does not contain a 'type' field.");
			return;
		}
		switch (messageType) {
		case "add":
			addNewLift(liftId, jObject);
			break;
		default:
			sendMessageToSession(session, "Unexpected operation: " + messageType);
			break;
		}

	}

	private void addNewLift(String liftId, JsonObject data) {
		try {
			String name = data.getString("name");
			String type = data.getString("type");
			int size = LiftType.valueOf(type).ordinal();
			liftHolder.setLiftData(liftId, name, size);
		} catch (ClassCastException | NullPointerException e) {
			e.printStackTrace();
			sendErrorToSession(getSessionbyId(liftId), "json has bad type");
		}
	}

	@Override
	public void sendMessageToId(String id, String msg) {
		sendMessageToSession(getSessionbyId(id), msg);
	}

	public void sendMessageToSession(Session s, String msg) {
		if (s != null && s.isOpen())
			s.getAsyncRemote().sendText(msg);
	}

	private void sendErrorToSession(Session s, String msg) {
		JsonObject jo = Json.createObjectBuilder().add("action", "error").add("message", msg).build();
		sendMessageToSession(s, jo.toString());
	}

	private Session getSessionbyId(String id) {
		Session s = null;
		for (Entry<Session, String> e : availableSessions.entrySet()) {
			if (e.getValue().equals(id)) {
				s = e.getKey();
			}
		}
		return s;
	}
}
