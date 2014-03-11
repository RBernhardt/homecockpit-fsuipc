package de.newsarea.homecockpit.fsuipc2http.watchdog;

import de.newsarea.homecockpit.fsuipc2http.watchdog.event.ConnectorStateChangedEventListener;
import org.apache.commons.lang3.event.EventListenerSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.*;

public class ConnectorWatchdog {

    private static final Logger log = LoggerFactory.getLogger(ConnectorWatchdog.class);

    private Map<String, MonitorableConnector> monitorableConnectorList;
    private Map<String, ConnectorStateChangedEventListener.State> lastStates;
    private EventListenerSupport<ConnectorStateChangedEventListener> eventListenerEventListenerSupport;
    private Map<String, ScheduledExecutorService> watchdogExecutorServiceList;

    public ConnectorWatchdog() {
        monitorableConnectorList = new ConcurrentHashMap<>();
        lastStates = new ConcurrentHashMap<>();
        eventListenerEventListenerSupport = EventListenerSupport.create(ConnectorStateChangedEventListener.class);
        watchdogExecutorServiceList = new ConcurrentHashMap<>();
    }

    public void stop() {
        for(ScheduledExecutorService scheduledExecutorService : watchdogExecutorServiceList.values()) {
            scheduledExecutorService.shutdown();
            try {
                scheduledExecutorService.awaitTermination(10, TimeUnit.SECONDS);
            } catch (Exception ex) {
                log.error(ex.getMessage(), ex);
            }
        }
    }

    private void watchConnection(String id, MonitorableConnector monitorableConnector) {
        try {
            ConnectorStateChangedEventListener.State lastState = null;
            if(lastStates.containsKey(id)) {
                lastState = lastStates.get(id);
            }
            // ~
            ConnectorStateChangedEventListener.State currentState = ConnectorStateChangedEventListener.State.CLOSED;
            if(monitorableConnector.isAlive()) {
                currentState = ConnectorStateChangedEventListener.State.OPEN;
            }
            // compare states
            if(lastState == null || !currentState.equals(lastState)) {
                lastStates.put(id, currentState);
                // fire state changed event
                eventListenerEventListenerSupport.fire().stateChanged(currentState);
            }
            // try to reconnect
            if(currentState == ConnectorStateChangedEventListener.State.CLOSED) {
                monitorableConnector.reconnect();
            }
        } catch(Exception ex) {
            log.error(ex.getMessage(), ex);
        }
    }

    public void addEventListerner(ConnectorStateChangedEventListener connectorStateChangedEventListener) {
        eventListenerEventListenerSupport.addListener(connectorStateChangedEventListener);
    }

    public void monitorConnector(final String id, final MonitorableConnector monitorableConnector) {
        monitorableConnectorList.put(id, monitorableConnector);
        // ~
        ScheduledExecutorService watchdogExecutorService = Executors.newScheduledThreadPool(1);
        watchdogExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                watchConnection(id, monitorableConnector);
            }
        }, 0, 1, TimeUnit.SECONDS);
        // ~
        watchdogExecutorServiceList.put(id, watchdogExecutorService);
    }

    public boolean hasConnector(String id) {
        return monitorableConnectorList.containsKey(id);
    }

}
