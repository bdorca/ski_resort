package websocket;

import javax.ejb.Local;
import javax.ejb.Remote;

@Remote
public interface CommunicatorSocketLocal {

	void sendMessageToId(String id, String msg);

}
