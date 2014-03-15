package de.newsarea.homecockpit.fsuipc.flightsim;

import de.newsarea.homecockpit.fsuipc.domain.ByteArray;
import de.newsarea.homecockpit.fsuipc.domain.OffsetItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class FSUIPCFlightSimOffsetInterface {

    private static final Logger log = LoggerFactory.getLogger(FSUIPCFlightSimInterface.class);

    private int offset;
    private FSUIPCFlightSimWrapper fsuipcFlightSimWrapper;
    private ReadWriteLock readWriteLock;

    public FSUIPCFlightSimOffsetInterface(int offset, FSUIPCFlightSimWrapper fsuipcFlightSimWrapper) {
        this.offset = offset;
        this.fsuipcFlightSimWrapper = fsuipcFlightSimWrapper;
        this.readWriteLock = new ReentrantReadWriteLock();
    }

    public synchronized void write(int size, ByteArray value, int timeOfBlocking) throws IOException {
        readWriteLock.writeLock().lock();
        fsuipcFlightSimWrapper.write(offset, size, value.toLittleEndian());
        readWriteLock.writeLock().unlock();
        if(timeOfBlocking > 0) {
            try {
                TimeUnit.MILLISECONDS.sleep(timeOfBlocking);
            } catch (InterruptedException e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    public OffsetItem read(int size) throws IOException {
        readWriteLock.readLock().lock();
        byte[] data = fsuipcFlightSimWrapper.read(offset, size);
        readWriteLock.readLock().unlock();
        ByteArray byteArray = ByteArray.create(data, true);
        return new OffsetItem(offset, size, byteArray);
    }


}
