package de.newsarea.homecockpit.fsuipc2net;

import de.newsarea.homecockpit.fsuipc.FSUIPCInterface;
import de.newsarea.homecockpit.fsuipc.domain.ByteArray;
import de.newsarea.homecockpit.fsuipc.domain.OffsetIdent;
import de.newsarea.homecockpit.fsuipc.domain.OffsetItem;
import de.newsarea.homecockpit.fsuipc.event.OffsetEventListener;
import de.newsarea.homecockpit.fsuipc2net.net.NetServer;
import de.newsarea.homecockpit.fsuipc2net.net.domain.Client;
import de.newsarea.homecockpit.fsuipc2net.net.domain.NetMessage;
import de.newsarea.homecockpit.fsuipc2net.net.event.ServerEventListener;
import org.apache.commons.lang3.event.EventListenerSupport;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Arrays;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class FSUIPCServerTest {


    private NetServer netServer;
    private FSUIPCInterface fsuipcInterface;
    private ClientRegistry clientRegistry;
    private EventListenerSupport<OffsetEventListener> offsetEventListenerList;
    private EventListenerSupport<ServerEventListener> serverEventListenerList;
    private FSUIPCServer fsuipcServer;

    @BeforeMethod
    public void beforeMethod() throws Exception {
        this.netServer = mock(NetServer.class);
        this.fsuipcInterface = mock(FSUIPCInterface.class);
        this.clientRegistry = mock(ClientRegistry.class);
        // ~
        offsetEventListenerList = EventListenerSupport.create(OffsetEventListener.class);
        doAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                offsetEventListenerList.addListener((OffsetEventListener) args[0]);
                return null;
            }
        }).when(this.fsuipcInterface).addEventListener(any(OffsetEventListener.class));
        // ~
        serverEventListenerList = EventListenerSupport.create(ServerEventListener.class);
        doAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                serverEventListenerList.addListener((ServerEventListener) args[0]);
                return null;
            }
        }).when(this.netServer).addEventListener(any(ServerEventListener.class));
        // ~
        fsuipcServer = new FSUIPCServer(netServer, fsuipcInterface, clientRegistry);
    }

    @Test
    public void shouldSendToClientId() throws Exception {
        when(clientRegistry.getClientIdsByOffsetEvent(any(OffsetIdent.class))).thenReturn(Arrays.asList(new Client("ClientId_1")));
        offsetEventListenerList.fire().offsetValueChanged(new OffsetItem((short)0x0001, (byte)2, ByteArray.create(new byte[]{1})));
        // ~
        verify(netServer).write(eq(new Client("ClientId_1")), eq(NetMessage.fromString("CHANGED[[0x0001:2:0x01]]")));
    }

    @Test
    public void shouldSendWriteFromClient() throws Exception {
        NetMessage netMessage = NetMessage.fromString("WRITE[[0x0001:2:0x01]]");
        serverEventListenerList.fire().valueReceived(new Client("Client_1"), netMessage);
        // ~
        verify(fsuipcInterface).write(eq(netMessage.getOffsetItems()));
    }

    @Test
    public void shouldSendMonitorFromClient() throws Exception {
        NetMessage netMessage = NetMessage.fromString("MONITOR[[0x0001:2:0x01]]");
        Client client = new Client("Client_Id");
        serverEventListenerList.fire().valueReceived(client, netMessage);
        // ~
        verify(clientRegistry).registerClientForOffsetEvent(eq(client), eq(netMessage.getItems().get(0).getOffsetIdent()));
        verify(fsuipcInterface).monitor(eq(netMessage.getItems().get(0).getOffsetIdent()));
    }

    @Test
    public void shouldSendReadFromClient() throws Exception {
        NetMessage netMessage = NetMessage.fromString("READ[[0x0001:2]]");
        OffsetItem offsetItem = new OffsetItem(1,2, new byte[] { 12, 10 });
        when(fsuipcInterface.read(eq(netMessage.getItems().get(0).getOffsetIdent()))).thenReturn(offsetItem);
        // ~
        Client client = new Client("Client_Id");
        serverEventListenerList.fire().valueReceived(client, netMessage);
        // ~
        verify(netServer).write(eq(client), eq(NetMessage.fromString("VALUE[[0x0001:2:0x0C0A]]")));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void shouldSendInvalidCommandFromClient() throws Exception {
        serverEventListenerList.fire().valueReceived(new Client("Client_1"), NetMessage.fromString("INVALID[[0x0001:2:0x01]]"));
    }



}
