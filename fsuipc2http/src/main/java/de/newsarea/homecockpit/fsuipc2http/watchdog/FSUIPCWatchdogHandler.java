package de.newsarea.homecockpit.fsuipc2http.watchdog;

import de.newsarea.homecockpit.fsuipc.domain.OffsetIdent;
import de.newsarea.homecockpit.fsuipc.domain.OffsetItem;
import de.newsarea.homecockpit.fsuipc.flightsim.FSUIPCFlightSimInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FSUIPCWatchdogHandler implements MonitorableConnector {

    private static final Logger log = LoggerFactory.getLogger(FSUIPCWatchdogHandler.class);

    private FSUIPCFlightSimInterface fsuipcFlightSimInterface;

    public FSUIPCWatchdogHandler(FSUIPCFlightSimInterface fsuipcFlightSimInterface) {
        this.fsuipcFlightSimInterface = fsuipcFlightSimInterface;
    }

    @Override
    public boolean isAlive() {
        OffsetItem offsetItem = fsuipcFlightSimInterface.read(new OffsetIdent(0x0274, 2));
        if(offsetItem != null) {
            short value = offsetItem.getValue().toShort();
            log.info("value={}", value);
            return value > 0;
        }
        return false;
    }

    @Override
    public boolean reconnect() {
        log.info("### try to reconnect");
        return false;
    }
}
