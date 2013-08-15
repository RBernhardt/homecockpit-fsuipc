package de.newsarea.homecockpit.fsuipc.domain;

import org.testng.annotations.Test;

import static org.testng.AssertJUnit.assertEquals;

public class OffsetIdentTest {

    @Test
    public void shouldReturnIdent() throws Exception {
        assertEquals("0x0001 : 4", OffsetIdent.fromString("0x0001 : 4").getIdentifier());
    }

    @Test
    public void shouldCreateOffsetIdentFromString() throws Exception {
        OffsetIdent oIdent = OffsetIdent.fromString("0x0001 : 4");
        assertEquals(1, oIdent.getOffset());
        assertEquals(4, oIdent.getSize());
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void shouldNotCreateOffsetIdentFromString_Invalid() throws Exception {
        OffsetItem.fromString("XXXXX");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void shouldNotCreateOffsetIdentFromString_ToBigOffsetValue() throws Exception {
        OffsetItem.fromString("0x00001 : 1");
    }

    @Test
    public void shouldReturnToString() throws Exception {
        assertEquals("0x3213 : 4", OffsetIdent.fromString("0x3213 : 4").toString());
        assertEquals("0x0321 : 4", OffsetIdent.fromString("0x321 : 4").toString());
        assertEquals("0x0032 : 4", OffsetIdent.fromString("0x32 : 4").toString());
        assertEquals("0x0003 : 4", OffsetIdent.fromString("0x3 : 4").toString());
    }

}
