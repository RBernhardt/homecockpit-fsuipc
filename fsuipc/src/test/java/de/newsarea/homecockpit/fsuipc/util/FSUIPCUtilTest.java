package de.newsarea.homecockpit.fsuipc.util;

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
        OffsetItem oItem = OffsetItem.from("1, 4, 5000");
        assertEquals(1, oItem.getOffset());
        assertEquals(4, oItem.getSize());
        assertEquals(5000, oItem.getValue().toInt());
    }

}
