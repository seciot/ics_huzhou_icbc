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
 * 登录应答
 */
public class CmdLoginResp_S2C extends TcpCmdHead {

	private byte loginRet;//登录结果
	
	public CmdLoginResp_S2C() {
		super();
		this.loginRet = 0;
		this.setCmdLen(ConstDefine.MIN_CMD_LENGTH - 2 + 1);
		this.setCmdFlag(ConstDefine.CMD_LOGIN_RESP);
	}

	@Override
	public int getCmdSize() {
		return super.getCmdSize() + 1 + this.getCmdEndSize();
	}

	@Override
	public boolean fillChannelBuffer(ChannelBuffer channelBuffer) {
		if (!super.fillChannelBuffer(channelBuffer))
			return false;
		try {
			channelBuffer.writeByte(loginRet);
			byte[] d = new byte[this.getCmdSize() - this.getCmdHeadTailAndXorSize()];//完整的长度减去校验位和头尾
			channelBuffer.getBytes(this.getCmdXorStartIndex(), d, 0, d.length);
			channelBuffer.writeByte(KKTool.getXorSum(d));
			channelBuffer.writeByte(ConstDefine.CMD_END);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public byte getLoginRet() {
		return loginRet;
	}

	public void setLoginRet(byte loginRet) {
		this.loginRet = loginRet;
	}
	
}
