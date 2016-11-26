package rest;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import exception.LiftException;
import management.lift.LiftHolderLocal;
import management.lift.LiftModel;

@Path("liftData")
@Stateless
public class LiftRest {
    @Context
    private UriInfo context;

    @EJB
    LiftHolderLocal liftHolder;
    
    /**
     * Default constructor. 
     */
    public LiftRest() {
        // TODO Auto-generated constructor stub
    }

    @GET
    @Path("lifts/{id}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public LiftModel getLiftById(@PathParam("id")String id){
    	LiftModel lm;
//    	try {
//			lm=liftHolder.getLift(id);
    		lm=new LiftModel("Egy", "lift", 1);
			return lm;
//		} catch (LiftException e) {
//	        throw new WebApplicationException(Response.Status.NOT_FOUND);	
//		}
    }
    

    @GET
    @Path("lifts")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<LiftModel> getLifts(){
    	List<LiftModel> lm;
    	try {
			lm=liftHolder.getLifts();
			return lm;
		} catch (LiftException e) {
			e.printStackTrace();
			throw new WebApplicationException(Response.Status.NOT_FOUND);	
		}
    }

    @POST
    @Path("lifts")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public void setLift(LiftModel lift){
    	try {
			liftHolder.setLift(lift);
		} catch (LiftException e) {
			e.printStackTrace();
	        throw new WebApplicationException(Response.Status.NOT_FOUND);	
		}
    }
    
}