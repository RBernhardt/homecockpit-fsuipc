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
        assertEquals(90D, FSUIPCUtil.toLatitude(42957189152768000L));
        assertEquals(-90D, FSUIPCUtil.toLatitude(-42957189152768000L));
        assertEquals(32.012D, FSUIPCUtil.toLatitude(15279394879537880L));
        assertEquals(38.898556, FSUIPCUtil.toLatitude(18566362531794872L));
    }

    @Test
    public void shouldConvertLongitudeToFSUIPCLongitude() {
        assertEquals(9223372036854775807L, FSUIPCUtil.toFSUIPCLongitude(180D));
        assertEquals(-9223372036854775808L, FSUIPCUtil.toFSUIPCLongitude(-180D));
        assertEquals(1640325475798861568L, FSUIPCUtil.toFSUIPCLongitude(32.012D));
        assertEquals(1993199187135719680L, FSUIPCUtil.toFSUIPCLongitude(38.898556D));
    }

    @Test
    public void shouldConvertFSUIPCLongitudeToLongitude() {
        assertEquals(180D, FSUIPCUtil.toLongitude(9223372036854775807L));
        assertEquals(-180D, FSUIPCUtil.toLongitude(-9223372036854775808L));
        assertEquals(32.012D, FSUIPCUtil.toLongitude(1640325475798861568L));
        assertEquals(38.898556D, FSUIPCUtil.toLongitude(1993199187135719680L));
    }

    @DataProvider(name = "invalidLongitudeValues")
    public Object[][] invalidLongitudeValues() {
        return new Object[][] {
                { -181 },
                { 181 }
        };
    }

    @Test(dataProvider = "invalidLongitudeValues", expectedExceptions = IllegalArgumentException.class)
    public void shouldNotConvertToFSUIPCLongitude_ArgumentOutOfBound(Integer invalidLatitudeValue) throws Exception {
        FSUIPCUtil.toFSUIPCLatitude(invalidLatitudeValue);
    }

    @Test
    public void shouldConvertToAlititude() {
        assertEquals(70.3D, FSUIPCUtil.toAlititude(92406982376L), 0);
    }

    @Test
    public void shouldConvertToFSUIPCAlititude() {
        assertEquals(90621753215L, FSUIPCUtil.toFSUIPCAlititude(70.3D));
    }

    @Test
    public void shouldConvertFromHeadingToFSUIPCHeading() throws Exception {
        assertEquals(1073742230, FSUIPCUtil.toFSUIPCHeading(90));
        assertEquals(-2147482835, FSUIPCUtil.toFSUIPCHeading(180));
        assertEquals(-11928844, FSUIPCUtil.toFSUIPCHeading(359));
    }

    @Test
    public void shouldConvertFromFSUIPCHeadingToHeading() throws Exception {
        assertEquals(90D, FSUIPCUtil.toHeading(1073742230));
        assertEquals(180D, FSUIPCUtil.toHeading(-2147482835));
        assertEquals(359D, FSUIPCUtil.toHeading(-11928844));
        assertEquals(241.9D, FSUIPCUtil.toHeading(2886002457L));
    }

    @Test
    public void shouldConvertFromDegreeToFSUIPCDegree() throws Exception {
        assertEquals(1073742230L, FSUIPCUtil.toFSUIPCDegree(90D));
        assertEquals(2147484460L, FSUIPCUtil.toFSUIPCDegree(180D));
    }

    @Test
    public void shouldConvertFromFSUIPCDegreeToDegree() throws Exception {
        assertEquals(90D, FSUIPCUtil.toDegree(1073742230L));
        assertEquals(180D, FSUIPCUtil.toDegree(2147484460L));
    }

}
