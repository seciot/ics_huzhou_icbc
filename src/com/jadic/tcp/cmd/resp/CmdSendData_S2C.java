/**
 * @author Jadic
 * @created 2012-9-18 
 */
package com.jadic.tcp.cmd.resp;

import org.jboss.netty.buffer.ChannelBuffer;

import com.jadic.tcp.cmd.TcpCmdHead;
import com.jadic.utils.ConstDefine;
import com.jadic.utils.KKTool;

/**
 * 发送交易数据条数
 */
public class CmdSendData_S2C extends TcpCmdHead {

	private byte[] data;//交易数据
	
	public CmdSendData_S2C() {
		super();
		this.setCmdLen(ConstDefine.MIN_CMD_LENGTH - 2 + 256);
		this.setCmdFlag(ConstDefine.CMD_SEND_DATA);
	}

	@Override
	public int getCmdSize() {
		return super.getCmdSize() + 256 + this.getCmdEndSize();
	}

	@Override
	public boolean fillChannelBuffer(ChannelBuffer channelBuffer) {
		if (data == null || !super.fillChannelBuffer(channelBuffer))
			return false;
		try {
			channelBuffer.writeBytes(data);
			byte[] d = new byte[this.getCmdSize() - this.getCmdHeadTailAndXorSize()];//完整的长度减去校验位和头尾
			channelBuffer.getBytes(this.getCmdXorStartIndex(), d, 0, d.length);
			channelBuffer.writeByte(KKTool.getXorSum(d));
			channelBuffer.writeByte(ConstDefine.CMD_END);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

}
