package exception;

import javax.ejb.ApplicationException;

@ApplicationException(rollback=true)
public class LiftException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6255230812942097465L;

	public LiftException(String msg){
		super(msg);
	}
	
}
