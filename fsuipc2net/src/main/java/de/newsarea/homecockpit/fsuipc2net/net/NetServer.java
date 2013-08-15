package de.newsarea.homecockpit.fsuipc2net.net;

import de.newsarea.homecockpit.fsuipc2net.net.domain.Client;
import de.newsarea.homecockpit.fsuipc2net.net.domain.NetMessage;
import de.newsarea.homecockpit.fsuipc2net.net.event.ServerEventListener;

import java.io.IOException;
import java.net.ConnectException;

public interface NetServer {

    int getPort();

    void start() throws ConnectException;
    void write(Client client, NetMessage message) throws IOException;
    void write(NetMessage message) throws IOException;
    void addEventListener(ServerEventListener valueEventListener);
    void stop();

}
