package de.newsarea.homecockpit.fsuipc2http.controller;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.newsarea.homecockpit.fsuipc.domain.OffsetItem;
import de.newsarea.homecockpit.fsuipc.event.OffsetCollectionEventListener;
import de.newsarea.homecockpit.fsuipc.flightsim.FSUIPCFlightSimInterface;
import org.glassfish.grizzly.http.HttpRequestPacket;
import org.glassfish.grizzly.websockets.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

public class EventsWebSocketApplication extends WebSocketApplication {

    private static final Logger log = LoggerFactory.getLogger(EventsWebSocketApplication.class);

    private FSUIPCFlightSimInterface fsuipcFlightSimInterface;

    private SimpleWebSocket simpleWebSocket;

    public EventsWebSocketApplication(FSUIPCFlightSimInterface fsuipcFlightSimInterface) {
        this.fsuipcFlightSimInterface = fsuipcFlightSimInterface;
        this.fsuipcFlightSimInterface.addEventListener(new OffsetCollectionEventListener() {
            @Override
            public void valuesChanged(Collection<OffsetItem> offsetItemCollection) {
                broadcast(offsetItemCollection);
            }
        });
    }

    @Override
    public WebSocket createSocket(ProtocolHandler handler,
                                  HttpRequestPacket request,
                                  WebSocketListener... listeners) {
        simpleWebSocket = new SimpleWebSocket(handler, listeners);
        return simpleWebSocket;
    }

    private void broadcast(Collection<OffsetItem> offsetItemCollection) {
        if(simpleWebSocket == null) { return; }
        if(!simpleWebSocket.isConnected()) { return; }
        //
        JsonArray jsonArray = new JsonArray();
        for(OffsetItem offsetItem : offsetItemCollection) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("offset", offsetItem.getOffsetHexString());
            jsonObject.addProperty("size", offsetItem.getSize());
            jsonObject.addProperty("data", offsetItem.getValue().toHexString());
            jsonArray.add(jsonObject);
        }
        //
        Gson gson = new Gson();
        simpleWebSocket.broadcast(getWebSockets(), gson.toJson(jsonArray));
    }

}
