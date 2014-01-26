package de.newsarea.homecockpit.fsuipc2http.netty;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.handler.codec.frame.DelimiterBasedFrameDecoder;
import org.jboss.netty.handler.codec.frame.Delimiters;
import org.jboss.netty.handler.codec.string.StringDecoder;
import org.jboss.netty.handler.codec.string.StringEncoder;

import static org.jboss.netty.channel.Channels.pipeline;

public class TelnetServerPipelineFactory implements ChannelPipelineFactory {

    private TelnetServerHandler telnetServerHandler;

    public TelnetServerPipelineFactory(TelnetServerHandler telnetServerHandler) {
        this.telnetServerHandler = telnetServerHandler;
    }

    public ChannelPipeline getPipeline() throws Exception {
        // Create a default pipeline implementation.
        ChannelPipeline pipeline = pipeline();

        // Add the text line codec combination first,
        pipeline.addLast("framer", new DelimiterBasedFrameDecoder(
                8192, Delimiters.lineDelimiter()));
        pipeline.addLast("decoder", new StringDecoder());
        pipeline.addLast("encoder", new StringEncoder());

        // and then business logic.
        pipeline.addLast("handler", telnetServerHandler);

        return pipeline;
    }

}
