package es.uvigo.esei.daa.rest;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import es.uvigo.esei.daa.dao.DAOException;
import es.uvigo.esei.daa.dao.PetsDAO;
import es.uvigo.esei.daa.entities.Person;
import es.uvigo.esei.daa.entities.Pet;

/**
 * REST resource for managing pets.
 * 
 * @author Daniel Duque Puga
 */
@Path("/pets")
@Produces(MediaType.APPLICATION_JSON)
public class PetsResource {
	private final static Logger LOG = Logger.getLogger(PetsResource.class.getName());
	
	private final PetsDAO dao;
	
	/**
	 * Constructs a new instance of {@link PetsResource}.
	 */
	public PetsResource() {
		this(new PetsDAO());
	}
	
	// Needed for testing purposes
	PetsResource(PetsDAO dao) {
		this.dao = dao;
	}
	
	/**
	 * Returns a pet with the provided identifier.
	 * 
	 * @param id the identifier of the pet to retrieve.
	 * @return a 200 OK response with a pet that has the provided identifier.
	 * If the identifier does not corresponds with any user, a 400 Bad Request
	 * response with an error message will be returned. If an error happens
	 * while retrieving the list, a 500 Internal Server Error response with an
	 * error message will be returned.
	 */
	@GET
	@Path("/{id}")
	public Response get(
		@PathParam("id") int id
	) {
		try {
			final Pet pet = this.dao.get(id);
			
			return Response.ok(pet).build();
		} catch (IllegalArgumentException iae) {
			LOG.log(Level.FINE, "Invalid pet id in get method", iae);
			
			return Response.status(Response.Status.BAD_REQUEST)
				.entity(iae.getMessage())
			.build();
		} catch (DAOException e) {
			LOG.log(Level.SEVERE, "Error getting a pet", e);
			
			return Response.serverError()
				.entity(e.getMessage())
			.build();
		}
	}

	/**
	 * Returns the complete list of pets stored in the system.
	 * 
	 * @return a 200 OK response with the complete list of pets stored in the
	 * system. If an error happens while retrieving the list, a 500 Internal
	 * Server Error response with an error message will be returned.
	 */
	@GET
	public Response list(
		@QueryParam("ownerId") int ownerId
	) {
		try {
			return Response.ok(this.dao.listWithOwner(ownerId)).build();
		} catch (DAOException e) {
			LOG.log(Level.SEVERE, "Error listing pets", e);
			return Response.serverError().entity(e.getMessage()).build();
		}
	}

	/**
	 * Creates a new pet in the system.
	 * 
	 * @param name the name of the new pet.
	 * @param owner the surname of the new person.
	 * @return a 200 OK response with a pet that has been created. If the
	 * name or the owner are not provided, a 400 Bad Request response with an
	 * error message will be returned. If an error happens while retrieving the
	 * list, a 500 Internal Server Error response with an error message will be
	 * returned.
	 */
	@POST
	public Response add(
		@FormParam("name") String name, 
		@FormParam("owner") int owner
	) {
		try {
			final Pet newPet = this.dao.add(name, owner);
			
			return Response.ok(newPet).build();
		} catch (IllegalArgumentException iae) {
			LOG.log(Level.FINE, "Invalid pet id in add method", iae);
			
			return Response.status(Response.Status.BAD_REQUEST)
				.entity(iae.getMessage())
			.build();
		} catch (DAOException e) {
			LOG.log(Level.SEVERE, "Error adding a pet", e);
			
			return Response.serverError()
				.entity(e.getMessage())
			.build();
		}
	}

	/**
	 * Modifies the data of a pet.
	 * 
	 * @param id identifier of the person to modify.
	 * @param name the new name of the person.
	 * @param surname the new surname of the person.
	 * @return a 200 OK response with a person that has been modified. If the
	 * identifier does not corresponds with any user or the name or surname are
	 * not provided, a 400 Bad Request response with an error message will be
	 * returned. If an error happens while retrieving the list, a 500 Internal
	 * Server Error response with an error message will be returned.
	 */
	
	@PUT
	@Path("/{id}")
	public Response modify(
		@PathParam("id") int id, 
		@FormParam("name") String name, 
		@FormParam("owner") int owner
	) {
		try {
			final Pet modifiedPet = new Pet(id, name, this.dao.get(id).getOwner());
			this.dao.modify(modifiedPet);
			
			return Response.ok(modifiedPet).build();
		} catch (NullPointerException npe) {
			final String message = String.format("Invalid data for pet (name: %s, owner %d)", name, owner);
			
			LOG.log(Level.FINE, message);
			
			return Response.status(Response.Status.BAD_REQUEST)
				.entity(message)
			.build();
		} catch (IllegalArgumentException iae) {
			LOG.log(Level.FINE, "Invalid pet id in modify method", iae);
			
			return Response.status(Response.Status.BAD_REQUEST)
				.entity(iae.getMessage())
			.build();
		} catch (DAOException e) {
			LOG.log(Level.SEVERE, "Error modifying a pet", e);
			
			return Response.serverError()
				.entity(e.getMessage())
			.build();
		}
	}

	/**
	 * Deletes a PET from the system.
	 * 
	 * @param id the identifier of the pet to be deleted.
	 * @return a 200 OK response with the identifier of the pet that has
	 * been deleted. If the identifier does not corresponds with any user, a 400
	 * Bad Request response with an error message will be returned. If an error
	 * happens while retrieving the list, a 500 Internal Server Error response
	 * with an error message will be returned.
	 */
	@DELETE
	@Path("/{id}")
	public Response delete(
		@PathParam("id") int id
	) {
		try {
			this.dao.delete(id);
			
			return Response.ok(id).build();
		} catch (IllegalArgumentException iae) {
			LOG.log(Level.FINE, "Invalid pet id in delete method", iae);
			
			return Response.status(Response.Status.BAD_REQUEST)
				.entity(iae.getMessage())
			.build();
		} catch (DAOException e) {
			LOG.log(Level.SEVERE, "Error deleting a pet", e);
			
			return Response.serverError()
				.entity(e.getMessage())
			.build();
		}
	}
}
