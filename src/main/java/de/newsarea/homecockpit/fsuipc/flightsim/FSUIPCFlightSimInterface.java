package de.newsarea.homecockpit.fsuipc.flightsim;

import de.newsarea.homecockpit.fsuipc.FSUIPCInterface;
import de.newsarea.homecockpit.fsuipc.domain.OffsetIdent;
import de.newsarea.homecockpit.fsuipc.domain.OffsetItem;
import de.newsarea.homecockpit.fsuipc.event.OffsetEventListener;
import de.newsarea.homecockpit.fsuipc.util.DataTypeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.ConnectException;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class FSUIPCFlightSimInterface implements FSUIPCInterface {

	private static Logger log = LoggerFactory.getLogger(FSUIPCFlightSimInterface.class);

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

	private FSUIPCFlightSimWrapper fsuipcFlightSimWrapper;
	private MonitorOffsetThread monitorOffsetThread;

    FSUIPCFlightSimInterface(FSUIPCFlightSimWrapper fsuipcFlightSimWrapper) {
        this.fsuipcFlightSimWrapper = fsuipcFlightSimWrapper;
        this.monitorOffsetThread = new FSUIPCFlightSimInterface.MonitorOffsetThread(this);
    }

	public boolean isConnected() {
		return this.monitorOffsetThread.isAlive();
	}

	public void open() throws ConnectException {
		this.fsuipcFlightSimWrapper.open();
		this.monitorOffsetThread.start();
	}
	
	public void monitor(OffsetIdent[] offsetIdents) {
		for(OffsetIdent offsetIdent : offsetIdents) {
			this.monitorOffsetThread.monitorOffset(offsetIdent);
		}
	}
	
	public void monitor(OffsetIdent offsetIdent) {
		this.monitor(new OffsetIdent[] { offsetIdent });
	}
	
	public void write(OffsetItem[] offsetItems) {
		int firstOffset = offsetItems[0].getOffset();
		byte[] data = this.createByteArray(offsetItems);
		log.debug(firstOffset + " : " + data.length + " : " + DataTypeUtil.toHexString(data));
		this.write(new OffsetItem(firstOffset, data.length, data));
	}
	
	public void write(OffsetItem offsetItem) {
		byte[] data = DataTypeUtil.toLittleEndian(offsetItem.getValue());
		log.debug("write offset item - " + offsetItem);
		this.fsuipcFlightSimWrapper.write(offsetItem.getOffset(), offsetItem.getSize(), data);
		log.debug("offset item written - " + offsetItem);
	}

	public byte[] read(OffsetIdent offsetIdent) {		
		byte[] data = this.fsuipcFlightSimWrapper.read(offsetIdent.getOffset(), offsetIdent.getSize());
		return DataTypeUtil.toLittleEndian(data);
	}

	public void close() {
		this.monitorOffsetThread.exit();
		try {
			this.monitorOffsetThread.join();
		} catch (InterruptedException e) {
			log.error(e.getMessage(), e);
		}
		this.fsuipcFlightSimWrapper.close();
	}

	public void addEventListener(OffsetEventListener offsetEventListener) {
		this.monitorOffsetThread.addEventListener(offsetEventListener);
	}

	/* */

	private static class MonitorOffsetThread extends Thread implements EventListener {

		private final ReadWriteLock rwLock = new ReentrantReadWriteLock();
		private final Lock readLock = rwLock.readLock();
		private final Lock writeLock = rwLock.writeLock();

		private List<OffsetEventListener> eventListeners = new ArrayList<>();

		private FSUIPCFlightSimInterface fsuipcFlightSimInterface;
		private Map<String, OffsetIdent> monitorOffsetList;
		private Map<Integer, byte[]> offsetValues;
		private boolean exit = false;

		public MonitorOffsetThread(FSUIPCFlightSimInterface fsuipcFlightSimInterface) {
			this.fsuipcFlightSimInterface = fsuipcFlightSimInterface;
			this.monitorOffsetList = new HashMap<>();
			this.offsetValues = new HashMap<>();
		}

		public void run() {
			try {
				while (!this.exit) {
					// lock mutex
					this.readLock.lock();
					//
					try {
						// iterate monitor offsets
						for (OffsetIdent monitorOffsetIdent : this.monitorOffsetList.values()) {
							if (this.exit) {
								break;
							}
							//
							int mOffset = monitorOffsetIdent.getOffset();
							byte[] newOffsetValue = this.fsuipcFlightSimInterface.read(new OffsetIdent(mOffset, monitorOffsetIdent.getSize()));
							// determine old offset value
							byte[] oldOffsetValue = null;
							if (this.offsetValues.containsKey(mOffset)) {
								oldOffsetValue = this.offsetValues.get(mOffset);
							}
							// send new offset value
							if (!DataTypeUtil.isEquals(newOffsetValue, oldOffsetValue)) {
								OffsetItem oItem = new OffsetItem(mOffset, monitorOffsetIdent.getSize(), newOffsetValue);
								this.fireEvent(oItem);
							}
							// save new offset value
							if (this.offsetValues.containsKey(mOffset)) {
								this.offsetValues.remove(mOffset);
							}
							this.offsetValues.put(mOffset, newOffsetValue);
						}
					} finally {
						// unlock mutex
						this.readLock.unlock();
					}
					//
					Thread.sleep(1);
				}
			} catch (InterruptedException e) {
				log.error(e.getMessage(), e);
			}
		}

		public void monitorOffset(OffsetIdent offsetIdent) {
			this.writeLock.lock();
			try {
				if(!this.monitorOffsetList.containsKey(offsetIdent.toString())) {
					log.debug("monitor offset - " + offsetIdent.toString());
					this.monitorOffsetList.put(offsetIdent.toString(), offsetIdent);
				}
			} finally { 
				this.writeLock.unlock();
			}
		}

		public void exit() {
			this.exit = true;
		}

		public void addEventListener(OffsetEventListener offsetEventListener) {
			this.eventListeners.add(offsetEventListener);
		}

		private void fireEvent(OffsetItem offsetItem) {
			if (this.eventListeners.size() == 0) {
				return;
			}
			for (OffsetEventListener eventListener : this.eventListeners) {
				eventListener.offsetValueChanged(offsetItem);
			}
		}

	}

	/* HELPER */
	
	private byte[] createByteArray(OffsetItem[] offsetItems) {	 
		int currentOffset = offsetItems[0].getOffset();		
		OffsetItem lastOffsetItem  = offsetItems[offsetItems.length - 1];
		int lastOffset = lastOffsetItem.getOffset() + lastOffsetItem.getSize();		
		//
		byte[] output = new byte[lastOffset - currentOffset];
		for(int i=0; i < offsetItems.length; i++) {
			OffsetItem offsetItem = offsetItems[i];
			if(offsetItem.getOffset() != currentOffset) {
				throw new IllegalArgumentException("expected offset was " + currentOffset + " but was " + offsetItem.getOffset());
			}	
			//
			for(int j=0; j < offsetItem.getValue().length; j++) {
				int lOffset = lastOffset - currentOffset - offsetItem.getSize();
				if(lOffset < 0) {
					throw new IllegalArgumentException("invalid first or last offset item detected - " + offsetItems[0].getOffset() + " : " + lastOffset + " : " + output.length);
				}
				output[lOffset + j] = offsetItem.getValue()[j];
			}
			//
			currentOffset += offsetItem.getSize();
		}
		return output;
	}
	
}
