package com.xkey.webcam.streaming.agent;

import java.awt.*;
import java.net.SocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xkey.webcam.streaming.channel.StreamClientChannelPipelineFactory;
import com.xkey.webcam.streaming.handler.StreamClientListener;
import com.xkey.webcam.streaming.handler.StreamFrameListener;

public class StreamClientAgent implements IStreamClientAgent {
    protected final static Logger logger = LoggerFactory.getLogger(StreamClientAgent.class);
    protected final ClientBootstrap clientBootstrap;
    protected final StreamClientListener streamClientListener;
    protected final StreamFrameListener streamFrameListener;
    protected final Dimension dimension;
    protected Channel clientChannel;


    public StreamClientAgent(StreamFrameListener streamFrameListener,
                             Dimension dimension) {
        super();
        this.dimension = dimension;
        this.clientBootstrap = new ClientBootstrap();
        this.clientBootstrap.setFactory(new NioClientSocketChannelFactory(
                Executors.newCachedThreadPool(),
                Executors.newCachedThreadPool()));
        this.streamFrameListener = streamFrameListener;
        this.streamClientListener = new StreamClientListenerIMPL();
        this.clientBootstrap.setPipelineFactory(
                new StreamClientChannelPipelineFactory(
                        streamClientListener,
                        streamFrameListener,
                        dimension));
    }

    public void connect(SocketAddress streamServerAddress) {
        logger.info("going to connect to stream server :{}", streamServerAddress);
        clientBootstrap.connect(streamServerAddress);
    }

    public void stop() {
        clientChannel.close();
        clientBootstrap.releaseExternalResources();
    }

    protected class StreamClientListenerIMPL implements StreamClientListener {

        public void onConnected(Channel channel) {
            //	logger.info("stream connected to server at :{}",channel);
            clientChannel = channel;
        }

        public void onDisconnected(Channel channel) {
            //	logger.info("stream disconnected to server at :{}",channel);
        }

        public void onException(Channel channel, Throwable t) {
            //	logger.debug("exception at :{},exception :{}",channel,t);
        }


    }

}
