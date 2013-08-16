package de.newsarea.homecockpit.fsuipc2net;

import de.newsarea.homecockpit.fsuipc.domain.OffsetIdent;
import de.newsarea.homecockpit.fsuipc.domain.OffsetItem;
import de.newsarea.homecockpit.fsuipc2net.net.domain.Client;
import de.newsarea.homecockpit.fsuipc2net.net.domain.NetMessageItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ClientRegistry {

    private static final Logger log = LoggerFactory.getLogger(ClientRegistry.class);

    private Set<Client> clients;
    private Map<String, List<Client>> offsetMonitoringClientList = new ConcurrentHashMap<>();

    public ClientRegistry() {
        this.clients = Collections.synchronizedSet(new HashSet<Client>());
        this.offsetMonitoringClientList = new ConcurrentHashMap<>();
    }

    public int getOffsetCount() {
        return offsetMonitoringClientList.size();
    }

    public Collection<Client> getClients() {
        return Collections.unmodifiableCollection(clients);
    }

    public void deregisterClientForOffsetEvent(Client client) {
        for(List<Client> clients : offsetMonitoringClientList.values()) {
            if(clients.contains(client)) {
                clients.remove(client);
            }
        }
        clients.remove(client);
    }

    public void registerClientForOffsetEvent(Client client, OffsetIdent offsetIdent) {
        String ident = offsetIdent.getIdentifier();
        List<Client> clientList = offsetMonitoringClientList.get(ident);
        if(clientList == null) {
            clientList = Collections.synchronizedList(new ArrayList<Client>());
            offsetMonitoringClientList.put(ident, clientList);
        }
        log.debug("append client ({}) to monitor list for offset {}", client, offsetIdent.getIdentifier());
        clientList.add(client);
        clients.add(client);
    }

    public Collection<Client> getClientIdsByOffsetEvent(OffsetIdent offsetIdent) {
        List<Client> clients = offsetMonitoringClientList.get(offsetIdent.getIdentifier());
        if(clients != null) {
            return clients;
        }
        return Collections.emptyList();
    }

    public Collection<NetMessageItem> filterForClient(Client client, Collection<OffsetItem> offsetItems) {
        Collection<NetMessageItem> netMessageItems = new HashSet<>();
        for(OffsetItem offsetItem : offsetItems) {
            Collection<Client> clients = getClientIdsByOffsetEvent(offsetItem);
            if(clients.contains(client)) {
                netMessageItems.add(new NetMessageItem(offsetItem, offsetItem.getValue()));
            }
        }
        return netMessageItems;
    }

}
