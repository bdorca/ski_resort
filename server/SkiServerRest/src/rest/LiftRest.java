package rest;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.security.auth.login.LoginException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import exception.LiftException;
import management.lift.Command;
import management.lift.LiftHolderLocal;
import management.lift.LiftModel;
import management.lift.ListOfLifts;
import user.UserAuthenticateLocal;

@Path("liftData")
@Stateless
public class LiftRest {
	@Context
	private UriInfo context;

	@EJB
	LiftHolderLocal liftHolder;

	@EJB
	UserAuthenticateLocal authenticator;

	/**
	 * Default constructor.
	 */
	public LiftRest() {
		// TODO Auto-generated constructor stub
	}

	@GET
	@Path("lifts/{id}")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public LiftModel getLiftById(@PathParam("id") String id) {
		LiftModel lm;
		try {
			lm = liftHolder.getLift(id);
			return lm;
		} catch (LiftException e) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
	}

	@GET
	@Path("lifts")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response getLifts() {
		List<LiftModel> lm;
		try {
			lm = liftHolder.getLifts();
			return getNoCacheResponseBuilder( Response.Status.OK ).entity( new ListOfLifts(lm) ).build();
		} catch (LiftException e) {
			e.printStackTrace();
			return Response.status(Response.Status.NOT_FOUND).entity("no lifts").build();
		}
	}
	
    protected ResponseBuilder getNoCacheResponseBuilder( Status status ) {
        CacheControl cc = new CacheControl();
        cc.setNoCache( true );
        cc.setMaxAge( -1 );
        cc.setMustRevalidate( true );
 
        return Response.status( status ).cacheControl( cc );
    }

	@POST
	@Path("lifts")
	@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response setLift(LiftModel lift) {
		try {
			liftHolder.setLift(lift);
			return Response.ok().build();
		} catch (LiftException e) {
			e.printStackTrace();
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
	}

	@GET
	@Path("command/{id}")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response sendCommand(@PathParam("id") String id, @QueryParam("command") String command,
			@QueryParam("arg") int arg, @CookieParam("token") Cookie token) {
//		System.out.println(token.getName());
		if (token!=null && authenticator.isAuthTokenValid(token.getValue())) {
			try {
				Command cmd = Command.valueOf(command);
				liftHolder.sendCommand(id, cmd, arg);
			} catch (IllegalArgumentException e) {
				throw new WebApplicationException(Response.Status.NOT_FOUND);
			} catch (LiftException e) {
				throw new WebApplicationException(Response.Status.NOT_FOUND);
			}
			return Response.ok(token.getValue()).build();
		}
		return Response.status(Response.Status.UNAUTHORIZED).build();
	}
	
	@Context HttpServletRequest request;
	
	@POST
	@Path("login")
	@Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML,MediaType.APPLICATION_FORM_URLENCODED})
	@Consumes({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML,MediaType.APPLICATION_FORM_URLENCODED})
	public Response login(@FormParam("username")String username,@FormParam("password")String password) throws LoginException {
		try {
			String authToken = authenticator.login(username, password);
			NewCookie cookie=new NewCookie("token", authToken,"/","","comment",10000, false);
			return Response.status(Status.SEE_OTHER).location(UriBuilder.fromPath("../").build()).cookie(cookie).build();

		} catch (final LoginException ex) {
			return Response.status(Status.SEE_OTHER).location(UriBuilder.fromPath("../").build()).build();
		}
	}
	
	@GET
	@Path("/logout")
	@Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
	public Response logout(@CookieParam("token") Cookie cookie) {
	    if (cookie != null) {
	        NewCookie newCookie = new NewCookie(cookie, "", 0, false);
	        return Response.ok("OK").cookie(newCookie).build();
	    }
	    return Response.ok("OK - No session").build();
	}
}