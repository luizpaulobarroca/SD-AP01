package AP01.resources;

import AP01.AP01Errors;
import AP01.dao.UniversityDAO;
import AP01.model.University;
import io.dropwizard.hibernate.UnitOfWork;
import org.hibernate.Hibernate;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/university")
@Produces(MediaType.APPLICATION_JSON)
public class UniversityResource {
    private final UniversityDAO universityDAO;

    public UniversityResource(UniversityDAO universityDAO) {
        this.universityDAO = universityDAO;
    }

//    @GET
//    @Path("/{id}")
//    @UnitOfWork
//    public Response getUniversity(@PathParam("id") Long id) {
//        University university = null;
//        try {
//            university = universityDAO.retrive(id);
//        } catch (Exception e) {
//            String error = AP01Errors.response("User not found.");
//            return Response.status(Response.Status.NOT_FOUND).entity(error).build();
//        }
//
//        return Response.ok(university).build();
//    }

    @GET
    @UnitOfWork
    public Response getAllUniversities() {
        List<University> users = universityDAO.retrieveAll();
        return Response.ok(users).build();
    }

    @POST
    @UnitOfWork
    public Response createUniversity(University university) {
        University universityIn = universityDAO.create(university);
        return Response.ok(universityIn).build();
    }

    @PUT
    @Path("/{id}")
    @UnitOfWork
    public Response updateUniversity(@PathParam("id") Long id, University university) {
        University universityIn = universityDAO.retrive(id);

        try {
            universityIn.setName(university.getName());
            universityIn = universityDAO.update(universityIn);
        } catch (Exception e) {
            String error = AP01Errors.response("User not found.");
            return Response.status(Response.Status.NOT_FOUND).entity(error).build();
        }

        return Response.ok(universityIn).build();
    }

    @DELETE
    @Path("/{id}")
    @UnitOfWork
    public Response deleteUniversity(@PathParam("id") Long id, University university) {


        try {
            University universityIn = universityDAO.retrive(id);
            universityDAO.delete(universityIn);
        } catch (Exception e) {
            String error = AP01Errors.response("User not found.");
            return Response.status(Response.Status.NOT_FOUND).entity(error).build();
        }

        return Response.ok().build();
    }
}
