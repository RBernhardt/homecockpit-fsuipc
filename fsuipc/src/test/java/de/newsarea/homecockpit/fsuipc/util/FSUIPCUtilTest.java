package de.newsarea.homecockpit.fsuipc.util;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.testng.AssertJUnit.assertEquals;

public class FSUIPCUtilTest {

    @Test
    public void shouldConvertLatitudeToFSUIPCLatitude() {
        assertEquals(42957189152768000L, FSUIPCUtil.toFSUIPCLatitude(90D));
        assertEquals(-42957189152768000L, FSUIPCUtil.toFSUIPCLatitude(-90D));
        assertEquals(15279394879537880L, FSUIPCUtil.toFSUIPCLatitude(32.012D));
        assertEquals(18566362531794872L, FSUIPCUtil.toFSUIPCLatitude(38.898556D));
    }

    @DataProvider(name = "invalidLatitudeValues")
    public Object[][] invalidLatitudeValues() {
        return new Object[][] {
                { -91 },
                { 91 }
        };
    }

    @Test(dataProvider = "invalidLatitudeValues", expectedExceptions = IllegalArgumentException.class)
    public void shouldNotConvertToFSUIPCLatitude_ArgumentOutOfBound(Integer invalidLatitudeValue) throws Exception {
        FSUIPCUtil.toFSUIPCLatitude(invalidLatitudeValue);
    }

    @Test
    public void shouldConvertFSUIPCLatitudeToLatitude() {
        assertEquals(32.012D, FSUIPCUtil.toLatitude(15279394879537880L));
        assertEquals(38.898556, FSUIPCUtil.toLatitude(18566362531794872L));
    }


    @Test
    public void shouldConvertToAlititude() {
        assertEquals(70.3D, FSUIPCUtil.toAlititude(92406982376L), 0);
    }

    @Test
    public void shouldConvertToFSUIPCAlititude() {
        assertEquals(90621753215L, FSUIPCUtil.toFSUIPCAlititude(70.3D));
    }

}
