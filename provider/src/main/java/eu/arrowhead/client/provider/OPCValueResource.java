/*
 *  Copyright (c) 2018 AITIA International Inc.
 *
 *  This work is part of the Productive 4.0 innovation project, which receives grants from the
 *  European Commissions H2020 research and innovation programme, ECSEL Joint Undertaking
 *  (project no. 737459), the free state of Saxony, the German Federal Ministry of Education and
 *  national funding authorities from involved countries.
 */
package eu.arrowhead.client.provider;

import eu.arrowhead.client.common.model.OPCVariableReadout;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
//REST service example
public class OPCValueResource {
    

    @GET
    @Path("opcVariable/{address}/{ns}/{name}")
    public Response getIt(@Context SecurityContext context,
            @PathParam("address") String address,
            @PathParam("ns") int namesapce,
            @PathParam("name") String name) {

        if (context.isSecure()) {
            return Response.status(Response.Status.NOT_IMPLEMENTED).build();
        }
        OPCUAReader reader = OPCUAReader.get();
        OPCVariableReadout v;
        try {
            v = reader.read(address, namesapce, name);
            return Response.status(200).entity(v).build();
        } catch (Exception ex) {
            Logger.getLogger(OPCValueResource.class.getName()).log(Level.SEVERE, null, ex);
            return Response.status(500).build();
        }
        

    }

}
