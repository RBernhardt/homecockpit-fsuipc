package de.newsarea.homecockpit.fsuipc2net;

import de.newsarea.homecockpit.fsuipc.domain.OffsetIdent;
import de.newsarea.homecockpit.fsuipc2net.net.domain.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClientRegistry {

    private static final Logger log = LoggerFactory.getLogger(ClientRegistry.class);

    private Map<String, List<Client>> offsetMonitoringClientList = new HashMap<>();

    public void registerClientForOffsetEvent(Client client, OffsetIdent offsetIdent) {
        if(!offsetMonitoringClientList.containsKey(offsetIdent.getIdentifier())) {
            offsetMonitoringClientList.put(offsetIdent.getIdentifier(), new ArrayList<Client>());
        }
        List<Client> clients = offsetMonitoringClientList.get(offsetIdent.getIdentifier());
        log.debug("append client ({}) to monitor list for offset {}", client, offsetIdent.getIdentifier());
        clients.add(client);
    }

    public List<Client> getClientIdsByOffsetEvent(OffsetIdent offsetIdent) {
        if(offsetMonitoringClientList.containsKey(offsetIdent.getIdentifier())) {
            return offsetMonitoringClientList.get(offsetIdent.getIdentifier());
        }
        return new ArrayList<>();
    }

}
