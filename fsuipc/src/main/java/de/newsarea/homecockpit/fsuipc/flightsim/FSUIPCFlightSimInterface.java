package de.newsarea.homecockpit.fsuipc.flightsim;

import de.newsarea.homecockpit.fsuipc.FSUIPCInterface;
import de.newsarea.homecockpit.fsuipc.domain.ByteArray;
import de.newsarea.homecockpit.fsuipc.domain.OffsetIdent;
import de.newsarea.homecockpit.fsuipc.domain.OffsetItem;
import de.newsarea.homecockpit.fsuipc.event.OffsetEventListener;
import org.apache.commons.lang3.event.EventListenerSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.ConnectException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

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
	private final MonitorOffsetThread monitorOffsetThread;

    FSUIPCFlightSimInterface(FSUIPCFlightSimWrapper fsuipcFlightSimWrapper) {
        this.fsuipcFlightSimWrapper = fsuipcFlightSimWrapper;
        this.monitorOffsetThread = new FSUIPCFlightSimInterface.MonitorOffsetThread(this);
    }

	public void open() throws ConnectException {
        try {
		    fsuipcFlightSimWrapper.open();
		    monitorOffsetThread.start();
        } catch(Exception ex) {
            throw new ConnectException(ex.getMessage());
        }
	}

    @Override
	public void monitor(OffsetIdent offsetIdent) {
        monitorOffsetThread.monitorOffset(offsetIdent);
	}
	
	public void write(OffsetItem[] offsetItems) {
		int firstOffset = offsetItems[0].getOffset();
        ByteArray byteArray = ByteArray.create(createByteArray(offsetItems));
        // ~
        OffsetItem offsetItem = new OffsetItem(firstOffset, byteArray.getSize(), byteArray);
		log.debug(offsetItem.toString());
        write(offsetItem);
	}
	
	public void write(OffsetItem offsetItem) {
		log.debug("write offset item - " + offsetItem);
		fsuipcFlightSimWrapper.write(offsetItem.getOffset(), offsetItem.getSize(), offsetItem.getValue().toLittleEndian());
		log.debug("offset item written - " + offsetItem);
	}

	public OffsetItem read(OffsetIdent offsetIdent) {
		byte[] data = fsuipcFlightSimWrapper.read(offsetIdent.getOffset(), offsetIdent.getSize());
        ByteArray byteArray = ByteArray.create(data, true);
        return new OffsetItem(offsetIdent.getOffset(), offsetIdent.getSize(), byteArray);
	}

	public void close() {
		monitorOffsetThread.exit();
		try {
			monitorOffsetThread.join();
		} catch (InterruptedException e) {
			log.error(e.getMessage(), e);
		}
		fsuipcFlightSimWrapper.close();
	}

	public void addEventListener(OffsetEventListener offsetEventListener) {
		monitorOffsetThread.addEventListener(offsetEventListener);
	}

	/* */

	private static class MonitorOffsetThread extends Thread implements EventListener {

		private final FSUIPCFlightSimInterface fsuipcFlightSimInterface;

		private final Map<String, OffsetIdent> monitorOffsetList;
		private final Map<Integer, ByteArray> offsetValues;
        private final EventListenerSupport<OffsetEventListener> offsetEventListeners;

		private boolean exit = false;

		public MonitorOffsetThread(FSUIPCFlightSimInterface fsuipcFlightSimInterface) {
			this.fsuipcFlightSimInterface = fsuipcFlightSimInterface;
			this.monitorOffsetList = new ConcurrentHashMap<>();
			this.offsetValues = new HashMap<>();
            this.offsetEventListeners = EventListenerSupport.create(OffsetEventListener.class);
        }

		public void run() {
			try {
				while (!exit) {
                    List<OffsetItem> offsetItemGroup = new ArrayList<>();
                    // iterate monitor offsets
                    for (OffsetIdent monitorOffsetIdent : monitorOffsetList.values()) {
                        if (exit) {
                            break;
                        }
                        //
                        int mOffset = monitorOffsetIdent.getOffset();
                        final OffsetItem newOffsetItem = fsuipcFlightSimInterface.read(new OffsetIdent(mOffset, monitorOffsetIdent.getSize()));
                        // determine old offset value
                        ByteArray oldOffsetValue = null;
                        if (offsetValues.containsKey(mOffset)) {
                            oldOffsetValue = offsetValues.get(mOffset);
                        }
                        // send new offset value
                        if (!newOffsetItem.getValue().equals(oldOffsetValue)) {
                            offsetItemGroup.add(newOffsetItem);
                            offsetEventListeners.fire().offsetValueChanged(newOffsetItem);
                        }
                        // save new offset value
                        if (offsetValues.containsKey(mOffset)) {
                            offsetValues.remove(mOffset);
                        }
                        offsetValues.put(mOffset, newOffsetItem.getValue());
                    }
                    //
                    if(offsetItemGroup.size() > 0) {
                        offsetEventListeners.fire().offsetValuesChanged(offsetItemGroup);
                    }
                    // ~
					Thread.sleep(0, 1);
				}
			} catch (InterruptedException e) {
				log.error(e.getMessage(), e);
			}
		}

		public void monitorOffset(OffsetIdent offsetIdent) {
            if(!monitorOffsetList.containsKey(offsetIdent.getIdentifier())) {
                log.debug("monitor offset - " + offsetIdent.toString());
                monitorOffsetList.put(offsetIdent.getIdentifier(), offsetIdent);
            }
		}

		public void exit() {
			exit = true;
		}

		public void addEventListener(OffsetEventListener offsetEventListener) {
			offsetEventListeners.addListener(offsetEventListener);
		}

	}

	/* HELPER */
	
	private byte[] createByteArray(OffsetItem[] offsetItems) {	 
		int currentOffset = offsetItems[0].getOffset();		
		OffsetItem lastOffsetItem  = offsetItems[offsetItems.length - 1];
		int lastOffset = lastOffsetItem.getOffset() + lastOffsetItem.getSize();		
		//
		byte[] output = new byte[lastOffset - currentOffset];
		for(OffsetItem offsetItem : offsetItems) {
			if(offsetItem.getOffset() != currentOffset) {
				throw new IllegalArgumentException("expected offset was " + currentOffset + " but was " + offsetItem.getOffset());
			}	
			//
			for(int j=0; j < offsetItem.getValue().getSize(); j++) {
				int lOffset = lastOffset - currentOffset - offsetItem.getSize();
				if(lOffset < 0) {
					throw new IllegalArgumentException("invalid first or last offset item detected - " + offsetItems[0].getOffset() + " : " + lastOffset + " : " + output.length);
				}
				output[lOffset + j] = offsetItem.getValue().get(j);
			}
			//
			currentOffset += offsetItem.getSize();
		}
		return output;
	}
	
}
