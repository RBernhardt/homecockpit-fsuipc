package de.newsarea.homecockpit.fsuipc2net;

import de.newsarea.homecockpit.fsuipc.domain.OffsetIdent;
import de.newsarea.homecockpit.fsuipc.domain.OffsetItem;
import de.newsarea.homecockpit.fsuipc2net.net.domain.Client;
import de.newsarea.homecockpit.fsuipc2net.net.domain.NetMessageItem;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.testng.AssertJUnit.assertEquals;

public class ClientRegistryTest {

    private ClientRegistry clientRegistry;

    @BeforeMethod
    public void beforeMethod() {
        clientRegistry = new ClientRegistry();
    }

    @Test
    public void shouldRegisterClient() throws Exception {
        clientRegistry.registerClientForOffsetEvent(new Client("ClientId"), new OffsetIdent(0xFFFF, 2));
        assertEquals("[Client{clientId='ClientId'}]", clientRegistry.getClientIdsByOffsetEvent(new OffsetIdent(0xFFFF, 2)).toString());
    }

    @Test
    public void shouldRegisterManyClientsASync() throws Exception {
        final List<Exception> exceptions = Collections.synchronizedList(new ArrayList<Exception>());
        ExecutorService executorService = Executors.newFixedThreadPool(100);
        for(int i=0; i < 100; i++) {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        for(int i=0; i < 100; i++) {
                            Client client = new Client(UUID.randomUUID().toString());
                            clientRegistry.registerClientForOffsetEvent(client, new OffsetIdent(i, 2));
                        }
                    } catch (Exception ex) {
                        exceptions.add(ex);
                    }
                }
            });
        }
        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.HOURS);
        // ~
        assertEquals(100, clientRegistry.getOffsetCount());
        assertEquals(100, clientRegistry.getClientIdsByOffsetEvent(new OffsetIdent(1, 2)).size());
        assertEquals(0, exceptions.size());
    }

    @Test
    public void shouldReturnClients() throws Exception {
        clientRegistry.registerClientForOffsetEvent(new Client("1"), OffsetIdent.fromString("0x0001 : 4"));
        clientRegistry.registerClientForOffsetEvent(new Client("1"), OffsetIdent.fromString("0x0001 : 4"));
        clientRegistry.registerClientForOffsetEvent(new Client("2"), OffsetIdent.fromString("0x0001 : 2"));
        clientRegistry.registerClientForOffsetEvent(new Client("2"), OffsetIdent.fromString("0x0001 : 4"));
        clientRegistry.registerClientForOffsetEvent(new Client("3"), OffsetIdent.fromString("0x0001 : 4"));
        //
        assertEquals(3, clientRegistry.getClients().size());
    }

    @Test
    public void shouldRemoveClient() throws Exception {
        clientRegistry.registerClientForOffsetEvent(new Client("1"), OffsetIdent.fromString("0x0001 : 4"));
        clientRegistry.registerClientForOffsetEvent(new Client("1"), OffsetIdent.fromString("0x0001 : 4"));
        clientRegistry.registerClientForOffsetEvent(new Client("2"), OffsetIdent.fromString("0x0001 : 2"));
        clientRegistry.registerClientForOffsetEvent(new Client("2"), OffsetIdent.fromString("0x0001 : 4"));
        clientRegistry.registerClientForOffsetEvent(new Client("3"), OffsetIdent.fromString("0x0001 : 4"));
        //
        assertEquals(3, clientRegistry.getClients().size());
        clientRegistry.deregisterClientForOffsetEvent(new Client("2"));
        assertEquals(2, clientRegistry.getClients().size());
    }

    @Test
    public void shouldReturnFilteredOffsets() throws Exception {
        clientRegistry.registerClientForOffsetEvent(new Client("1"), OffsetIdent.fromString("0x0001 : 4"));
        clientRegistry.registerClientForOffsetEvent(new Client("1"), OffsetIdent.fromString("0x0002 : 4"));
        clientRegistry.registerClientForOffsetEvent(new Client("2"), OffsetIdent.fromString("0x0001 : 4"));
        clientRegistry.registerClientForOffsetEvent(new Client("2"), OffsetIdent.fromString("0x0001 : 4"));
        clientRegistry.registerClientForOffsetEvent(new Client("3"), OffsetIdent.fromString("0x0003 : 4"));
        // ~
        Collection<OffsetItem> offsetItems = new ArrayList<>();
        offsetItems.add(OffsetItem.fromString("0x0001 : 4 : 0xFF"));
        offsetItems.add(OffsetItem.fromString("0x0002 : 4 : 0xFF"));
        // ~
        Collection<NetMessageItem> filteredItems = clientRegistry.filterForClient(new Client("1"), offsetItems);
        assertEquals(2, filteredItems.size());
    }
}
