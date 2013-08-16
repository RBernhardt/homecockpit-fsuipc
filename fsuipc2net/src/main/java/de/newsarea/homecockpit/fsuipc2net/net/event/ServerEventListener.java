package de.newsarea.homecockpit.fsuipc2net.net.event;

import de.newsarea.homecockpit.fsuipc2net.net.domain.Client;
import de.newsarea.homecockpit.fsuipc2net.net.domain.NetMessage;

public interface ServerEventListener {

    void clientConneted(Client client);

    void clientDisconnected(Client client);
	
	void valueReceived(Client client, NetMessage message);

}
