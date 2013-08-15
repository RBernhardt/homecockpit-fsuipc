package de.newsarea.homecockpit.fsuipc2net;

import de.newsarea.homecockpit.fsuipc.domain.OffsetIdent;
import de.newsarea.homecockpit.fsuipc2net.net.domain.Client;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
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



}
