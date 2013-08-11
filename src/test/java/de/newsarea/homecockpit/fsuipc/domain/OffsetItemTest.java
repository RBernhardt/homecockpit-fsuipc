package de.newsarea.homecockpit.fsuipc.domain;

import org.junit.Assert;
import org.testng.annotations.Test;

import static org.testng.AssertJUnit.assertEquals;

public class OffsetItemTest {

    @Test
    public void shouldCreateOffsetItemFromString() throws Exception {
        OffsetItem oItem = OffsetItem.from("1, 4, 5000");
        Assert.assertEquals(1, oItem.getOffset());
        Assert.assertEquals(4, oItem.getSize());
        Assert.assertEquals(5000, oItem.getValue().toInt());
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void shouldNotCreateOffsetItemFromString() throws Exception {
        OffsetItem.from("XXXXX");
    }

    @Test
    public void shouldReturnToString() throws Exception {
        assertEquals("0x00000001 (1) : 4 : 255 (0xFF)", new OffsetItem(0x0001, 4, ByteArray.create(new byte[] { (byte)0xFF })).toString());
        assertEquals("0x00000001 (1) : 4 : 65535 (0xFFFF)", new OffsetItem(0x0001, 4, ByteArray.create(new byte[] { (byte)0xFF, (byte)0xFF })).toString());
        assertEquals("0x00000001 (1) : 4 : 44975 (0xAFAF)", new OffsetItem(0x0001, 4, ByteArray.create(new byte[] { (byte)0xAF, (byte)0xAF })).toString());
    }

}
