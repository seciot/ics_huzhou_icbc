/**
 * @author Jadic
 * @created 2012-5-2 
 */
package com.jadic.cmd;

import org.jboss.netty.buffer.ChannelBuffer;


public class CmdHeadPos2Ic extends CmdHead {

	private byte[] tId;
	private byte cmdFlag;
	private byte[] posId;
	
	public CmdHeadPos2Ic() {
		super();
		tId = new byte[6];
		this.cmdFlag = 0x00;
		posId = new byte[4];
	}
	
	public int getCmdSize() {
		return super.getCmdSize() + 11;
	}
	
	public int getCmdEndSize() {
		return 2;
	}
	
	public boolean disposeData(byte[] data) {
		try {
			if (data == null || data.length < this.getCmdSize())
				return false;

			if (super.disposeData(data)) {
				//POS机编号
				System.arraycopy(data, this.offset, this.tId, 0, this.tId.length);
				this.offset += 6;
				
				//命令码
				this.cmdFlag = data[this.offset];
				this.offset ++;
				
				System.arraycopy(data, this.offset, this.posId, 0, this.posId.length);
				this.offset += 4;
				
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			return false;
		}	
	}

	@Override
	public boolean disposeData(ChannelBuffer channelBuffer) {
		try {
			if (channelBuffer == null || channelBuffer.readableBytes() < this.getCmdSize()) {
				return false;
			}
			
			if (super.disposeData(channelBuffer)) {
				channelBuffer.readBytes(this.tId);
				this.cmdFlag = channelBuffer.readByte();
				channelBuffer.readBytes(this.posId);
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			return false;
		}
	}
	
	public byte[] gettId() {
		return tId;
	}

	public void settId(byte[] tId) {
		this.tId = tId;
	}

	public byte getCmdFlag() {
		return cmdFlag;
	}

	public void setCmdFlag(byte cmdFlag) {
		this.cmdFlag = cmdFlag;
	}

	@Override
	public short getCmdPackSize() {
		return 0;
	}

	public byte[] getPosId() {
		return posId;
	}

	public void setPosId(byte[] posId) {
		this.posId = posId;
	}

}
