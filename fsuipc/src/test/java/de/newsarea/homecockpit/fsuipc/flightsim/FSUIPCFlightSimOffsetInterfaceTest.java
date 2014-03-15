package de.newsarea.homecockpit.fsuipc.flightsim;

import de.newsarea.homecockpit.fsuipc.domain.ByteArray;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.*;

public class FSUIPCFlightSimOffsetInterfaceTest {

    private static final Logger log = LoggerFactory.getLogger(FSUIPCFlightSimOffsetInterfaceTest.class);

    private FSUIPCFlightSimOffsetInterface fsuipcFlightSimOffsetInterface;
    private FSUIPCFlightSimWrapper flightSimWrapper;

    @BeforeMethod
    public void beforeMethod() {
        flightSimWrapper = mock(FSUIPCFlightSimWrapper.class);
        fsuipcFlightSimOffsetInterface = new FSUIPCFlightSimOffsetInterface(0x0001, flightSimWrapper);
    }

    @Test
    public void shouldNotReadOnWrite() throws Exception {
        // given
        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                TimeUnit.MILLISECONDS.sleep(995);
                return null;
            }
        }).when(flightSimWrapper).write(anyInt(), anyInt(), any(byte[].class));
        when(flightSimWrapper.read(anyInt(), anyInt())).thenReturn(new byte[0]);
        // when
        ExecutorService writeExecutorService = Executors.newFixedThreadPool(1);
        writeExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    fsuipcFlightSimOffsetInterface.write(2, ByteArray.create("0x0000"), 0);
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                }
            }
        });
        //
        ScheduledExecutorService readExecutorService = Executors.newScheduledThreadPool(1);
        readExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    fsuipcFlightSimOffsetInterface.read(2);
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }, 0, 1, TimeUnit.MILLISECONDS);
        TimeUnit.SECONDS.sleep(1);
        // shutdown
        writeExecutorService.shutdown();
        writeExecutorService.awaitTermination(1, TimeUnit.MINUTES);
        readExecutorService.shutdown();
        readExecutorService.awaitTermination(1, TimeUnit.MINUTES);
        // then
        verify(flightSimWrapper).write(anyInt(), anyInt(), any(byte[].class));
        verify(flightSimWrapper, atLeastOnce()).read(0x0001, 2);
        verify(flightSimWrapper, atMost(1000)).read(0x0001, 2);
    }

    @Test
    public void shouldSyncronisedOffsetWrite() throws Exception {
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        for(int i=0; i < 10; i++) {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        fsuipcFlightSimOffsetInterface.write(2, ByteArray.create("0x0000"), 20);
                    } catch (IOException e) {
                        log.error(e.getMessage(), e);
                    }
                }
            });
        }
        TimeUnit.MILLISECONDS.sleep(10);
        // then
        verify(flightSimWrapper).write(anyInt(), anyInt(), any(byte[].class));
        // shutdown
        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.SECONDS);
        //
        verify(flightSimWrapper, times(10)).write(anyInt(), anyInt(), any(byte[].class));
    }

}
