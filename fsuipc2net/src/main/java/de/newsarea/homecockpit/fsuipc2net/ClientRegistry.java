package de.newsarea.homecockpit.fsuipc2net;

import de.newsarea.homecockpit.fsuipc.domain.OffsetIdent;
import de.newsarea.homecockpit.fsuipc2net.net.domain.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ClientRegistry {

    private static final Logger log = LoggerFactory.getLogger(ClientRegistry.class);

    private Map<String, List<Client>> offsetMonitoringClientList = new ConcurrentHashMap<>();

    public int getOffsetCount() {
        return offsetMonitoringClientList.size();
    }

    public void registerClientForOffsetEvent(Client client, OffsetIdent offsetIdent) {
        String ident = offsetIdent.getIdentifier();
        List<Client> clients = offsetMonitoringClientList.get(ident);
        if(clients == null) {
            clients = Collections.synchronizedList(new ArrayList<Client>());
            offsetMonitoringClientList.put(ident, clients);
        }
        log.debug("append client ({}) to monitor list for offset {}", client, offsetIdent.getIdentifier());
        clients.add(client);
    }

    public Collection<Client> getClientIdsByOffsetEvent(OffsetIdent offsetIdent) {
        List<Client> clients = offsetMonitoringClientList.get(offsetIdent.getIdentifier());
        if(clients != null) {
            return clients;
        }
        return Collections.EMPTY_LIST;
    }

}
