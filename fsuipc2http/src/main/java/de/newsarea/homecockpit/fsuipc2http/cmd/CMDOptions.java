package de.newsarea.homecockpit.fsuipc2http.cmd;

public class CMDOptions {

    private int httpPort;
    private int socketPort;

    public int getHttpPort() {
        return httpPort;
    }

    public int getSocketPort() {
        return socketPort;
    }

    public CMDOptions(int httpPort, int socketPort) {
        this.httpPort = httpPort;
        this.socketPort = socketPort;
    }

}
