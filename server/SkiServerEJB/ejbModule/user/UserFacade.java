package user;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import management.AbstractFacade;

@Stateless
@LocalBean
public class UserFacade extends AbstractFacade<UserData> {
	@PersistenceContext
	EntityManager em;


	public UserFacade() {
		super(UserData.class);
	}

	@Override
	protected EntityManager em() {
		return em;
	}

}
