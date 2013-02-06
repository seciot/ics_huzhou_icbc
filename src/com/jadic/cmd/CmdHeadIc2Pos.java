/**
 * @author Jadic
 * @created 2012-5-2 
 */
package com.jadic.cmd;

import org.jboss.netty.buffer.ChannelBuffer;

import com.jadic.utils.KKTool;

public abstract class CmdHeadIc2Pos extends CmdHead{
	
	private short retCode;

	public CmdHeadIc2Pos () {
		super();
		this.retCode = 0;
	}

	@Override
	public byte[] getBytes() {
		byte[] buf = super.getBytes();
		
		byte[] data = new byte[buf.length + 2];
		
		System.arraycopy(buf, 0, data, 0, buf.length);
		
		KKTool.short2BytesBigEndian(this.retCode, data, buf.length);
		
		return data;
	}

	@Override
	public boolean fillChannelBuffer(ChannelBuffer channelBuffer) {
		if (!super.fillChannelBuffer(channelBuffer))
			return false;
		try {
			channelBuffer.writeShort(retCode);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public int getCmdSize() {
		return super.getCmdSize() + 2;
	}
	
	protected int getCmdEndSize() {
		return 2;
	}

	public short getRetCode() {
		return retCode;
	}

	public void setRetCode(short retCode) {
		this.retCode = retCode;
	}
}
