package de.newsarea.homecockpit.fsuipc2http.controller;

import de.newsarea.homecockpit.fsuipc.flightsim.FSUIPCFlightSimInterface;
import de.newsarea.homecockpit.fsuipc2http.controller.converter.NewsareaStatusMediaTypeConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/status")
public class StatusController {

    private static final Logger log = LoggerFactory.getLogger(FSUIPCController.class);

    private FSUIPCFlightSimInterface fsuipcFlightSimInterface;

    public StatusController(FSUIPCFlightSimInterface fsuipcFlightSimInterface) {
        this.fsuipcFlightSimInterface = fsuipcFlightSimInterface;
    }

    @GET
    public Response getStatus() {
        try {
            boolean isConnectionEstablished = fsuipcFlightSimInterface.isConnectionEstablished();
            String status = isConnectionEstablished ? "OPEN" : "CLOSED";
            String responseValue = NewsareaStatusMediaTypeConverter.toFromUrlEncoded(status);
            return buildWithAllowOriginAll(Response.ok(responseValue, MediaType.APPLICATION_FORM_URLENCODED));
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
