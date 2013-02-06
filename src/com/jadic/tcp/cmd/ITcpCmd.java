/**
 * @author Jadic
 * @created 2012-9-18 
 */
package com.jadic.tcp.cmd;

import org.jboss.netty.buffer.ChannelBuffer;

/**
 * @author Jadic
 *
 */
public interface ITcpCmd {

	public int getCmdSize();
	public int getCmdEndSize();//消息体后的长度
	public int getCmdHeadTailAndXorSize();//头尾和校验位的长度
	public int getCmdXorStartIndex();//计算校验位时开始的位置
	public boolean disposeData(ChannelBuffer channelBuffer);
	public boolean fillChannelBuffer(ChannelBuffer channelBuffer);
}
