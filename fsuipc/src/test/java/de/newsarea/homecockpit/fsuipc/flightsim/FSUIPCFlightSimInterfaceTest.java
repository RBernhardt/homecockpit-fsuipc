package de.newsarea.homecockpit.fsuipc.flightsim;

import de.newsarea.homecockpit.fsuipc.domain.ByteArray;
import de.newsarea.homecockpit.fsuipc.domain.OffsetIdent;
import de.newsarea.homecockpit.fsuipc.domain.OffsetItem;
import de.newsarea.homecockpit.fsuipc.event.OffsetEventListener;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.*;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

public class FSUIPCFlightSimInterfaceTest {

    private static final Logger log = LoggerFactory.getLogger(FSUIPCFlightSimInterfaceTest.class);

    private FSUIPCFlightSimInterface fsuipcFlightSimInterface;
    private FSUIPCFlightSimWrapper flightSimWrapper;
    private List<OffsetItem> lastWriteOffsetItems;

    @BeforeMethod
    public void beforeMethod() {
        lastWriteOffsetItems = new ArrayList<>();
        //
        flightSimWrapper = mock(FSUIPCFlightSimWrapper.class);
        fsuipcFlightSimInterface = new FSUIPCFlightSimInterface(flightSimWrapper);
        //
        doAnswer(new Answer<Object>() {
            public Object answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                lastWriteOffsetItems.add(new OffsetItem((int)args[0], (int)args[1], (byte[])args[2]));
                return null;
            }}).when(flightSimWrapper).write(anyInt(), anyInt(), any(byte[].class));
    }

    @Test
    public void shouldOpenAndClose() {
        when(flightSimWrapper.isConnectionEstablished()).thenReturn(true);
        // then
        try {
            fsuipcFlightSimInterface.open();
        } catch (ConnectException e) {
            assertTrue(false);
        }
        fsuipcFlightSimInterface.close();
        assertTrue(true);
    }

    @Test(expectedExceptions = ConnectException.class)
    public void shouldNotOpen() throws Exception {
        when(flightSimWrapper.isConnectionEstablished()).thenReturn(false);
        fsuipcFlightSimInterface.open();
    }

    @Test
    public void shouldRead() {
        when(flightSimWrapper.read(1010, 1)).thenReturn(new byte[]{1});
        assertEquals(new OffsetItem(1010, 1, ByteArray.create("1", 1)), fsuipcFlightSimInterface.read(new OffsetIdent(1010, 1)));
    }

    @Test
    public void shouldWriteSingleItem() {
        fsuipcFlightSimInterface.write(new OffsetItem(1000, 8, new byte[]{5, 6, 7}));
        assertEquals(lastWriteOffsetItems.size(), 1);
        assertEquals(1000, lastWriteOffsetItems.get(0).getOffset());
        assertEquals(8, lastWriteOffsetItems.get(0).getSize());
        // return little endian byte order
        assertEquals(ByteArray.create(new byte[]{7, 6, 5}), lastWriteOffsetItems.get(0).getValue());
    }

    @Test
    public void shouldMonitorManyOffsetIdents() throws InterruptedException, ConnectException {
        when(flightSimWrapper.isConnectionEstablished()).thenReturn(true);
        when(flightSimWrapper.read(anyInt(), anyInt())).thenReturn(new byte[]{(byte) 0xFF});
        fsuipcFlightSimInterface.open();
        //
        final List<Exception> exceptions = Collections.synchronizedList(new ArrayList<Exception>());
        ExecutorService executorService = Executors.newFixedThreadPool(100);
        for(int i=0; i < 100; i++) {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        for(int i=0; i < 1000; i++) {
                            fsuipcFlightSimInterface.monitor(new OffsetIdent(i, 2));
                            Thread.sleep(1);
                        }
                    } catch (Exception ex) {
                        exceptions.add(ex);
                    }
                }
            });
        }
        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.HOURS);
        // ~
        assertEquals(0, exceptions.size());
    }

    @Test
    public void shouldWriteOnlyNewValues() throws Exception {
        // given
        when(flightSimWrapper.isConnectionEstablished()).thenReturn(true);
        final List<OffsetItem> offsetItemList = new ArrayList<>();
        when(flightSimWrapper.read(eq(0x0001), eq(1))).thenReturn(new byte[] { 0x0A });
        fsuipcFlightSimInterface.addEventListener(new OffsetEventListener() {
            @Override
            public void valueChanged(OffsetItem offsetItem) {
                offsetItemList.add(offsetItem);
            }
        });
        // when
        fsuipcFlightSimInterface.monitor(new OffsetIdent(0x0001, 1));
        fsuipcFlightSimInterface.open();
        Thread.sleep(100);
        fsuipcFlightSimInterface.close();
        // then
        assertEquals(1, offsetItemList.size());
    }

    @Test
    public void shouldWriteWithDelay() throws Exception {
        Date startTime = new Date();
        fsuipcFlightSimInterface.write(new OffsetItem(0x0001, 1, ByteArray.create("1", 1)), 15);
        // then
        assertTrue(new Date().getTime() - startTime.getTime() > 10);
        verify(flightSimWrapper).write(0x0001, 1, new byte[] { 1 });
    }

    @Test
    public void shouldSyncronisedWrite() throws Exception {
        ExecutorService executorService =  Executors.newFixedThreadPool(10);
        // when
        for(int i=0; i < 10; i++) {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    fsuipcFlightSimInterface.write(new OffsetItem(0x0001, 1, ByteArray.create("1", 1)), 20);
                }
            });
        }
        Thread.sleep(10);
        // then
        verify(flightSimWrapper).write(anyInt(), anyInt(), any(byte[].class));
        // shutdown
        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.SECONDS);
        //
        verify(flightSimWrapper, times(10)).write(anyInt(), anyInt(), any(byte[].class));
    }
}
