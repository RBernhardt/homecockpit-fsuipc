package de.newsarea.homecockpit.fsuipc2net;

import de.newsarea.homecockpit.fsuipc.FSUIPCInterface;
import de.newsarea.homecockpit.fsuipc.domain.ByteArray;
import de.newsarea.homecockpit.fsuipc.domain.OffsetItem;
import de.newsarea.homecockpit.fsuipc.event.OffsetCollectionEventListener;
import de.newsarea.homecockpit.fsuipc2net.net.NetServer;
import de.newsarea.homecockpit.fsuipc2net.net.domain.Client;
import de.newsarea.homecockpit.fsuipc2net.net.domain.NetMessage;
import de.newsarea.homecockpit.fsuipc2net.net.domain.NetMessageItem;
import de.newsarea.homecockpit.fsuipc2net.net.event.ServerEventListener;
import org.apache.commons.lang3.event.EventListenerSupport;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Collection;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class FSUIPCServerTest {


    private NetServer netServer;
    private FSUIPCInterface fsuipcInterface;
    private ClientRegistry clientRegistry;
    private EventListenerSupport<OffsetCollectionEventListener> offsetCollectionEventListener;
    private EventListenerSupport<ServerEventListener> serverEventListenerList;

    @BeforeMethod
    public void beforeMethod() throws Exception {
        this.netServer = mock(NetServer.class);
        this.fsuipcInterface = mock(FSUIPCInterface.class);
        this.clientRegistry = mock(ClientRegistry.class);
        // ~
        offsetCollectionEventListener = EventListenerSupport.create(OffsetCollectionEventListener.class);
        doAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                offsetCollectionEventListener.addListener((OffsetCollectionEventListener) args[0]);
                return null;
            }
        }).when(this.fsuipcInterface).addEventListener(any(OffsetCollectionEventListener.class));
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
        new FSUIPCServer(netServer, fsuipcInterface, clientRegistry);
    }

    @Test
    public void shouldSendToClientId() throws Exception {
        Client client = new Client("ClientId_1");
        Collection<NetMessageItem> netMessageItems = Arrays.asList(NetMessageItem.fromString("0x0001:2:0x01"));
        when(clientRegistry.getClients()).thenReturn(Arrays.asList(client));
        when(clientRegistry.filterForClient(eq(client), anyCollectionOf(OffsetItem.class))).thenReturn(netMessageItems);
        // when
        offsetCollectionEventListener.fire().valuesChanged(Arrays.asList(new OffsetItem((short) 0x0001, (byte) 2, ByteArray.create(new byte[]{1}))));
        Thread.sleep(10);
        // then
        verify(netServer).write(any(Client.class), any(NetMessage.class));
    }

    @Test
    public void shouldSendWriteFromClient() throws Exception {
        NetMessage netMessage = NetMessage.fromString("WRITE[[0x0001:2:0x01]]");
        // when
        serverEventListenerList.fire().valueReceived(new Client("Client_1"), netMessage);
        Thread.sleep(10);
        // then
        verify(fsuipcInterface).write(eq(netMessage.getOffsetItems()));
    }

    @Test
    public void shouldSendMonitorFromClient() throws Exception {
        NetMessage netMessage = NetMessage.fromString("MONITOR[[0x0001:2:0x01]]");
        Client client = new Client("Client_Id");
        // when
        serverEventListenerList.fire().valueReceived(client, netMessage);
        Thread.sleep(10);
        // then
        verify(clientRegistry).registerClientForOffsetEvent(eq(client), eq(netMessage.getItems().iterator().next().getOffsetIdent()));
        verify(fsuipcInterface).monitor(eq(netMessage.getItems().iterator().next().getOffsetIdent()));
    }

    @Test
    public void shouldSendReadFromClient() throws Exception {
        NetMessage netMessage = NetMessage.fromString("READ[[0x0001:2]]");
        OffsetItem offsetItem = new OffsetItem(1,2, new byte[] { 12, 10 });
        when(fsuipcInterface.read(eq(netMessage.getItems().iterator().next().getOffsetIdent()))).thenReturn(offsetItem);
        // when
        Client client = new Client("Client_Id");
        serverEventListenerList.fire().valueReceived(client, netMessage);
        Thread.sleep(10);
        // then
        verify(netServer).write(eq(client), eq(NetMessage.fromString("VALUE[[0x0001:2:0x0C0A]]")));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void shouldSendInvalidCommandFromClient() throws Exception {
        serverEventListenerList.fire().valueReceived(new Client("Client_1"), NetMessage.fromString("INVALID[[0x0001:2:0x01]]"));
    }



}
