package de.newsarea.homecockpit.fsuipc2net.net.domain;

public class Client {

    private final String clientId;

    public String getId() {
        return clientId;
    }

    public Client(String clientId) {
        this.clientId = clientId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Client)) return false;
        Client client = (Client) o;
        return clientId.equals(client.clientId);
    }

    @Override
    public int hashCode() {
        return clientId.hashCode();
    }

    @Override
    public String toString() {
        return "Client{" +
                "clientId='" + clientId + '\'' +
                '}';
    }
}
