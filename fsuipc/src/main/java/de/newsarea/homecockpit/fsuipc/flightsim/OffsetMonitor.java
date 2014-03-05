package de.newsarea.homecockpit.fsuipc.flightsim;

import de.newsarea.homecockpit.fsuipc.domain.ByteArray;
import de.newsarea.homecockpit.fsuipc.domain.OffsetIdent;
import de.newsarea.homecockpit.fsuipc.domain.OffsetItem;
import de.newsarea.homecockpit.fsuipc.event.OffsetCollectionEventListener;
import de.newsarea.homecockpit.fsuipc.event.OffsetEventListener;
import org.apache.commons.lang3.event.EventListenerSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class OffsetMonitor {

    private static final Logger log = LoggerFactory.getLogger(OffsetMonitor.class);

    private final FSUIPCFlightSimInterface fsuipcFlightSimInterface;
    private final Map<String, OffsetIdent> monitorOffsetList;
    private final EventListenerSupport<OffsetEventListener> offsetEventListeners;
    private final EventListenerSupport<OffsetCollectionEventListener> offsetCollectionEventListeners;

    private ByteArray[] offsetValues;
    private ScheduledExecutorService scheduledExecutorService;

    public OffsetMonitor(FSUIPCFlightSimInterface fsuipcFlightSimInterface) {
        this.fsuipcFlightSimInterface = fsuipcFlightSimInterface;
        this.monitorOffsetList = new ConcurrentHashMap<>();
        this.offsetEventListeners = EventListenerSupport.create(OffsetEventListener.class);
        this.offsetCollectionEventListeners = EventListenerSupport.create(OffsetCollectionEventListener.class);
    }

    public void open() {
        if(scheduledExecutorService != null) {
            throw new IllegalStateException("scheduledExecutorService is running");
        }
        // reset offset values
        offsetValues = new ByteArray[0x6000];
        //
        scheduledExecutorService = Executors.newScheduledThreadPool(1);
        scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                readOffsetChanges();
            }
        },
        1,
        1,
        TimeUnit.MICROSECONDS);
    }

    public void close() {
        if(scheduledExecutorService != null) {
            scheduledExecutorService.shutdown();
            try {
                scheduledExecutorService.awaitTermination(10, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                log.error(e.getMessage(), e);
            }
            scheduledExecutorService = null;
        }
    }

    private void readOffsetChanges() {
        if(monitorOffsetList.values().size() == 0) { return; }
        // only execute if monitor offset list is not empty
        try {
            List<OffsetItem> offsetItemGroup = new ArrayList<>();
            // iterate monitor offsets
            for (OffsetIdent monitorOffsetIdent : monitorOffsetList.values()) {
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
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public void monitorOffset(OffsetIdent offsetIdent) {
        if(!monitorOffsetList.containsKey(offsetIdent.getIdentifier())) {
            log.debug("monitor offset - " + offsetIdent.toString());
            monitorOffsetList.put(offsetIdent.getIdentifier(), offsetIdent);
        }
    }

    public void addEventListener(OffsetEventListener offsetEventListener) {
        offsetEventListeners.addListener(offsetEventListener);
    }

    public void addEventListener(OffsetCollectionEventListener offsetCollectionEventListener) {
        offsetCollectionEventListeners.addListener(offsetCollectionEventListener);
    }

}
