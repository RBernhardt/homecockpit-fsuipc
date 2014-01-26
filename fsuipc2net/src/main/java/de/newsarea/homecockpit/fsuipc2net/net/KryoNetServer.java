package de.newsarea.homecockpit.fsuipc2net.net;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import de.newsarea.homecockpit.fsuipc2net.net.domain.Client;
import de.newsarea.homecockpit.fsuipc2net.net.domain.NetMessage;
import de.newsarea.homecockpit.fsuipc2net.net.event.ServerEventListener;
import org.apache.commons.lang3.event.EventListenerSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ConnectException;

public class KryoNetServer implements NetServer {

	private static final Logger log = LoggerFactory.getLogger(KryoNetServer.class);

    private EventListenerSupport<ServerEventListener> eventListeners = EventListenerSupport.create(ServerEventListener.class);

    private int port;
    private Server server;

	public KryoNetServer(int port) {
        this.port = port;
    }

    @Override
    public int getPort() {
        return port;
    }

    @Override
	public void start() throws ConnectException {
        log.info("open server on port: {} ", port);
        try {
            server = new Server();
            server.start();
            server.addListener(new Listener() {
                @Override
                public void received (Connection connection, Object object) {
                    if(object instanceof String) {
                        Client client = new Client(String.valueOf(connection.getID()));
                        NetMessage message = NetMessage.fromJson((String)object);
                        eventListeners.fire().valueReceived(client, message);
                    }
                }

                @Override
                public void connected(Connection connection) {
                    Client client = new Client(String.valueOf(connection.getID()));
                    eventListeners.fire().clientConneted(client);
                }

                @Override
                public void disconnected(Connection connection) {
                    Client client = new Client(String.valueOf(connection.getID()));
                    eventListeners.fire().clientDisconnected(client);
                }
            });
            server.bind(port);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new ConnectException(e.getMessage());
        }
    }

    @Override
	public void write(Client client, NetMessage message) throws IOException {
        int intClientId = Integer.parseInt(client.getId());
        server.sendToTCP(intClientId, message.toJsonString());
	}

    @Override
    public void write(NetMessage message) throws IOException {
        server.sendToAllTCP(message.toString());
    }

    @Override
    public void addEventListener(ServerEventListener valueEventListener) {
        eventListeners.addListener(valueEventListener);
    }

    @Override
	public void stop() {
        server.close();
	}

    @Override
    public String toString() {
        return "KryoNetServer{" +
                "server=" + server +
                '}';
    }

}
