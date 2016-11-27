package websocket;

import javax.ejb.Local;
import javax.ejb.Remote;

import management.lift.Command;

@Remote
public interface CommunicatorSocketLocal {

	void sendCommandToLiftId(String liftId, Command command, String argument);

}
