package de.sn.quarkus.businessfunctions.resources;

import java.util.List;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import javax.validation.constraints.Min;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import de.sn.quarkus.businessfunctions.model.Project;
import io.quarkus.panache.common.Page;

@Path("/projects")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Transactional
public class ProjectResource {

	@GET
    public Response getPagableList( 
    		@QueryParam("pageNum") @DefaultValue("0") @Min(0) int pageNum, 
    		@QueryParam("pageSize") @DefaultValue("10") @Min(0) int pageSize) {
    	
		long timestamp = System.currentTimeMillis();
    	
		List<Project> locomotives = Project
    			.findAll().page(Page.of(pageNum, pageSize)).list();
    	
    	return Response
    			.ok(locomotives)
        		.header("responsetime", (System.currentTimeMillis() - timestamp))
    			.build();
    }
}