package com.xkey.webcam.streaming.channel;

import java.awt.*;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.handler.codec.frame.LengthFieldBasedFrameDecoder;

import com.xkey.webcam.streaming.handler.H264StreamDecoder;
import com.xkey.webcam.streaming.handler.StreamClientHandler;
import com.xkey.webcam.streaming.handler.StreamClientListener;
import com.xkey.webcam.streaming.handler.StreamFrameListener;


public class StreamClientChannelPipelineFactory implements ChannelPipelineFactory {
    protected final StreamClientListener streamClientListener;
    protected final StreamFrameListener streamFrameListener;
    protected final Dimension dimension;


    public StreamClientChannelPipelineFactory(
            StreamClientListener streamClientListener,
            StreamFrameListener streamFrameListener, Dimension dimension) {
        super();
        this.streamClientListener = streamClientListener;
        this.streamFrameListener = streamFrameListener;
        this.dimension = dimension;
    }

    public ChannelPipeline getPipeline() throws Exception {
        ChannelPipeline pipeline = Channels.pipeline();
        //add an simple indicator handler
        pipeline.addLast("stream client handler", new StreamClientHandler(streamClientListener));
        //add the frame codec
        pipeline.addLast("frame decoder", new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
        //add the video stream handler
        //change the below falst --> ture ,if using the netty's frame codec
        pipeline.addLast("stream handler", new H264StreamDecoder(streamFrameListener, dimension, false, false));

        return pipeline;
    }


}
