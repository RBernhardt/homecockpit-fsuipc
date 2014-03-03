package de.newsarea.homecockpit.fsuipc.flightsim;

import de.newsarea.homecockpit.fsuipc.FSUIPCInterface;
import de.newsarea.homecockpit.fsuipc.domain.ByteArray;
import de.newsarea.homecockpit.fsuipc.domain.OffsetIdent;
import de.newsarea.homecockpit.fsuipc.domain.OffsetItem;
import de.newsarea.homecockpit.fsuipc.event.OffsetCollectionEventListener;
import de.newsarea.homecockpit.fsuipc.event.OffsetEventListener;
import org.apache.commons.lang3.event.EventListenerSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;
import java.util.Map;
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

    @Override
    public void addEventListener(OffsetCollectionEventListener offsetCollectionEventListener) {
        monitorOffsetThread.addEventListener(offsetCollectionEventListener);
    }

	/* */

	private static class MonitorOffsetThread extends Thread implements EventListener {

		private final FSUIPCFlightSimInterface fsuipcFlightSimInterface;

		private final Map<String, OffsetIdent> monitorOffsetList;
        private final ByteArray[] offsetValues;
        private final EventListenerSupport<OffsetEventListener> offsetEventListeners;
        private final EventListenerSupport<OffsetCollectionEventListener> offsetCollectionEventListeners;

		private boolean exit = false;

		public MonitorOffsetThread(FSUIPCFlightSimInterface fsuipcFlightSimInterface) {
			this.fsuipcFlightSimInterface = fsuipcFlightSimInterface;
			this.monitorOffsetList = new ConcurrentHashMap<>();
			this.offsetValues = new ByteArray[0x6000];
            this.offsetEventListeners = EventListenerSupport.create(OffsetEventListener.class);
            this.offsetCollectionEventListeners = EventListenerSupport.create(OffsetCollectionEventListener.class);
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
                        ByteArray oldOffsetValue = offsetValues[mOffset];
                        // send new offset value
                        if (!newOffsetItem.getValue().equals(oldOffsetValue)) {
                            offsetItemGroup.add(newOffsetItem);
                            offsetEventListeners.fire().valueChanged(newOffsetItem);
                        }
                        // save new offset value
                        offsetValues[mOffset] = newOffsetItem.getValue();
                    }
                    //
                    if(offsetItemGroup.size() > 0) {
                        offsetCollectionEventListeners.fire().valuesChanged(offsetItemGroup);
                    }
                    // ~
					Thread.sleep(0, 1);
				}
			} catch (InterruptedException e) {
				log.error(e.getMessage(), e);
			}
		}

        @Override
        public void start() {
            this.exit = false;
            super.start();
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

        public void addEventListener(OffsetCollectionEventListener offsetCollectionEventListener) {
            offsetCollectionEventListeners.addListener(offsetCollectionEventListener);
        }

	}
	
}
