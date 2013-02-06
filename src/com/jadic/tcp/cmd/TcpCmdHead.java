/**
 * @author Jadic
 * @created 2012-9-18 
 */
package com.jadic.tcp.cmd;

import org.jboss.netty.buffer.ChannelBuffer;

import com.jadic.utils.ConstDefine;

/**
 * @author Jadic
 *
 */
public class TcpCmdHead implements ITcpCmd{
	
	private byte msgFlag;//消息标识位
	private int cmdLen;//消息长度
	private byte cmdFlag;//消息命令字
	private int userId;//用户编号
	private byte[] userPass;//用户密码

	public TcpCmdHead() {
		this.msgFlag = ConstDefine.CMD_HEAD;
		this.cmdLen = 0;
		this.cmdFlag = 0;
		this.userId = 0;
		this.userPass = new byte[10];
	}

	@Override
	public int getCmdSize() {
		return 20;
	}
	
	@Override
	public int getCmdEndSize() {
		return 2;
	}

	@Override
	public int getCmdHeadTailAndXorSize() {
		return 3;
	}

	@Override
	public int getCmdXorStartIndex() {
		return 1;
	}

	@Override
	public boolean disposeData(ChannelBuffer channelBuffer) {
		try {
			if (channelBuffer == null || channelBuffer.readableBytes() < this.getCmdSize()) {
				return false;
			}
			
			this.msgFlag = channelBuffer.readByte();
			this.cmdLen = channelBuffer.readInt();
			this.cmdFlag = channelBuffer.readByte();
			this.userId = channelBuffer.readInt();
			channelBuffer.readBytes(this.userPass);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public boolean fillChannelBuffer(ChannelBuffer channelBuffer) {
		try {
			channelBuffer.writeByte(msgFlag);
			channelBuffer.writeInt(cmdLen);
			channelBuffer.writeByte(cmdFlag);
			channelBuffer.writeInt(userId);
			channelBuffer.writeBytes(this.userPass);
			return true;
		} catch (Exception e){
			return false;
		}
	}

	public byte getMsgFlag() {
		return msgFlag;
	}

	public void setMsgFlag(byte msgFlag) {
		this.msgFlag = msgFlag;
	}

	public int getCmdLen() {
		return cmdLen;
	}

	public void setCmdLen(int cmdLen) {
		this.cmdLen = cmdLen;
	}

	public byte getCmdFlag() {
		return cmdFlag;
	}

	public void setCmdFlag(byte cmdFlag) {
		this.cmdFlag = cmdFlag;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public byte[] getUserPass() {
		return userPass;
	}

	public void setUserPass(byte[] userPass) {
		this.userPass = userPass;
	}

}
