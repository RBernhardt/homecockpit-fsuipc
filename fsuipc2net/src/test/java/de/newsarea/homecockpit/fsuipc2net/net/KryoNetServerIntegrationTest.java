package de.newsarea.homecockpit.fsuipc2net.net;

import com.esotericsoftware.kryonet.Client;
import de.newsarea.homecockpit.fsuipc2net.net.domain.NetMessage;
import de.newsarea.homecockpit.fsuipc2net.net.event.ServerEventListener;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

public class KryoNetServerIntegrationTest {

    private static final int PORT = 8989;

    private KryoNetServer kryoNetServer;


    @BeforeMethod
    public void beforeMethod() throws ConnectException {
        kryoNetServer = new KryoNetServer(PORT);
        kryoNetServer.start();
    }

    @AfterMethod
    public void afterMethod() throws InterruptedException {
        kryoNetServer.stop();
    }

    /*
    @Test
    public void shouldSendFromClient() throws Exception {
        Thread.sleep(100);
        Client client = new Client();
        client.start();
        client.connect(5000, "localhost", PORT);
        client.sendTCP("WRITE[[0x0001:2:0x5050][0xFF00:2:0x10]]");
        client.close();
        Thread.sleep(200);
        //
        assertEquals(1, messages.size());
        assertEquals("[WRITE[[0x0001:2:0x5050][0xFF00:2:0x10]]]", messages.toString());
    }
    */

    @Test
    public void shouldSendFromClient() throws Exception {
        final List<NetMessage> messages = new ArrayList<>();
        kryoNetServer.addEventListener(new ServerEventListener() {
            @Override
            public void clientConneted(de.newsarea.homecockpit.fsuipc2net.net.domain.Client client) { }

            @Override
            public void clientDisconnected(de.newsarea.homecockpit.fsuipc2net.net.domain.Client client) { }

            @Override
            public void valueReceived(de.newsarea.homecockpit.fsuipc2net.net.domain.Client client, NetMessage message) {
                messages.add(message);
            }
        });
        // when
        Thread.sleep(100);
        Client client = new Client();
        client.start();
        client.connect(5000, "localhost", PORT);
        client.sendTCP("{\"cmd\":\"WRITE\",\"items\":[{\"offset\":0x0001,\"size\":2,\"data\":\"0x01F6\"},{\"offset\":0xFF00,\"size\":2,\"data\":\"0x10\"}]}");
        client.close();
        Thread.sleep(200);
        // then
        assertEquals(1, messages.size());
        assertEquals("[WRITE[[0x0001:2:0x01F6][0xFF00:2:0x10]]]", messages.toString());
    }

    @Test(enabled = false)
    public void shouldBeABlockingRequest() throws InterruptedException, IOException {
        kryoNetServer.addEventListener(new ServerEventListener() {
            @Override
            public void clientConneted(de.newsarea.homecockpit.fsuipc2net.net.domain.Client client) { }

            @Override
            public void clientDisconnected(de.newsarea.homecockpit.fsuipc2net.net.domain.Client client) { }

            @Override
            public void valueReceived(de.newsarea.homecockpit.fsuipc2net.net.domain.Client client, NetMessage message) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        // when
        Thread.sleep(100);
        Client client = new Client();
        client.start();
        client.connect(5000, "localhost", PORT);
        Date startTime = new Date();
        client.sendTCP("{\"cmd\":\"WRITE\",\"items\":[{\"offset\":0x0001,\"size\":2,\"data\":\"0x01F6\"},{\"offset\":0xFF00,\"size\":2,\"data\":\"0x10\"}]}");
        client.sendTCP("{\"cmd\":\"WRITE\",\"items\":[{\"offset\":0x0001,\"size\":2,\"data\":\"0x01F6\"},{\"offset\":0xFF00,\"size\":2,\"data\":\"0x10\"}]}");
        client.sendTCP("{\"cmd\":\"WRITE\",\"items\":[{\"offset\":0x0001,\"size\":2,\"data\":\"0x01F6\"},{\"offset\":0xFF00,\"size\":2,\"data\":\"0x10\"}]}");
        client.close();
        // then
        long timeDiff = new Date().getTime() - startTime.getTime();
        assertTrue("but was " + timeDiff, timeDiff > 100);
    }

}
