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
import java.io.IOException;

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
            String value = readOffsetValue(offset, size);
            return buildWithAllowOriginAll(Response.ok(value, MediaType.TEXT_PLAIN));
        } catch(IOException ex) {
             return buildWithAllowOriginAll(Response.status(Response.Status.SERVICE_UNAVAILABLE));
        } catch(Exception ex) {
            log.error(ex.getMessage(), ex);
            return buildWithAllowOriginAll(Response.serverError());
        }
    }

    @PUT
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
            // read value
            String value = readOffsetValue(offset, data.getSize());
            return buildWithAllowOriginAll(Response.ok(value, MediaType.TEXT_PLAIN));
        } catch(IOException ex) {
            return buildWithAllowOriginAll(Response.status(Response.Status.SERVICE_UNAVAILABLE));
        } catch(Exception ex) {
            log.error(ex.getMessage(), ex);
            return buildWithAllowOriginAll(Response.serverError());
        }
    }

    @PUT
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
            // read value
            String value = readOffsetValue(offset, data.getSize());
            return buildWithAllowOriginAll(Response.ok(value, MediaType.TEXT_PLAIN));
        } catch(IOException ex) {
            return buildWithAllowOriginAll(Response.status(Response.Status.SERVICE_UNAVAILABLE));
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
            return buildWithAllowOriginAll(Response.ok());
        } catch(Exception ex) {
            log.error(ex.getMessage(), ex);
            return buildWithAllowOriginAll(Response.serverError());
        }
    }

    /* HELPER */

    private String readOffsetValue(int offset, int size) throws IOException {
        OffsetItem offsetItem = fsuipcFlightSimInterface.read(new OffsetIdent(offset, size));
        String value = offsetItem.getValue().toHexString();
        return value;
    }

    private Response buildWithAllowOriginAll(Response.ResponseBuilder responseBuilder) {
        return responseBuilder.header("Access-Control-Allow-Origin", "*").build();
    }

}
