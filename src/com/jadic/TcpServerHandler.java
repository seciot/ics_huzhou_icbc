/**
 * @author Jadic
 * @created 2012-4-5 
 */
package com.jadic;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;

import com.jadic.utils.KKLog;
import com.jadic.utils.KKTool;

public class TcpServerHandler extends SimpleChannelHandler {

	@Override
	public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent event)
			throws Exception {
		Channel channel = ctx.getChannel();
		if (isTcpClientIpValid(channel)) {
			addTcpChannel(channel);
			KKLog.info("a tcpclient Connected[" + channel.getRemoteAddress() + "], total:" + TcpServer.tcpChannels.getCount());
		} else {
			KKLog.info("a tcpclient Connected[" + channel.getRemoteAddress() + "], its ip is not valid, ignore this connection, total:" + TcpServer.tcpChannels.getCount());
		}
	}
	
	@Override
	public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent event)
			throws Exception {
		disconnectAndRemoveTcpChannel(ctx.getChannel());
		KKLog.info("channel closed, channelId=" + ctx.getChannel().getId() + "," + ctx.getChannel().getRemoteAddress() + ",total:" + TcpServer.tcpChannels.getCount());
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent event)
			throws Exception {
		KKLog.info("channel[" + ctx.getChannel().getRemoteAddress() + "]exceptionCaught:" + event.getCause().getMessage()  + ",total:" + TcpServer.tcpChannels.getCount());
		disconnectAndRemoveTcpChannel(ctx.getChannel());
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent event)
			throws Exception {
		if (isTcpClientIpValid(ctx.getChannel())) {
			ChannelBuffer channelBuffer = (ChannelBuffer)event.getMessage();
			TcpChannel tcpChannel = getTcpChannel(ctx.getChannel());
			if (tcpChannel != null) {
				tcpChannel.recvData(channelBuffer);
				if (!tcpChannel.isDisposingData) {
					TcpServer.tcpChannels.disposeRecvdData(tcpChannel);
				} else {
				}
			}
		}
	}
	
	private TcpChannel addTcpChannel(Channel channel) {
		if (channel == null) {
			return null;
		}
		
		TcpChannel tcpChannel = TcpServer.tcpChannels.getTcpChannel(channel.getId());
		
		if (tcpChannel == null) {
			tcpChannel = new TcpChannel(channel);
			TcpServer.tcpChannels.addTcpChannel(tcpChannel);
		} else {
			tcpChannel.setLastConnTime(System.currentTimeMillis());
		}
		
		return tcpChannel;
	}
	
	private TcpChannel getTcpChannel(Channel channel) {
		TcpChannel tcpChannel = TcpServer.tcpChannels.getTcpChannel(channel.getId());
//		if (tcpChannel == null)
//			tcpChannel = TcpServer.tcpChannelsNoAuth.getTcpChannel(channel.getId());
		
		return tcpChannel;
	}
	
	private void disconnectAndRemoveTcpChannel(Channel channel) {
		if (channel != null) {
			channel.disconnect();
			removeTcpChannel(channel);
		}
	}
	
	private boolean removeTcpChannel(Channel channel) {
		if (channel != null) {
			return TcpServer.tcpChannels.removeTcpChannel(channel.getId());
		} else {
			return false;
		}
	}
	
	private boolean isTcpClientIpValid(Channel channel) {
		if (KKTool.isStrNullOrBlank(Ics.sysParams.getClientIp())) {
			return true;
		}
		if (channel == null) {
			return false;
		}
		SocketAddress socketAddress = channel.getRemoteAddress();
		if (socketAddress instanceof InetSocketAddress) {
			InetSocketAddress isa = (InetSocketAddress)socketAddress;
			return isa.getAddress().getHostAddress().equals(Ics.sysParams.getClientIp());
		} else {
			return channel.getRemoteAddress().toString().indexOf(Ics.sysParams.getClientIp()) >= 0;
		}
	}

	public static void printLog(Object log) {
	}
}
