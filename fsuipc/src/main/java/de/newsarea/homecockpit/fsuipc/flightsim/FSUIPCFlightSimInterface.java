package de.newsarea.homecockpit.fsuipc.flightsim;

import de.newsarea.homecockpit.fsuipc.FSUIPCInterface;
import de.newsarea.homecockpit.fsuipc.domain.ByteArray;
import de.newsarea.homecockpit.fsuipc.domain.OffsetIdent;
import de.newsarea.homecockpit.fsuipc.domain.OffsetItem;
import de.newsarea.homecockpit.fsuipc.event.OffsetCollectionEventListener;
import de.newsarea.homecockpit.fsuipc.event.OffsetEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.ConnectException;

public class FSUIPCFlightSimInterface implements FSUIPCInterface {

	private static final Logger log = LoggerFactory.getLogger(FSUIPCFlightSimInterface.class);

    private static FSUIPCFlightSimInterface fsuipcFlightSimInterface;

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
    }

	public void open() throws ConnectException {
        fsuipcFlightSimWrapper.open();
        // validate connection
        if(!isConnectionEstablished()) {
            fsuipcFlightSimWrapper.close();
            throw new ConnectException("can't establish a connection");
        }
        // ~
        offsetMonitor.open();
	}

    public boolean isConnectionEstablished() {
        return fsuipcFlightSimWrapper.isConnectionEstablished();
    }

    @Override
	public void monitor(OffsetIdent offsetIdent) {
        offsetMonitor.monitorOffset(offsetIdent);
	}

    public void write(OffsetItem offsetItem) {
        write(offsetItem, 0);
    }

	public synchronized void write(OffsetItem offsetItem, int timeOfBlocking) {
		log.debug("write offset p {}" + offsetItem);
        fsuipcFlightSimWrapper.write(offsetItem.getOffset(), offsetItem.getSize(), offsetItem.getValue().toLittleEndian());
        if(timeOfBlocking > 0) {
            try {
                Thread.sleep(timeOfBlocking);
            } catch (InterruptedException e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    public OffsetItem read(OffsetIdent offsetIdent) {
		byte[] data = fsuipcFlightSimWrapper.read(offsetIdent.getOffset(), offsetIdent.getSize());
        ByteArray byteArray = ByteArray.create(data, true);
        return new OffsetItem(offsetIdent.getOffset(), offsetIdent.getSize(), byteArray);
	}

	public void close() {
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
