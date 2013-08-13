package de.newsarea.homecockpit.fsuipc.flightsim;

import de.newsarea.homecockpit.fsuipc.domain.ByteArray;
import de.newsarea.homecockpit.fsuipc.domain.OffsetIdent;
import de.newsarea.homecockpit.fsuipc.domain.OffsetItem;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.*;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

public class FSUIPCFlightSimInterfaceTest {

    private FSUIPCFlightSimInterface fsuipcFlightSimInterface;
    private FSUIPCFlightSimWrapper flightSimWrapper;
    private List<OffsetItem> lastWriteOffsetItems;

    @BeforeMethod
    public void before() {
        lastWriteOffsetItems = new ArrayList<>();
        //
        flightSimWrapper = mock(FSUIPCFlightSimWrapper.class);
        fsuipcFlightSimInterface = new FSUIPCFlightSimInterface(flightSimWrapper);
        //
        doAnswer(new Answer<Object>() {
            public Object answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                lastWriteOffsetItems.add(new OffsetItem((Integer)args[0], (Integer)args[1], (byte[])args[2]));
                return null;
            }})
                .when(flightSimWrapper).write(anyInt(), anyInt(), any(byte[].class));
    }

    @Test
    public void shouldOpenAndClose() {
        try {
            fsuipcFlightSimInterface.open();
        } catch (ConnectException e) {
            assertTrue(false);
        }
        fsuipcFlightSimInterface.close();
        assertTrue(true);
    }

    @Test
    public void shouldRead() {
        when(flightSimWrapper.read(1010, 4)).thenReturn(new byte[] { 1 });
        assertEquals(new OffsetItem(1010, 4, ByteArray.create("1", 1)), fsuipcFlightSimInterface.read(new OffsetIdent(1010, 4)));
    }

    @Test
    public void shouldWriteSingleItem() {
        fsuipcFlightSimInterface.write(new OffsetItem(1000, 8, new byte[] { 5, 6, 7 }));
        assertEquals(lastWriteOffsetItems.size(), 1);
        assertEquals(1000, lastWriteOffsetItems.get(0).getOffset());
        assertEquals(8, lastWriteOffsetItems.get(0).getSize());
        // return little endian byte order
        assertEquals(ByteArray.create(new byte[]{7, 6, 5}), lastWriteOffsetItems.get(0).getValue());
    }

    @Test
    public void shouldWriteMultipleValidItem() {
        OffsetItem[] offsetItemBlock = new OffsetItem[] {
                new OffsetItem(1000, 4, new byte[] { 4, 4, 4, 4 }),
                new OffsetItem(1004, 1, new byte[] { 1 }),
                new OffsetItem(1005, 5, new byte[] { 5, 5, 5, 5, 5}),
                new OffsetItem(1010, 2, new byte[] { 2, 2 }),
        };
        //
        fsuipcFlightSimInterface.write(offsetItemBlock);
        //
        OffsetItem wOffsetItem = lastWriteOffsetItems.get(0);
        assertEquals(1000, wOffsetItem.getOffset());
        assertEquals(12, wOffsetItem.getSize());
        assertEquals(ByteArray.create(new byte[]{4, 4, 4, 4, 1, 5, 5, 5, 5, 5, 2, 2}), wOffsetItem.getValue());
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void shouldWriteMultipleItem_InvalidUnsort() {
        OffsetItem[] offsetItemBlock = new OffsetItem[] {
                new OffsetItem(1000, 4, new byte[] { 4, 4, 4, 4 }),
                new OffsetItem(1004, 1, new byte[] { 1 }),
                new OffsetItem(1010, 2, new byte[] { 2, 2 }),
                new OffsetItem(1005, 5, new byte[] { 5, 5, 5, 5, 5}),
                new OffsetItem(1001, 1, new byte[] { 1 }),
        };
        //
        fsuipcFlightSimInterface.write(offsetItemBlock);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void shouldWriteMultipleItem_InvalidGap() {
        OffsetItem[] offsetItemBlock = new OffsetItem[] {
                new OffsetItem(1000, 4, new byte[4]),
                new OffsetItem(1005, 1, new byte[1]),
        };
        //
        fsuipcFlightSimInterface.write(offsetItemBlock);
    }

}
