/**
 * @author Jadic
 * @created 2012-9-18 
 */
package com.jadic.tcp.cmd.resp;

import org.jboss.netty.buffer.ChannelBuffer;

import com.jadic.tcp.cmd.TcpCmdHead;

/**
 * 平台发送的通用应答
 */
public class CmdTYRet_S2C extends TcpCmdHead {
	private short snoResp;//应答流水号
	private short cmdFlagResp;//应答命令字
	private byte ret;//应答结果
	
	public CmdTYRet_S2C() {
		super();
		this.snoResp = 0;
		this.cmdFlagResp = 0;
		this.ret = 0;
	}

	@Override
	public int getCmdSize() {
		return super.getCmdSize() + 5 + this.getCmdEndSize();
	}

	@Override
	public boolean disposeData(ChannelBuffer channelBuffer) {
		try {
			if (channelBuffer == null || channelBuffer.readableBytes() < this.getCmdSize())
				return false;
			
			if (super.disposeData(channelBuffer)) {
				this.snoResp = channelBuffer.readShort();
				this.cmdFlagResp = channelBuffer.readShort();
				this.ret = channelBuffer.readByte();
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			return false;
		}
	}

	public short getSnoResp() {
		return snoResp;
	}

	public short getCmdFlagResp() {
		return cmdFlagResp;
	}

	public byte getRet() {
		return ret;
	}

}
