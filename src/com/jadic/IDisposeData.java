/**
 * @author Jadic
 * @created 2012-5-3 
 */
package com.jadic;

import org.jboss.netty.buffer.ChannelBuffer;

public interface IDisposeData {
	
	public void disposeData(TcpChannel tcpChannel, ChannelBuffer channelBuffer);

}
