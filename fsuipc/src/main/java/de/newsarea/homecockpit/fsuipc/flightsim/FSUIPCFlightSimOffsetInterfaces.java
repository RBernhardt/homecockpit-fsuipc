package de.newsarea.homecockpit.fsuipc.flightsim;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class FSUIPCFlightSimOffsetInterfaces {

    private static final Logger log = LoggerFactory.getLogger(FSUIPCFlightSimInterface.class);

    private FSUIPCFlightSimWrapper fsuipcFlightSimWrapper;
    private Map<Integer, FSUIPCFlightSimOffsetInterface> fsuipcFlightSimOffsetInterfaceMap;

    public FSUIPCFlightSimOffsetInterfaces(FSUIPCFlightSimWrapper fsuipcFlightSimWrapper) {
        this.fsuipcFlightSimWrapper = fsuipcFlightSimWrapper;
        this.fsuipcFlightSimOffsetInterfaceMap = new HashMap<>();
    }

    public FSUIPCFlightSimOffsetInterface getInterface(int offset) {
        if(!fsuipcFlightSimOffsetInterfaceMap.containsKey(offset)) {
           fsuipcFlightSimOffsetInterfaceMap.put(offset, new FSUIPCFlightSimOffsetInterface(offset, fsuipcFlightSimWrapper));
        }
        return fsuipcFlightSimOffsetInterfaceMap.get(offset);
    }

}
