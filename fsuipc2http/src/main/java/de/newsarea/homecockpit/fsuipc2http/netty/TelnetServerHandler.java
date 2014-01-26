package de.newsarea.homecockpit.fsuipc2http.netty;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.*;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;

public class TelnetServerHandler extends SimpleChannelUpstreamHandler {

    private static final Logger log = LoggerFactory.getLogger(TelnetServerHandler.class);

    private ChannelGroup allConnected = new DefaultChannelGroup("all-connected");

    public void broadcastToAllClients(String message) {
        message += System.lineSeparator();
        ChannelBuffer cb = ChannelBuffers.wrappedBuffer(message.getBytes(Charset.forName("UTF-8")));
        allConnected.write(cb);
    }

    @Override
    public void handleUpstream(ChannelHandlerContext ctx, ChannelEvent e) throws Exception {
        if (e instanceof ChannelStateEvent) {
            log.info(e.toString());
        }
        super.handleUpstream(ctx, e);
    }

    @Override
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        super.channelConnected(ctx, e);
        log.info("channel connected - {}", e.getChannel());
        allConnected.add(e.getChannel());
    }

    @Override
    public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        super.channelDisconnected(ctx, e);
        log.info("channel diconnected - {}", e.getChannel());
        allConnected.remove(e.getChannel());
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
        String request = (String) e.getMessage();
        if ("exit".equals(request.toLowerCase())) {
            e.getChannel().close();
        }
    }

    @Override
    public void exceptionCaught (
        ChannelHandlerContext ctx, ExceptionEvent e) {
        log.warn("Unexpected exception from downstream.", e.getCause());
        e.getChannel().close();
    }

}
