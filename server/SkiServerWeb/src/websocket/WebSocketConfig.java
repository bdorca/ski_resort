package websocket;

import javax.websocket.server.ServerEndpointConfig;

public class WebSocketConfig extends ServerEndpointConfig.Configurator{

	@Override
	public boolean checkOrigin(String originHeaderValue) {
		// TODO Auto-generated method stub
		return true;
	}
	
	
}
