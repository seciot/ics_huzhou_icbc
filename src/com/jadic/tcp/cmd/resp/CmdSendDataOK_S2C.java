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
public class CmdSendDataOK_S2C extends TcpCmdHead {

	private int dataCount;
	
	public CmdSendDataOK_S2C() {
		super();
		this.dataCount = 0;
		this.setCmdLen(ConstDefine.MIN_CMD_LENGTH - 2 + 4);
		this.setCmdFlag(ConstDefine.CMD_SEND_OK);
	}

	@Override
	public int getCmdSize() {
		return super.getCmdSize() + 4 + this.getCmdEndSize();
	}

	@Override
	public boolean fillChannelBuffer(ChannelBuffer channelBuffer) {
		if (!super.fillChannelBuffer(channelBuffer))
			return false;
		try {
			channelBuffer.writeInt(dataCount);
			byte[] d = new byte[this.getCmdSize() - this.getCmdHeadTailAndXorSize()];//完整的长度减去校验位和头尾
			channelBuffer.getBytes(this.getCmdXorStartIndex(), d, 0, d.length);
			channelBuffer.writeByte(KKTool.getXorSum(d));
			channelBuffer.writeByte(ConstDefine.CMD_END);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public int getDataCount() {
		return dataCount;
	}

	public void setDataCount(int dataCount) {
		this.dataCount = dataCount;
	}

}
