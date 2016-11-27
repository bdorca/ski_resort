package user;

import java.security.GeneralSecurityException;

import javax.ejb.Local;
import javax.security.auth.login.LoginException;

@Local
public interface UserAuthenticateLocal {
	void logout(String authToken) throws GeneralSecurityException;

	boolean isAuthTokenValid(String authToken);

	String login(String username, String password) throws LoginException;

}
