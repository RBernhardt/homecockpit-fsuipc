package de.newsarea.homecockpit.fsuipc2http.watchdog;

import de.newsarea.homecockpit.fsuipc2http.watchdog.event.ConnectorStateChangedEventListener;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertTrue;

public class ConnectorWatchdogTest {

    private ConnectorWatchdog connectorWatchdog;

    @BeforeMethod
    public void setUp() throws Exception {
        connectorWatchdog = new ConnectorWatchdog();
    }

    @Test
    public void shouldAddMonitorableConnector() throws Exception {
        assertFalse(connectorWatchdog.hasConnector("CONNECTOR1"));
        connectorWatchdog.monitorConnector("CONNECTOR1", mock(MonitorableConnector.class));
        assertTrue(connectorWatchdog.hasConnector("CONNECTOR1"));
    }

    @Test
    public void shouldFireOpenEvent() throws Exception {
        ConnectorStateChangedEventListener connectorStateChangedEventListener = mock(ConnectorStateChangedEventListener.class);
        connectorWatchdog.addEventListerner(connectorStateChangedEventListener);
        // when
        MonitorableConnector monitorableConnector = mock(MonitorableConnector.class);
        when(monitorableConnector.isAlive()).thenReturn(true);
        connectorWatchdog.monitorConnector("CONNECTOR1", monitorableConnector);
        Thread.sleep(1);
        // then
        verify(connectorStateChangedEventListener).stateChanged("CONNECTOR1", eq(ConnectorStateChangedEventListener.State.OPEN));
        // close
        connectorWatchdog.stop();
    }

    @Test
    public void shouldFireCloseEvent() throws Exception {
        ConnectorStateChangedEventListener connectorStateChangedEventListener = mock(ConnectorStateChangedEventListener.class);
        connectorWatchdog.addEventListerner(connectorStateChangedEventListener);
        // when
        MonitorableConnector monitorableConnector = mock(MonitorableConnector.class);
        when(monitorableConnector.isAlive()).thenReturn(false);
        connectorWatchdog.monitorConnector("CONNECTOR1", monitorableConnector);
        Thread.sleep(1);
        // then
        verify(connectorStateChangedEventListener).stateChanged("CONNECTOR1", eq(ConnectorStateChangedEventListener.State.CLOSED));
        // close
        connectorWatchdog.stop();
    }

    @Test
    public void shouldNotTryToReconnect() throws Exception {
        MonitorableConnector monitorableConnector = mock(MonitorableConnector.class);
        when(monitorableConnector.isAlive()).thenReturn(true);
        connectorWatchdog.monitorConnector("CONNECTOR1", monitorableConnector);
        Thread.sleep(1);
        // then
        verify(monitorableConnector, never()).reconnect();
        // close
        connectorWatchdog.stop();
    }

    @Test
    public void shouldTryToReconnect() throws Exception {
        MonitorableConnector monitorableConnector = mock(MonitorableConnector.class);
        when(monitorableConnector.isAlive()).thenReturn(false);
        connectorWatchdog.monitorConnector("CONNECTOR1", monitorableConnector);
        Thread.sleep(1);
        // then
        verify(monitorableConnector).reconnect();
        // close
        connectorWatchdog.stop();
    }

}
