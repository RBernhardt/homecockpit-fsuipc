package de.newsarea.homecockpit.fsuipc2net.net.domain;

import de.newsarea.homecockpit.fsuipc.domain.ByteArray;
import de.newsarea.homecockpit.fsuipc.domain.OffsetIdent;
import de.newsarea.homecockpit.fsuipc.domain.OffsetItem;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import static org.testng.AssertJUnit.assertEquals;

public class NetMessageTest {

    @Test
    public void shouldReturnToString() throws Exception {
        assertEquals("MONITOR[[0x0001:2:0xAFFA]]", new NetMessage(NetMessage.Command.MONITOR, Arrays.asList(createDummyNetMessageItem(0x0001, 2)), 0).toString());
        assertEquals("CHANGED[[0x1010:8:0xAFFA]]", new NetMessage(NetMessage.Command.CHANGED, Arrays.asList(createDummyNetMessageItem(0x1010, 8)), 0).toString());
    }

    @Test
    public void shouldCreateFromString() throws Exception {
        assertEquals("MONITOR[[0x0001:2:0x5050]]", NetMessage.fromString("MONITOR[[0x0001:2:0x5050]]").toString());
    }

    @Test
    public void shouldCreateEqualObject() throws Exception {
        assertEquals(NetMessage.fromString("CHANGED[[0x0001:2:0x01]]"), NetMessage.fromString("CHANGED[[0x0001:2:0x01]]"));
        assertEquals(NetMessage.fromString("CHANGED[[0x0001:2:0x01]]").getItems().iterator().next(), new NetMessage(NetMessage.Command.CHANGED, new OffsetItem(1, 2, new byte[] { 1 })).getItems().iterator().next());
        assertEquals(NetMessage.fromString("CHANGED[[0x0001:2:0x01]]"), new NetMessage(NetMessage.Command.CHANGED, new OffsetItem(1, 2, new byte[] { 1 })));
    }

    @Test
    public void shouldReturnJson() throws Exception {
        Collection<NetMessageItem> netMessageItems = new ArrayList<>();
        netMessageItems.add(new NetMessageItem(new OffsetIdent(0x0001, 2), ByteArray.create("502", 2)));
        NetMessage netMessage = new NetMessage(NetMessage.Command.MONITOR, netMessageItems, 0);
        assertEquals("{\"cmd\":\"MONITOR\",\"items\":[{\"offset\":\"0x0001\",\"size\":2,\"data\":\"0x01F6\"}]}", netMessage.toJsonString());
    }

    @Test
    public void shouldCreateObjectFromJson() throws Exception {
        NetMessage netMessage = NetMessage.fromJson("{\"cmd\":\"MONITOR\",\"items\":[{\"offset\":\"0x0001\",\"size\":2,\"data\":\"0x01F6\"}]}");
        // then
        assertEquals(NetMessage.Command.MONITOR, netMessage.getCommand());
        assertEquals(1, netMessage.getItems().size());
        assertEquals("0x0001 : 2", netMessage.getItems().iterator().next().getOffsetIdent().toString());
        assertEquals("0x01F6", netMessage.getItems().iterator().next().getByteArray().toHexString());
    }

    /* HELPER */

    private NetMessageItem createDummyNetMessageItem(int offset, int size) {
        OffsetIdent offsetIdent = new OffsetIdent(offset, size);
        ByteArray byteArray = ByteArray.create(new byte[] {(byte) 0xAF, (byte) 0xFA});
        return new NetMessageItem(offsetIdent, byteArray);
    }

}
