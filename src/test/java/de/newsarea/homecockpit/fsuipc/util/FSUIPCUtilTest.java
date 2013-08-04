package de.newsarea.homecockpit.fsuipc.util;

import de.newsarea.homecockpit.fsuipc.domain.OffsetItem;
import org.apache.log4j.BasicConfigurator;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class FSUIPCUtilTest {

    @BeforeClass
    public static void beforeClass() {
        BasicConfigurator.configure();
    }

    @Test
    public void testToAlititude() {
        Assert.assertEquals(70.3D, FSUIPCUtil.toAlititude(92406982376L), 0);
    }

    @Test
    public void testToFSUIPCAlititude() {
        Assert.assertEquals(90621753215L, FSUIPCUtil.toFSUIPCAlititude(70.3D));
    }

    @Test
    public void testToOffsetItem() {
        OffsetItem oItem = FSUIPCUtil.toOffsetItem("1, 4, 5000");
        Assert.assertEquals(1, oItem.getOffset());
        Assert.assertEquals(4, oItem.getSize());
        Assert.assertEquals(5000, DataTypeUtil.toInt(oItem.getValue()));
    }

}
