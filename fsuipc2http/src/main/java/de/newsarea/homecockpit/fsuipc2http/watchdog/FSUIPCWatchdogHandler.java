package de.newsarea.homecockpit.fsuipc2http.watchdog;

import de.newsarea.homecockpit.fsuipc.flightsim.FSUIPCFlightSimInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.ConnectException;

public class FSUIPCWatchdogHandler implements MonitorableConnector {

    private static final Logger log = LoggerFactory.getLogger(FSUIPCWatchdogHandler.class);

    private FSUIPCFlightSimInterface fsuipcFlightSimInterface;

    public FSUIPCWatchdogHandler(FSUIPCFlightSimInterface fsuipcFlightSimInterface) {
        this.fsuipcFlightSimInterface = fsuipcFlightSimInterface;
    }

    @Override
    public boolean isAlive() {
        return fsuipcFlightSimInterface.isConnectionEstablished();
    }

    @Override
    public boolean reconnect() {
        log.info("reconnect fsuipcFlightSimInterface");
        fsuipcFlightSimInterface.close();
        try {
            fsuipcFlightSimInterface.open();
            return true;
        } catch (ConnectException e) {
            log.error(e.getMessage(), e);
        }
        return false;
    }
}
