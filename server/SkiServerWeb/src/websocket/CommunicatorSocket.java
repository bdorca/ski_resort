package websocket;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import management.lift.Command;
import management.lift.Events;
import management.lift.LiftHolderLocal;
import management.lift.LiftModel;
import management.lift.LiftType;

/**
 * Session Bean implementation class CommunicatorSocket
 */
@ServerEndpoint("/lift")
@Stateless
@LocalBean
public class CommunicatorSocket implements CommunicatorSocketLocal {

	private static Map<Session, String> availableSessions = new HashMap<>();

	private LiftHolderLocal liftHolder;

	ScheduledExecutorService executor;

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
		if(executor==null)
			executor = Executors.newScheduledThreadPool(1);
		executor.scheduleAtFixedRate(reportTask, 0, 5, TimeUnit.SECONDS);
	}

	Runnable reportTask = new Runnable() {

		@Override
		public void run() {
			for (Entry<Session, String> e : availableSessions.entrySet()) {
				sendCommandToLiftId(e.getValue(), Command.report, "");
			}
		}
	};

	@OnOpen
	public void onOpen(Session session) {
		String liftId = UUID.randomUUID().toString();
		availableSessions.put(session, liftId);
		liftHolder.addNewLift(liftId);
		 sendCommandToLiftId(liftId, Command.id, liftId);
		sendCommandToLiftId(liftId, Command.report, "");
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
//		System.out.println("message: " + message + " , lift: " + liftId);
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
		JsonObject jo=jObject.getJsonObject("data");
		switch (messageType) {
		case "report":
			addNewLift(liftId, jo);
			break;
		case "add":
			addNewLift(liftId,jo.getString("name"),LiftType.valueOf(jo.getString("type")));
		default:
			sendMessageToSession(session, "Unexpected operation: " + messageType);
			break;
		}

	}

	private void addNewLift(String liftId, String name, LiftType type) {
		liftHolder.setLiftData(liftId, name,type);
		
	}

	private void addNewLift(String liftId, JsonObject data) {

		try {
			String name = data.getString("name");
			String type = data.getString("type");
			LiftType size = LiftType.valueOf(type);
			float speed = (float) (data.getJsonNumber("speed")).doubleValue();
			float customers = (float) (data.getJsonNumber("customers")).doubleValue();
			float resource = (float) (data.getJsonNumber("resource")).doubleValue();
			float consumption = (float) (data.getJsonNumber("speed")).doubleValue();
			JsonObject eventsObject = data.getJsonObject("events");
			boolean running=data.getBoolean("running");
			Events events = new Events((float) eventsObject.getJsonNumber("failure").doubleValue(),
					(float) eventsObject.getJsonNumber("add_people").doubleValue());
			LiftModel l = new LiftModel(liftId, name, size, speed, customers, resource, consumption, events,running);
			liftHolder.setLiftData(liftId, l);
		} catch (ClassCastException | NullPointerException | IllegalArgumentException e) {
			e.printStackTrace();
			sendErrorToSession(getSessionbyId(liftId), "json has bad type");
		}
	}

	public void sendMessageToId(String id, String msg) {
//		System.out.println("sendTo "+ id+": "+msg);
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

	@Override
	public void sendCommandToLiftId(String liftId, Command command, String argument) {
		JsonObject jo = Json.createObjectBuilder().add("command", command.toString()).add("arg", argument).build();
		sendMessageToId(liftId, jo.toString());
	}
}
