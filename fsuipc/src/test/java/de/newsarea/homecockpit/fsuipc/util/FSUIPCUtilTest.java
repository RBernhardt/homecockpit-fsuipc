package de.newsarea.homecockpit.fsuipc.util;

import de.newsarea.homecockpit.fsuipc.domain.OffsetIdent;
import de.newsarea.homecockpit.fsuipc.domain.OffsetItem;
import org.testng.annotations.Test;

import static org.testng.AssertJUnit.assertEquals;

public class FSUIPCUtilTest {

    @Test
    public void shouldConvertToAlititude() {
        assertEquals(70.3D, FSUIPCUtil.toAlititude(92406982376L), 0);
    }

    @Test
    public void shouldConvertToFSUIPCAlititude() {
        assertEquals(90621753215L, FSUIPCUtil.toFSUIPCAlititude(70.3D));
    }

    @Test
    public void shouldConvertToOffsetItem() {
        OffsetItem oItem = FSUIPCUtil.toOffsetItem("1, 4, 5000");
        assertEquals(1, oItem.getOffset());
        assertEquals(4, oItem.getSize());
        assertEquals(5000, oItem.getValue().toInt());
    }

    @Test
    public void shouldConvertToOffsetItems() throws Exception {
        OffsetItem[] oItems = FSUIPCUtil.toOffsetItems("[[ 1, 4, 5000 ], [ 2, 4, 5000 ]]");
        assertEquals(2, oItems.length);
        assertEquals("0x00000001 (1) : 4 : 5000 (0x00001388)", oItems[0].toString());
        assertEquals("0x00000002 (2) : 4 : 5000 (0x00001388)", oItems[1].toString());
    }

    @Test
    public void shouldConvertToOffsetIdent() {
        OffsetIdent oIdent = FSUIPCUtil.toOffsetIdent("1, 4");
        assertEquals(1, oIdent.getOffset());
        assertEquals(4, oIdent.getSize());
    }

    @Test
    public void shouldConvertToOffsetIdents() throws Exception {
        OffsetIdent[] oIdents = FSUIPCUtil.toOffsetIdents("[[ 1, 4, 5000 ], [ 2, 4, 5000 ]]");
        assertEquals(2, oIdents.length);
        assertEquals("0x00000001 (1) : 4", oIdents[0].toString());
        assertEquals("0x00000002 (2) : 4", oIdents[1].toString());
    }

}
