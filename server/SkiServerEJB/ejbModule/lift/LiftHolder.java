package lift;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import exception.LiftException;
import management.lift.Command;
import management.lift.LiftHolderLocal;
import management.lift.LiftModel;
import websocket.CommunicatorSocketLocal;

/**
 * Session Bean implementation class LiftHolder
 */
@Stateless
@LocalBean
public class LiftHolder implements LiftHolderLocal {

	@EJB
	LiftFacade liftFacade;

	@EJB
	LiftValueFacade liftValueFacade;
	
	CommunicatorSocketLocal socket;

	/**
	 * Default constructor.
	 */
	public LiftHolder() {
		InitialContext ic;
		try {
			ic = new InitialContext();
			socket = (CommunicatorSocketLocal) (ic
					.lookup("java:global/SkiServer/SkiServerWeb/CommunicatorSocket!websocket.CommunicatorSocketLocal"));
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public LiftModel getLift(String id) throws LiftException {
		Lift lift = liftFacade.findLiftByLiftId(id);
		if (lift == null)
			throw new LiftException("no such lift");
		return toLiftModel(lift);
	}

	@Override
	public List<LiftModel> getLifts() throws LiftException {
		List<LiftModel> models = new ArrayList<>();
		for (Lift l : liftFacade.findAll()) {
			models.add(toLiftModel(l));
		}
		if (models.isEmpty()) {
			throw new LiftException("no lifts");
		}
		return models;
	}

	@Override
	public void setLift(LiftModel liftModel) throws LiftException {
		// Lift to = facade.findLiftByLiftId(liftModel.getId());
		// to.setSize(liftModel.getType().ordinal());
		// facade.edit(to);
		// socket.sendMessageToId(to.getLiftId(), "hehehe");
	}

	private LiftModel toLiftModel(Lift lift) {
		return new LiftModel(lift.getLiftId(),lift.getName(),lift.getType(),lift.getSpeed(),lift.getCustomers(),lift.getResource(),lift.getConsumption(),lift.getEvents());
	}

	private Lift toLift(LiftModel lift) {
		return new Lift(lift.getId(), lift.getName(), lift.getType(),lift.getSpeed(),lift.getCustomers(),lift.getResource(),lift.getConsumption(),lift.getEvents());
	}

	@Override
	public void addNewLift(String liftId) {
		Lift l = liftFacade.findLiftByLiftId(liftId);
		if (l == null) {
			liftFacade.create(new Lift(liftId));
		}
	}

	@Override
	public void setLiftData(String liftId, LiftModel lift) {
		Lift l = liftFacade.findLiftByLiftId(liftId);
		if (l == null) {
			l=toLift(lift);
			liftFacade.create(l);
		} else {
			l.setData(lift.getName(), lift.getType(),lift.getSpeed(),lift.getCustomers(),lift.getResource(),lift.getConsumption(),lift.getEvents());
		}
		
		if(l.getValues()==null){
			l.setValues(liftValueFacade.find(l.getType()));
			String name=l.getValues().getName();
			float initResource=l.getValues().getInitResource();
			l.setName(name);
			l.setResource(initResource);
			try {
				sendCommand(liftId,Command.name,l.getValues().getName());
				sendCommand(liftId,Command.resource,initResource);
			} catch (LiftException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void sendCommand(String liftId, Command cmd, String arg) throws LiftException {
		Lift l = liftFacade.findLiftByLiftId(liftId);
		if (l == null) {
			throw new LiftException("No lift with id");
		}
		socket.sendCommandToLiftId(liftId, cmd, "" + arg);		
	}

	@Override
	public void sendCommand(String liftId, Command cmd, float arg) throws LiftException {
		sendCommand(liftId,cmd,""+arg);
	}	
}
