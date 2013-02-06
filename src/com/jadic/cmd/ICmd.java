/**
 * @author Jadic
 * @created 2012-5-3 
 */
package com.jadic.cmd;

import org.jboss.netty.buffer.ChannelBuffer;

public interface ICmd {
	
	public int getCmdSize();
	public boolean disposeData(byte[] data);
	public boolean disposeData(ChannelBuffer channelBuffer);
	public byte[] getBytes();
	public boolean fillChannelBuffer(ChannelBuffer channelBuffer);
	public short getCmdPackSize();
}
