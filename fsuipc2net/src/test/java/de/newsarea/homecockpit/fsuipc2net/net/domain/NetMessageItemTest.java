package de.newsarea.homecockpit.fsuipc2net.net.domain;

import org.testng.annotations.Test;

import static org.testng.AssertJUnit.assertEquals;

public class NetMessageItemTest {

    @Test
    public void shouldCreateFromString() throws Exception {
        assertEquals("0x0001:2:0xAFFA", NetMessageItem.fromString("0x0001:2:0xAFFA").toString());
        assertEquals("0x0001:2", NetMessageItem.fromString("0x0001:2").toString());
        assertEquals("0xFFFF:16", NetMessageItem.fromString("0xFFFF:16").toString());
        assertEquals("0x0FFF:16", NetMessageItem.fromString("0xFFF:16").toString());
        assertEquals("0x00FF:16", NetMessageItem.fromString("0xFF:16").toString());
        assertEquals("0x000F:16", NetMessageItem.fromString("0xF:16").toString());
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void shouldNotCreateFromString_ToBigOffset() throws Exception {
        NetMessageItem.fromString("0xFFFFF:16");
    }

}
