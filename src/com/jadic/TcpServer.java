/**
 * @author Jadic
 * @created 2012-4-5 
 */
package com.jadic;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

import com.jadic.utils.KKLog;

public class TcpServer{
	
//	public final static TcpChannels tcpChannelsNoAuth = new TcpChannels(0, 0);
	public final static TcpChannels tcpChannels = new TcpChannels(20);//Runtime.getRuntime().availableProcessors() + 2);
	private int localPort;
	private ServerBootstrap bootstrap;

	public TcpServer(int port, int autoDisconnectMinutes, IDisposeData iDisposeData) {
		this.localPort = port;
		tcpChannels.setIDisposeDataAndLogger(iDisposeData);
		tcpChannels.startThreadCheckAutoDisconnect(autoDisconnectMinutes);
	}

	public void start() {
		ChannelFactory channelFactory = new NioServerSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool());
		bootstrap = new ServerBootstrap(channelFactory);
		bootstrap.setPipelineFactory(new ChannelPipelineFactory(){
			@Override
			public ChannelPipeline getPipeline() throws Exception {
				return Channels.pipeline(new TcpServerHandler());
			}
		});
		bootstrap.setOption("child.tcpNoDelay", true);
		bootstrap.setOption("child.keepAlive", true);
		bootstrap.bind(new InetSocketAddress(localPort));
		KKLog.info("TcpServer started, listening on Port:" + localPort);		
	}
	
	public void stop() {
		closeAllChannels();
		bootstrap.releaseExternalResources();
	}
	
	public void closeAllChannels() {
		tcpChannels.closeAllChannels();
	}

}
