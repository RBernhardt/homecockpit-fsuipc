package de.newsarea.homecockpit.fsuipc2http.watchdog;

public interface MonitorableConnector {

    boolean isAlive();
    boolean reconnect();

}
