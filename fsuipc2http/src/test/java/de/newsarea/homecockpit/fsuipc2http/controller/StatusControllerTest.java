package de.newsarea.homecockpit.fsuipc2http.controller;

import de.newsarea.homecockpit.fsuipc.flightsim.FSUIPCFlightSimInterface;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.ws.rs.core.Response;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.AssertJUnit.assertEquals;

public class StatusControllerTest {

    private FSUIPCFlightSimInterface fsuipcFlightSimInterface;
    private StatusController statusController;

    @BeforeMethod
    public void setUp() throws Exception {
        fsuipcFlightSimInterface = mock(FSUIPCFlightSimInterface.class);
        statusController = new StatusController(fsuipcFlightSimInterface);
    }

    @Test
    public void shouldReturnStatusOK() throws Exception {
        // when
        when(fsuipcFlightSimInterface.isConnectionEstablished()).thenReturn(true);
        // then
        Response response = statusController.getStatus();
        assertEquals(200, response.getStatus());
        assertEquals("application/x-www-form-urlencoded", response.getMediaType().toString());
        assertEquals("status=OPEN", response.getEntity().toString());
    }

    @Test
    public void shouldReturnStatusError() throws Exception {
        // when
        when(fsuipcFlightSimInterface.isConnectionEstablished()).thenReturn(false);
        // then
        Response response = statusController.getStatus();
        assertEquals(200, response.getStatus());
        assertEquals("application/x-www-form-urlencoded", response.getMediaType().toString());
        assertEquals("status=CLOSED", response.getEntity());
    }

}
