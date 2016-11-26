package lift;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

import management.AbstractFacade;;

@Stateless
public class LiftFacade extends AbstractFacade<Lift> {

	@PersistenceContext
	EntityManager em;

	public LiftFacade() {
		super(Lift.class);
	}

	@Override
	protected EntityManager em() {
		return em;
	}

	public Lift findLiftByLiftId(String liftId) {
		try {
			Lift l = em().createQuery("SELECT l FROM Lift l WHERE l.liftId = :liftId", Lift.class)
					.setParameter("liftId", liftId).getSingleResult();
			return l;
		} catch (NoResultException e) {
			return null;
		}

	}
}
