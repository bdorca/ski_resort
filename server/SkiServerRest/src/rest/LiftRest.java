package rest;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.security.auth.login.LoginException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import exception.LiftException;
import management.lift.Command;
import management.lift.LiftHolderLocal;
import management.lift.LiftModel;
import management.lift.ListOfLifts;
import user.UserAuthenticateLocal;
import user.UserDataModel;

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
			throw new WebApplicationException(Response.Status.NO_CONTENT);
		}
	}

	@GET
	@Path("lifts")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public ListOfLifts getLifts() {
		List<LiftModel> lm;
		try {
			lm = liftHolder.getLifts();
			return new ListOfLifts(lm);
		} catch (LiftException e) {
			e.printStackTrace();
			throw new WebApplicationException(Response.Status.NO_CONTENT);
		}
	}

	@POST
	@Path("lifts")
	@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public void setLift(LiftModel lift) {
		try {
			liftHolder.setLift(lift);
		} catch (LiftException e) {
			e.printStackTrace();
			throw new WebApplicationException(Response.Status.NOT_ACCEPTABLE);
		}
	}

	@GET
	@Path("command/{id}")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response sendCommand(@PathParam("id") String id, @QueryParam("command") String command,
			@QueryParam("arg") int arg, @QueryParam("token") String token) {
		if (authenticator.isAuthTokenValid(token)) {
			try {
				Command cmd = Command.valueOf(command);
				liftHolder.sendCommand(id, cmd, arg);
			} catch (IllegalArgumentException e) {
				throw new WebApplicationException(Response.Status.NOT_ACCEPTABLE);
			} catch (LiftException e) {
				throw new WebApplicationException(Response.Status.NOT_ACCEPTABLE);
			}
			return Response.ok().build();
		}
		throw new WebApplicationException(Response.Status.UNAUTHORIZED);
	}

	@POST
	@Path("login")
	@Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
	@Consumes({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
	public Response login(UserDataModel userData) throws LoginException {
		try {
			String authToken = authenticator.login(userData.getUserName(), userData.getPassword());

			return Response.ok("{authToken:" + authToken + "}").build();

		} catch (final LoginException ex) {
			throw new WebApplicationException(Response.Status.UNAUTHORIZED);
		}
	}

}