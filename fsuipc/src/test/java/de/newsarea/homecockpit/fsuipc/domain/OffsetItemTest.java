package de.newsarea.homecockpit.fsuipc.domain;

import org.junit.Assert;
import org.testng.annotations.Test;

import static org.testng.AssertJUnit.assertEquals;

public class OffsetItemTest {

    @Test
    public void shouldCreateOffsetItemFromString() throws Exception {
        OffsetItem oItem = OffsetItem.fromString("0x0001 : 4 : 0x5000");
        Assert.assertEquals(1, oItem.getOffset());
        Assert.assertEquals(4, oItem.getSize());
        Assert.assertEquals(20480, oItem.getValue().toInt());
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void shouldNotCreateOffsetItemFromString_Invalid() throws Exception {
        OffsetItem.fromString("XXXXX");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void shouldNotCreateOffsetItemFromString_ToBigOffsetValue() throws Exception {
        OffsetItem.fromString("0x00001 : 1 : 0x5000");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void shouldNotCreateOffsetItemFromString_ValueBiggerThanSize() throws Exception {
        OffsetItem.fromString("0x0001 : 1 : 0x5000");
    }

    @Test
    public void shouldReturnToString() throws Exception {
        assertEquals("0x0001 : 4 : 0xFF", new OffsetItem(0x0001, 4, ByteArray.create(new byte[] { (byte)0xFF })).toString());
        assertEquals("0x0001 : 2 : 0xFFFF", new OffsetItem(0x0001, 2, ByteArray.create(new byte[] { (byte)0xFF, (byte)0xFF })).toString());
        assertEquals("0x0001 : 8 : 0xAFAF", new OffsetItem(0x0001, 8, ByteArray.create(new byte[] { (byte)0xAF, (byte)0xAF })).toString());
        assertEquals("0xFFFF : 8 : 0xAFAF", new OffsetItem(0xFFFF, 8, ByteArray.create(new byte[] { (byte)0xAF, (byte)0xAF })).toString());
    }

    @Test
    public void shouldConvertToStringToFromString() throws Exception {
        OffsetItem origItem = new OffsetItem(0x0001, 2, ByteArray.create(new byte[] { (byte)0xF0, (byte)0xF0 }));
        String value = origItem.toString();
        OffsetItem newItem = OffsetItem.fromString(value);
        assertEquals(origItem, newItem);
    }


}
