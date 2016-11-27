package management.lift;

import java.util.List;

import javax.ejb.Local;

import exception.LiftException;

@Local
public interface LiftHolderLocal {

	LiftModel getLift(String id) throws LiftException;
	List<LiftModel> getLifts() throws LiftException;
	void setLift(LiftModel liftModel) throws LiftException;
	void addNewLift(String liftId);
	void setLiftData(String liftId, LiftModel l);
	void sendCommand(String liftId, Command cmd, float arg) throws LiftException;
	void sendCommand(String liftId, Command name, String arg) throws LiftException;
	void setLiftData(String liftId, String name, LiftType type);
}
