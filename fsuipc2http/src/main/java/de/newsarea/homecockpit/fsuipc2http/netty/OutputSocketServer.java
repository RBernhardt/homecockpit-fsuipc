package de.newsarea.homecockpit.fsuipc2http.netty;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.newsarea.homecockpit.fsuipc.domain.OffsetItem;
import de.newsarea.homecockpit.fsuipc.event.OffsetCollectionEventListener;
import de.newsarea.homecockpit.fsuipc.flightsim.FSUIPCFlightSimInterface;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.concurrent.Executors;

public class OutputSocketServer {

    private final int port;
    private ServerBootstrap bootstrap;
    private TelnetServerHandler telnetServerHandler;

    public OutputSocketServer(FSUIPCFlightSimInterface fsuipcFlightSimInterface, int port) {
        this.port = port;
        // ~
        fsuipcFlightSimInterface.addEventListener(new OffsetCollectionEventListener() {
            @Override
            public void valuesChanged(Collection<OffsetItem> offsetItemCollection) {
                handleValueChanged(offsetItemCollection);
            }
        });
    }

    private void handleValueChanged(Collection<OffsetItem> offsetItemCollection) {
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
        telnetServerHandler.broadcastToAllClients(gson.toJson(jsonArray));
    }

    public void open() {
        // Configure the server.
        bootstrap = new ServerBootstrap(
            new NioServerSocketChannelFactory(
                Executors.newCachedThreadPool(),
                Executors.newCachedThreadPool()
            )
        );
        // create telnet server handler
        telnetServerHandler = new TelnetServerHandler();
        // Configure the pipeline factory.
        bootstrap.setPipelineFactory(new TelnetServerPipelineFactory(telnetServerHandler));

        // Bind and start to accept incoming connections.
        bootstrap.bind(new InetSocketAddress(port));
    }

    public void close() {
        bootstrap.shutdown();
    }

}
