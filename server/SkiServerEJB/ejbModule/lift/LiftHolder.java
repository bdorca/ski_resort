package lift;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import exception.LiftException;
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
	LiftFacade facade;

	CommunicatorSocketLocal socket;

	/**
	 * Default constructor.
	 */
	public LiftHolder() {
		InitialContext ic;
		try {
			ic = new InitialContext();
			socket = (CommunicatorSocketLocal) (ic.lookup(
					"java:global/SkiServer/SkiServerWeb/CommunicatorSocket!websocket.CommunicatorSocketLocal"));
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public LiftModel getLift(String id) throws LiftException {
		Lift lift = facade.findLiftByLiftId(id);
		if (lift == null)
			throw new LiftException("no such lift");
		return toLiftModel(lift);
	}

	@Override
	public List<LiftModel> getLifts() throws LiftException {
		List<LiftModel> models = new ArrayList<>();
		for (Lift l : facade.findAll()) {
			models.add(toLiftModel(l));
		}
		if (models.isEmpty()) {
			throw new LiftException("no lifts");
		}
		return models;
	}

	@Override
	public void setLift(LiftModel liftModel) throws LiftException {
		Lift to = facade.findLiftByLiftId(liftModel.getId());
		to.setSize(liftModel.getType().ordinal());
		facade.edit(to);
		socket.sendMessageToId(to.getLiftId(), "hehehe");
	}

	private LiftModel toLiftModel(Lift lift) {
		return new LiftModel(lift.getLiftId(), lift.getName(), lift.getSize());
	}

	private Lift toLift(LiftModel lift) {
		return new Lift(lift.getId(), lift.getName(), lift.getType());
	}

	@Override
	public void addNewLift(String liftId) {
		Lift l = facade.findLiftByLiftId(liftId);
		if (l == null) {
			facade.create(new Lift(liftId));
		}
	}

	@Override
	public void setLiftData(String liftId, String name, int size) {
		Lift l=facade.findLiftByLiftId(liftId);
		if (l == null) {
			facade.create(new Lift(liftId,name,size));
		}else{
			l.setName(name);
			l.setSize(size);
		}
	}

}
