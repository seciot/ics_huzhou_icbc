/**
 * @author Jadic
 * @created 2012-5-2 
 */
package com.jadic.cmd;

import org.jboss.netty.buffer.ChannelBuffer;

import com.jadic.utils.KKTool;

public abstract class CmdHead implements ICmd {

	private byte type;
	private byte type2;
	private short packLen;
	private short packSNo;

	protected int offset;

	public CmdHead() {
		this.type = 0;
		this.type2 = 0;
		this.packLen = 0;
		this.packSNo = 0;
		offset = 0;
	}

	public int getCmdSize() {
		return 2 + 2 + 2;
	}

	@Override
	public byte[] getBytes() {
		byte[] data = new byte[6];
		int i = 0;
		data[i] = type;
		i ++;
		
		data[i] = type2;
		i ++;
		
		KKTool.short2BytesBigEndian(this.packLen, data, i);
		i += 2;
		
		KKTool.short2BytesBigEndian(this.packSNo, data, i);
		
		return data;
	}
	
	@Override
	public boolean disposeData(byte[] data) {
		try {
			if (data == null || data.length < this.getCmdSize())
				return false;
			this.type = data[offset];//包类型
			offset ++;
			
			this.type2 = data[offset];
			offset ++;
			
			//this.setPackLen(KKTool.getShortBigEndian(data[offset], data[offset + 1]));//包长度
			offset += 2;
			
			this.packSNo = KKTool.getShortBigEndian(data[offset], data[offset + 1]);//包流水号
			offset += 2;
			
			return true;
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
			
			this.type = channelBuffer.readByte();
			this.type2 = channelBuffer.readByte();
			channelBuffer.skipBytes(2);
			this.packSNo = channelBuffer.readShort();
			
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public boolean fillChannelBuffer(ChannelBuffer channelBuffer) {
		try {
			channelBuffer.writeByte(type);
			channelBuffer.writeByte(type2);
			channelBuffer.writeShort(packLen);
			channelBuffer.writeShort(packSNo);
			return true;
		} catch (Exception e){
			return false;
		}
	}
	
	public byte getType() {
		return type;
	}

	public void setType(byte type) {
		this.type = type;
	}

	public byte getType2() {
		return type2;
	}

	public void setType2(byte type2) {
		this.type2 = type2;
	}

	public short getPackLen() {
		return packLen;
	}

	public void setPackLen(short packLen) {
		this.packLen = packLen;
	}

	public short getPackSNo() {
		return packSNo;
	}

	public void setPackSNo(short packSNo) {
		this.packSNo = packSNo;
	}

}
