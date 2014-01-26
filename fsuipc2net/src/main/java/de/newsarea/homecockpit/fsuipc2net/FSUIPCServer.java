package de.newsarea.homecockpit.fsuipc2net;

import de.newsarea.homecockpit.fsuipc.FSUIPCInterface;
import de.newsarea.homecockpit.fsuipc.domain.OffsetItem;
import de.newsarea.homecockpit.fsuipc.event.OffsetCollectionEventListener;
import de.newsarea.homecockpit.fsuipc2net.net.NetServer;
import de.newsarea.homecockpit.fsuipc2net.net.domain.Client;
import de.newsarea.homecockpit.fsuipc2net.net.domain.NetMessage;
import de.newsarea.homecockpit.fsuipc2net.net.domain.NetMessageItem;
import de.newsarea.homecockpit.fsuipc2net.net.event.ServerEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;

public class FSUIPCServer {
	
	private static final Logger log = LoggerFactory.getLogger(FSUIPCServer.class);

	private NetServer netServer;
	private FSUIPCInterface fsuipcInterface;
    private ClientRegistry clientRegistry;

    public FSUIPCServer(final NetServer netServer, FSUIPCInterface fsuipcInterface, final ClientRegistry clientRegistry) {
        this.netServer = netServer;
        this.fsuipcInterface = fsuipcInterface;
        this.clientRegistry = clientRegistry;
        // ~
        netServer.addEventListener(new ServerEventListener() {
            @Override
            public void clientConneted(Client client) {
                log.info("client connected - {}", client);
            }

            @Override
            public void clientDisconnected(Client client) {
                log.info("client disconnected - {}", client);
                try {
                    clientRegistry.deregisterClientForOffsetEvent(client);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }

            @Override
            public void valueReceived(Client client, NetMessage message) {
                try {
                    log.debug("NetServer.valueReceived - {} - {}", client, message);
                    handleNetServerInput(client, message);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
        });
        // ~
        fsuipcInterface.addEventListener(new OffsetCollectionEventListener() {
            @Override
            public void valuesChanged(Collection<OffsetItem> offsetItemCollection) {
                try {
                    log.debug("FSUIPCInterface.valuesChanged - {}", offsetItemCollection);
                    for(Client client : clientRegistry.getClients()) {
                        Collection<NetMessageItem> netMessageItems = clientRegistry.filterForClient(client, offsetItemCollection);
                        handleFSUIPCInput(client, new NetMessage(NetMessage.Command.CHANGED, netMessageItems));
                    }
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
        });
    }
	
	public void start() {
		log.info("net port: " + netServer.getPort());
		try {
			netServer.start();
			fsuipcInterface.open();
		} catch(Exception e) {
			log.error(e.getMessage(), e);
			stop();
		}
        log.info("application started - " + new Date());
	}
	
	public void stop() {
		try {
			netServer.stop();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		//
		try {
			fsuipcInterface.close();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		//
		log.info("application stopped - " + new Date());
	}

    private void handleFSUIPCInput(Client client, NetMessage message) throws IOException {
        netServer.write(client, message);
    }

    private void handleNetServerInput(Client client, NetMessage message) {
        switch(message.getCommand()) {
            case WRITE:
                fsuipcInterface.write(message.getOffsetItems());
                break;
            case MONITOR:
                for(NetMessageItem item : message.getItems()) {
                    clientRegistry.registerClientForOffsetEvent(client, item.getOffsetIdent());
                    fsuipcInterface.monitor(item.getOffsetIdent());
                }
                break;
            case READ:
                for(NetMessageItem netMessageItem : message.getItems()) {
                    OffsetItem offsetItem = fsuipcInterface.read(netMessageItem.getOffsetIdent());
                    try {
                        netServer.write(client, new NetMessage(NetMessage.Command.VALUE, offsetItem));
                    } catch (IOException e) {
                        log.error(e.getMessage(), e);
                    }
                }
                break;
            default:
                throw new IllegalArgumentException("invalid message - " + message);

        }
    }

}
