package user;

import java.security.GeneralSecurityException;
import java.util.List;
import java.util.UUID;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.NoResultException;
import javax.security.auth.login.LoginException;

import management.lift.LiftHolderLocal;

/**
 * Session Bean implementation class UserAuthenticate
 * http://www.developerscrappad.com/1814/java/java-ee/rest-jax-rs/java-ee-7-jax-rs-2-0-simple-rest-api-authentication-authorization-with-custom-http-header/
 */
@Stateless
@LocalBean
public class UserAuthenticate implements UserAuthenticateLocal {

	@EJB
	UserFacade userFacade;

	private UserAuthenticate() {
		InitialContext ic;
		try {
			ic = new InitialContext();
			userFacade = (UserFacade) (ic.lookup("java:global/SkiServer/SkiServerEJB/UserFacade!user.UserFacade"));
		} catch (NamingException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String login(String username, String password) throws LoginException {
		String passwordMatch = "";
		try {
			UserData ud = userFacade.find(username);
			if (ud != null) {
				passwordMatch = ud.getPassword();
				if (passwordMatch.equals(password)) {

					/**
					 * Once all params are matched, the authToken will be
					 * generated and will be stored in the
					 * authorizationTokensStorage. The authToken will be needed
					 * for every REST API invocation and is only valid within
					 * the login session
					 */
					String authToken = UUID.randomUUID().toString();
					ud.setToken(authToken);
					ud.setValidUntil(System.currentTimeMillis()+300*1000);
					return authToken;
				}
			}
		} catch (NoResultException e) {
			e.printStackTrace();
		}
		throw new LoginException("username or password is wrong");
	}

	/**
	 * The method that pre-validates if the client which invokes the REST API is
	 * from a authorized and authenticated source.
	 *
	 * @param serviceKey
	 *            The service key
	 * @param authToken
	 *            The authorization token generated after login
	 * @return TRUE for acceptance and FALSE for denied.
	 */
	@Override
	public boolean isAuthTokenValid(String authToken) {
		List<UserData> list = userFacade.findAll();
		for (UserData ud : list) {
			if (authToken.equals(ud.getToken())&&System.currentTimeMillis()<=ud.getValidUntil()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * This method checks is the service key is valid
	 *
	 * @param serviceKey
	 * @return TRUE if service key matches the pre-generated ones in service key
	 *         storage. FALSE for otherwise.
	 */
	@Override
	public void logout(String authToken) throws GeneralSecurityException {
		List<UserData> list = userFacade.findAll();
		for (UserData ud : list) {
			if (ud.getToken().equals(authToken)) {
				ud.setToken(null);
			}
		}

		throw new GeneralSecurityException("Invalid authorization token match.");
	}

}
