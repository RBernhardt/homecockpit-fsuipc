package de.newsarea.homecockpit.fsuipc2net.net.domain;

import de.newsarea.homecockpit.fsuipc.domain.ByteArray;
import de.newsarea.homecockpit.fsuipc.domain.OffsetIdent;
import de.newsarea.homecockpit.fsuipc.domain.OffsetItem;
import org.testng.annotations.Test;

import java.util.Arrays;

import static org.testng.AssertJUnit.assertEquals;

public class NetMessageTest {

    @Test
    public void shouldReturnToString() throws Exception {
        assertEquals("MONITOR[[0x0001:2:0xAFFA]]", new NetMessage(NetMessage.Command.MONITOR, Arrays.asList(createDummyNetMessageItem(0x0001, 2))).toString());
        assertEquals("CHANGED[[0x1010:8:0xAFFA]]", new NetMessage(NetMessage.Command.CHANGED, Arrays.asList(createDummyNetMessageItem(0x1010, 8))).toString());
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

    /* HELPER */

    private NetMessageItem createDummyNetMessageItem(int offset, int size) {
        OffsetIdent offsetIdent = new OffsetIdent(offset, size);
        ByteArray byteArray = ByteArray.create(new byte[] {(byte) 0xAF, (byte) 0xFA});
        return new NetMessageItem(offsetIdent, byteArray);
    }

}
