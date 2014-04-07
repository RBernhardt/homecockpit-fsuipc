package de.newsarea.homecockpit.fsuipc2http.watchdog.event;

public interface ConnectorStateChangedEventListener {

    public enum State {
        OPEN,
        CLOSED
    }

    void stateChanged(String id, State state);

}
