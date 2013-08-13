package de.newsarea.homecockpit.fsuipc.domain;

import org.testng.annotations.Test;

import static org.testng.AssertJUnit.assertEquals;

public class OffsetIdentTest {

    @Test
    public void shouldReturnIdent() throws Exception {
        assertEquals("1 : 4", OffsetIdent.from("1, 4").getIdentifier());
    }

    @Test
    public void shouldCreateOffsetIdentFromString() throws Exception {
        OffsetIdent oIdent = OffsetIdent.from("1, 4");
        assertEquals(1, oIdent.getOffset());
        assertEquals(4, oIdent.getSize());
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void shouldNotCreateOffsetIdentFromString() throws Exception {
        OffsetIdent.from("XXXXX");
    }

    @Test
    public void shouldReturnToString() throws Exception {
        assertEquals("0x00000C8D (3213) : 4", OffsetIdent.from("3213, 4").toString());
    }

}
