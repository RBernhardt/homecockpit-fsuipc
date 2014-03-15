package de.newsarea.homecockpit.fsuipc.flightsim;

import de.newsarea.homecockpit.fsuipc.FSUIPCInterface;
import de.newsarea.homecockpit.fsuipc.domain.OffsetIdent;
import de.newsarea.homecockpit.fsuipc.domain.OffsetItem;
import de.newsarea.homecockpit.fsuipc.event.OffsetCollectionEventListener;
import de.newsarea.homecockpit.fsuipc.event.OffsetEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ConnectException;

public class FSUIPCFlightSimInterface implements FSUIPCInterface {

	private static final Logger log = LoggerFactory.getLogger(FSUIPCFlightSimInterface.class);

    private static FSUIPCFlightSimInterface fsuipcFlightSimInterface;
    private boolean isOpenConnection = false;

    private FSUIPCFlightSimOffsetInterfaces fsuipcFlightSimOffsetInterfaces;

    /**
     * @return Singleton of FlightSim FSUIPC Interface
     */
    public static FSUIPCFlightSimInterface getInstance() {
        if(fsuipcFlightSimInterface == null) {
            fsuipcFlightSimInterface = new FSUIPCFlightSimInterface(new FSUIPCFlightSimWrapper());
        }
        return fsuipcFlightSimInterface;
    }

	private final FSUIPCFlightSimWrapper fsuipcFlightSimWrapper;
	private final OffsetMonitor offsetMonitor;

    FSUIPCFlightSimInterface(FSUIPCFlightSimWrapper fsuipcFlightSimWrapper) {
        this.fsuipcFlightSimWrapper = fsuipcFlightSimWrapper;
        this.offsetMonitor = new OffsetMonitor(this);
        this.fsuipcFlightSimOffsetInterfaces = new FSUIPCFlightSimOffsetInterfaces(fsuipcFlightSimWrapper);
    }

	public void open() throws ConnectException {
        fsuipcFlightSimWrapper.open();
        // validate connection
        if(!fsuipcFlightSimWrapper.isConnectionEstablished()) {
            fsuipcFlightSimWrapper.close();
            throw new ConnectException("can't establish a connection");
        }
        // ~
        offsetMonitor.open();
        // ~
        isOpenConnection = true;
	}

    public boolean isConnectionEstablished() {
        if(!isOpenConnection) { return false; }
        isOpenConnection = fsuipcFlightSimWrapper.isConnectionEstablished();
        return isOpenConnection;
    }

    @Override
	public void monitor(OffsetIdent offsetIdent) {
        offsetMonitor.monitorOffset(offsetIdent);
	}

    public void write(OffsetItem offsetItem) throws IOException {
        write(offsetItem, 0);
    }

	public void write(OffsetItem offsetItem, int timeOfBlocking) throws IOException {
        if(!isOpenConnection) {
            throw new IOException("connection is not established");
        }
        FSUIPCFlightSimOffsetInterface fsuipcInterface = fsuipcFlightSimOffsetInterfaces.getInterface(offsetItem.getOffset());
        fsuipcInterface.write(offsetItem.getSize(), offsetItem.getValue(), timeOfBlocking);
    }

    public OffsetItem read(OffsetIdent offsetIdent) throws IOException {
        if(!isOpenConnection) {
            throw new IOException("connection is not established");
        }
        FSUIPCFlightSimOffsetInterface fsuipcInterface = fsuipcFlightSimOffsetInterfaces.getInterface(offsetIdent.getOffset());
        return fsuipcInterface.read(offsetIdent.getSize());
    }

	public void close() {
        isOpenConnection = false;
		offsetMonitor.close();
		fsuipcFlightSimWrapper.close();
	}

	public void addEventListener(OffsetEventListener offsetEventListener) {
		offsetMonitor.addEventListener(offsetEventListener);
	}

    @Override
    public void addEventListener(OffsetCollectionEventListener offsetCollectionEventListener) {
        offsetMonitor.addEventListener(offsetCollectionEventListener);
    }
	
}
