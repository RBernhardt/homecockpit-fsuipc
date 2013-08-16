package de.newsarea.homecockpit.fsuipc.example.net;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import de.newsarea.homecockpit.fsuipc.FSUIPCInterface;
import de.newsarea.homecockpit.fsuipc.domain.ByteArray;
import de.newsarea.homecockpit.fsuipc.domain.OffsetIdent;
import de.newsarea.homecockpit.fsuipc.domain.OffsetItem;
import de.newsarea.homecockpit.fsuipc.event.OffsetCollectionEventListener;
import de.newsarea.homecockpit.fsuipc.event.OffsetEventListener;
import org.apache.commons.lang3.event.EventListenerSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigInteger;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FSUIPCKryonetInterface implements FSUIPCInterface {

    public static final String REGEX_ITEM = "0x([A-F0-9]{1,4}):([0-9]+)(?::0x((?:[A-F0-9][A-F0-9])+))?";

    private static final Logger log = LoggerFactory.getLogger(FSUIPCKryonetInterface.class);

    private final EventListenerSupport<OffsetEventListener> offsetEventListeners;
    private final EventListenerSupport<OffsetCollectionEventListener> offsetCollectionEventListeners;


    private final String server;
    private final int port;
    private final Client client;

    public FSUIPCKryonetInterface(String server, int port) {
        this.server = server;
        this.port = port;
        // ~
        client = new Client();
        client.start();
        client.addListener(new Listener() {
            public void received (Connection connection, Object object) {
                if(object instanceof String) {
                    String message = (String)object;
                    if(message.startsWith("CHANGED")) {
                        Collection<OffsetItem> offsetItems = toOffsetItems(message);
                        for(OffsetItem offsetItem : offsetItems) {
                            offsetEventListeners.fire().valueChanged(offsetItem);
                        }
                        offsetCollectionEventListeners.fire().valuesChanged(offsetItems);
                    }
                }
            }
        });
        // ~
        offsetEventListeners = EventListenerSupport.create(OffsetEventListener.class);
        offsetCollectionEventListeners = EventListenerSupport.create(OffsetCollectionEventListener.class);
    }

    @Override
    public void open() throws ConnectException {
        log.info("open client {}:{}", server, port);
        try {
            client.connect(5000, server, port);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new ConnectException(e.getMessage());
        }
    }

    @Override
    public void monitor(OffsetIdent offsetIdent) {
        StringBuilder strBld = new StringBuilder();
        strBld.append("MONITOR");
        strBld.append("[[");
        strBld.append(ByteArray.create(String.valueOf(offsetIdent.getOffset()), 2).toHexString());
        strBld.append(":");
        strBld.append(offsetIdent.getSize());
        strBld.append("]]");
        // ~
        client.sendTCP(strBld.toString());
    }

    @Override
    public void write(OffsetItem[] offsetItems) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void write(OffsetItem offsetItem) {
        throw new UnsupportedOperationException();
    }

    @Override
    public OffsetItem read(OffsetIdent offsetIdent) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void close() {
        client.close();
    }

    @Override
    public void addEventListener(OffsetEventListener offsetEventListener) {
        offsetEventListeners.addListener(offsetEventListener);
    }

    @Override
    public void addEventListener(OffsetCollectionEventListener offsetCollectionEventListener) {
        offsetCollectionEventListeners.addListener(offsetCollectionEventListener);
    }

    /* HELPER */

    private static Collection<OffsetItem> toOffsetItems(String message) {
        List<OffsetItem> items = new ArrayList<>();
        Pattern p = Pattern.compile(REGEX_ITEM);
        Matcher m = p.matcher(message);
        while(m.find()) {
            int offset = Integer.parseInt(m.group(1), 16);
            int size = Integer.parseInt(m.group(2));
            // ~
            String byteArrayHex = m.group(3);
            ByteArray byteArray = ByteArray.create(new BigInteger(byteArrayHex, 16), byteArrayHex.length() / 2);
            items.add(new OffsetItem(offset, size, byteArray));
        }
        return items;
    }


}
