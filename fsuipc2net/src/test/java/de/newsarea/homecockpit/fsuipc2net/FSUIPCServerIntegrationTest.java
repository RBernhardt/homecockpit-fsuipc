package de.newsarea.homecockpit.fsuipc2net;

import com.esotericsoftware.kryonet.Client;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Date;

import static org.testng.AssertJUnit.assertTrue;

public class FSUIPCServerIntegrationTest {

    private Client client;

    @BeforeMethod
    public void setUp() throws Exception {
        client = new Client();
        client.start();
        client.connect(5000, "simulator", 4020);
    }

    @AfterMethod
    public void tearDown() throws Exception {
        client.stop();
        client.close();
    }

    @Test(enabled = false)
    public void shouldMonitorLatitude() throws Exception {
        Date startTime = new Date();
        client.sendTCP("{\"cmd\":\"WRITE\",\"timeOfBlocking\":\"1500\",\"items\":[{\"offset\":0x04F4,\"size\":2,\"data\":\"0x0002\"}]}");
        client.sendTCP("{\"cmd\":\"WRITE\",\"timeOfBlocking\":\"1500\",\"items\":[{\"offset\":0x04F4,\"size\":2,\"data\":\"0x0001\"}]}");
        client.sendTCP("{\"cmd\":\"WRITE\",\"timeOfBlocking\":\"1500\",\"items\":[{\"offset\":0x04F4,\"size\":2,\"data\":\"0x0002\"}]}");
        assertTrue(new Date().getTime() - startTime.getTime() > 1500);
    }
}
