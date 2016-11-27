package lift;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import management.AbstractFacade;

/**
 * Session Bean implementation class LiftValueFacade
 */
@Stateless
@LocalBean
public class LiftValueFacade extends AbstractFacade<LiftValue> implements LiftValueFacadeLocal {
	@PersistenceContext
	EntityManager em;

    /**
     * Default constructor. 
     */
    public LiftValueFacade() {
    	super(LiftValue.class);
    }

	@Override
	protected EntityManager em() {
		return em;
	}
       
}
