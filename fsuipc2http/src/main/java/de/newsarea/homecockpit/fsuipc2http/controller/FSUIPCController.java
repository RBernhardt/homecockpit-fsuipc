package de.newsarea.homecockpit.fsuipc2http.controller;

import de.newsarea.homecockpit.fsuipc.domain.ByteArray;
import de.newsarea.homecockpit.fsuipc.domain.OffsetIdent;
import de.newsarea.homecockpit.fsuipc.domain.OffsetItem;
import de.newsarea.homecockpit.fsuipc.flightsim.FSUIPCFlightSimInterface;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

@Path("/")
public class FSUIPCController {

    private static final String PARAM_OFFSET = "offset";
    private static final String PARAM_SIZE = "size";

    private static final Logger log = LoggerFactory.getLogger(FSUIPCController.class);

    private FSUIPCFlightSimInterface fsuipcFlightSimInterface;

    public FSUIPCController(FSUIPCFlightSimInterface fsuipcFlightSimInterface) {
        this.fsuipcFlightSimInterface = fsuipcFlightSimInterface;
    }

    @GET
    @Path("/offsets/{offset}")
    public Response getOffset(@PathParam(PARAM_OFFSET) String offsetHexString, @DefaultValue("1") @QueryParam(PARAM_SIZE) Integer size) {
        try {
            int offset = Integer.parseInt(offsetHexString, 16);
            // read offset and return result
            OffsetItem offsetItem = fsuipcFlightSimInterface.read(new OffsetIdent(offset, size));
            return Response.ok().entity(offsetItem.getValue().toHexString()).build();
        } catch(Exception ex) {
            log.error(ex.getMessage(), ex);
            return buildWithAllowOriginAll(Response.serverError());
        }
    }

    @POST
    @Path("/offsets/{offset}")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response postOffset(@PathParam(PARAM_OFFSET) String offsetHexString, MultivaluedMap<String, String> formParams) {
        try {
            int offset = Integer.parseInt(offsetHexString, 16);
            // handle data key
            String dataHexString = formParams.getFirst("data");
            if(StringUtils.isEmpty(dataHexString)) {
                return Response.status(400).entity("data required").build();
            }
            ByteArray data = ByteArray.create(dataHexString);
            // handle timeOfBlocking key
            int timeOfBlocking = 0;
            String timeOfBlockString = formParams.getFirst("timeOfBlocking");
            if(StringUtils.isNotEmpty(timeOfBlockString)) {
                timeOfBlocking = Integer.parseInt(timeOfBlockString);
            }
            // write message
            OffsetItem offsetItem = new OffsetItem(offset, data.getSize(), data);
            fsuipcFlightSimInterface.write(offsetItem, timeOfBlocking);
            return Response.ok().build();
        } catch(Exception ex) {
            log.error(ex.getMessage(), ex);
            return buildWithAllowOriginAll(Response.serverError());
        }
    }

    @POST
    @Path("/offsets/{offset}")
    @Consumes(MediaType.TEXT_PLAIN)
    public Response postOffset(@PathParam(PARAM_OFFSET) String offsetHexString, String dataHexString) {
        try {
            int offset = Integer.parseInt(offsetHexString, 16);
            // handle data key
            if(StringUtils.isEmpty(dataHexString)) {
                return Response.status(400).entity("data required").build();
            }
            ByteArray data = ByteArray.create(dataHexString);
            // write message
            OffsetItem offsetItem = new OffsetItem(offset, data.getSize(), data);
            fsuipcFlightSimInterface.write(offsetItem);
            return Response.ok().build();
        } catch(Exception ex) {
            log.error(ex.getMessage(), ex);
            return buildWithAllowOriginAll(Response.serverError());
        }
    }

    @POST
    @Path("/monitor")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response postMonitor(MultivaluedMap<String, String> formParams) {
        try {
            // handle offset key
            String offsetHexString =  formParams.getFirst("offset");
            if(StringUtils.isEmpty(offsetHexString)) {
                return Response.status(400).entity("offset required").build();
            }
            int offset = Integer.parseInt(offsetHexString, 16);
            // handle size key
            String sizeString =  formParams.getFirst(PARAM_SIZE);
            if(StringUtils.isEmpty(sizeString)) {
                return Response.status(400).entity("size required").build();
            }
            int size = Integer.parseInt(sizeString);
            // monitor offset
            fsuipcFlightSimInterface.monitor(new OffsetIdent(offset, size));
            return Response.ok().build();
        } catch(Exception ex) {
            log.error(ex.getMessage(), ex);
            return buildWithAllowOriginAll(Response.serverError());
        }
    }

    /* HELPER */

    private Response buildWithAllowOriginAll(Response.ResponseBuilder responseBuilder) {
        return responseBuilder.header("Access-Control-Allow-Origin", "*").build();
    }

}
