package de.newsarea.homecockpit.fsuipc2http.watchdog.event;

import de.newsarea.homecockpit.fsuipc.domain.OffsetItem;

public interface ConnectorStateChangedEventListener {

    public enum State {
        OPEN,
        CLOSE
    }

    void stateChanged(State state);

}
