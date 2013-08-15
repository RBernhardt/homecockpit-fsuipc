package de.newsarea.homecockpit.fsuipc.util;

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

}
