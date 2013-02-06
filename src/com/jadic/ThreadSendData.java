/**
 * @author Jadic
 * @created 2012-10-17 
 */
package com.jadic;

import java.util.List;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import com.jadic.cmd.PosData;
import com.jadic.db.DBOper;
import com.jadic.tcp.cmd.resp.CmdSendDataCount_S2C;
import com.jadic.tcp.cmd.resp.CmdSendDataOK_S2C;
import com.jadic.tcp.cmd.resp.CmdSendData_S2C;
import com.jadic.utils.KKLog;
import com.jadic.utils.KKTool;

/**
 * @author Jadic
 *
 */
public class ThreadSendData implements Runnable {
	
	private TcpChannel tcpChannel;
	private DBOper dbOper;
	
	public ThreadSendData (TcpChannel tcpChannel, DBOper dbOper) {
		this.tcpChannel = tcpChannel;
		this.dbOper = dbOper;
	}

	@Override
	public void run() {
		if (tcpChannel == null || dbOper == null) {
			return;
		}
		List<PosData> posDatas = tcpChannel.getPosDataListUnsend();
		if (posDatas.size() <= 0) {//当前连接下没有未发送数据则从数据库中加载
			dbOper.getPosDataUnsend(posDatas);
		}
		
		ChannelBuffer channelBuffer = null;
		//先发送记录数
		CmdSendDataCount_S2C cmdSendDataCount = new CmdSendDataCount_S2C();
		cmdSendDataCount.setDataCount(posDatas.size());
		channelBuffer = ChannelBuffers.buffer(cmdSendDataCount.getCmdSize());
		if (cmdSendDataCount.fillChannelBuffer(channelBuffer)) {
			tcpChannel.sendData(channelBuffer);
		}
		KKLog.info("通知用户本次交易数据记录数:" + posDatas.size());
		KKTool.sleepTime(1000);
		
		if (posDatas.size() <= 0) {
			return;
		}
		
		//发明细
		CmdSendData_S2C cmdSendData = new CmdSendData_S2C();
		for (int i = 0; i < posDatas.size(); i ++) {
			channelBuffer = ChannelBuffers.buffer(cmdSendData.getCmdSize());
			cmdSendData.setData(posDatas.get(i).getData());
			if (cmdSendData.fillChannelBuffer(channelBuffer)){
				tcpChannel.sendData(channelBuffer);
			}
			KKLog.info("正在向用户[" + tcpChannel.getUserId() + "]发送第" + (i + 1) + "条交易数据");
			KKTool.sleepTime(100);
		}
		
		//发送结束
		CmdSendDataOK_S2C cmdSendDataOK = new CmdSendDataOK_S2C();
		cmdSendDataOK.setDataCount(posDatas.size());
		channelBuffer = ChannelBuffers.buffer(cmdSendDataOK.getCmdSize());
		if (cmdSendDataOK.fillChannelBuffer(channelBuffer)) {
			tcpChannel.sendData(channelBuffer);
		}
		KKLog.info("向用户[" + tcpChannel.getUserId() + "]发送交易数据完毕.");
	}

}
