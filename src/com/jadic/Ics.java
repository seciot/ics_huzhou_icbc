/**
 * @author Jadic
 * @created 2012-6-12 
 */
/**
 * 
 */
package com.jadic;

import java.util.List;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import com.jadic.cmd.PosData;
import com.jadic.db.DBOper;
import com.jadic.tcp.cmd.req.CmdGetDataSucc_C2S;
import com.jadic.tcp.cmd.req.CmdGetData_C2S;
import com.jadic.tcp.cmd.req.CmdLogin_C2S;
import com.jadic.tcp.cmd.resp.CmdLoginResp_S2C;
import com.jadic.utils.ConstDefine;
import com.jadic.utils.KKLog;
import com.jadic.utils.KKTool;
import com.jadic.utils.SysParams;

/**
 * @author Jadic
 *
 */
public class Ics implements IDisposeData, IKKTimer {
	
	private DBOper dbOper;
	private TcpServer tcpServer;
	private KKTimer kkTimer;

	public final static SysParams sysParams = new SysParams();

	public static void main(String[] args) {
		new Ics();
	}

	public Ics() {
		this.loadSysParam();
		dbOper = DBOper.getDBOper(sysParams);
		this.startService();
	}
	
	private void loadSysParam() {
		sysParams.loadSysParams();
		KKLog.info("*************************SysParams*************************");
		KKLog.info("Local Tcp Port : " + sysParams.getLocalTcpPort());
		KKLog.info("AutoDisconnectMinutes : " + sysParams.getAutoDisconnectMinutes());
		KKLog.info("JDBC Driver : " + sysParams.getJdbcDriver());
		KKLog.info("JDBC Url : " + sysParams.getJdbcUrl());
		KKLog.info("DB UserName : " + sysParams.getDbUserName());
		KKLog.info("DB UserPass : " + sysParams.getDbUserPass());
		KKLog.info("Client Ip Filter:" + sysParams.getClientIp());
		KKLog.info("*************************SysParams*************************");
	}
	
	private void startService() {
		this.startTcpServer();
		this.startKKTimer();
		
		this.tcpServer.start();
	}

	private void startKKTimer() {
		kkTimer = new KKTimer(this);
		kkTimer.setDelay(60 * 1000L);
		kkTimer.setPeriod(45 * 1000L);
		kkTimer.start();
	}
	
	private void startTcpServer() {
		this.tcpServer = new TcpServer(sysParams.getLocalTcpPort(), sysParams.getAutoDisconnectMinutes(), this);
	}

	public void disposeData(TcpChannel tcpChannel, ChannelBuffer channelBuffer) {
		try {
			if (tcpChannel == null || channelBuffer == null || channelBuffer.readableBytes() < 22) {
				return;
			}
			
			byte cmdFlag = channelBuffer.getByte(channelBuffer.readerIndex() + 5);
			
			//根据命令字来判断命令类型
			switch (cmdFlag) {
			case ConstDefine.CMD_LOGIN://登录
				CmdLogin_C2S cmdLogin = new CmdLogin_C2S();
				if (cmdLogin.disposeData(channelBuffer)) {
					dealCmdLogin(cmdLogin, tcpChannel);
				}
				KKLog.info("用户[" + cmdLogin.getUserId() + "]登录,用户密码:" + new String(cmdLogin.getUserPass()));
				break;
			case ConstDefine.CMD_GETDATA://获取数据
				CmdGetData_C2S cmdGetData = new CmdGetData_C2S();
				if (cmdGetData.disposeData(channelBuffer)) {
					KKLog.info("用户[" + cmdGetData.getUserId() + "]向服务器请求交易数据");
					dealCmdGetData(cmdGetData, tcpChannel);
				}
				break;
			case ConstDefine.CMD_GETDATA_SUCC://本次获取数据成功
				CmdGetDataSucc_C2S cmdGetDataSucc = new CmdGetDataSucc_C2S();
				if (cmdGetDataSucc.disposeData(channelBuffer)) {
					KKLog.info("用户[" + cmdGetDataSucc.getUserId() + "]通知服务器获取数据成功");
					dealCmdGetDataSucc(cmdGetDataSucc, tcpChannel);
				}
				break;
			default:
				//不认识的命令字
				KKLog.warn("Unknown cmd[" + KKTool.byteToHexStr(cmdFlag) + "],from[" + tcpChannel.getChannel().getRemoteAddress() + "] " + tcpChannel.getPosIdStr());
				break;	
			}
		} catch (Exception e) {
			KKLog.error("Ics.disposData Err:" + KKTool.getExceptionTip(e));
		}
	}
	
	private void dealCmdLogin(CmdLogin_C2S cmdLogin, TcpChannel tcpChannel) {
		CmdLoginResp_S2C cmdLoginResp = new CmdLoginResp_S2C();
		tcpChannel.setUserId(cmdLogin.getUserId());
		//fillCmdHead(cmdLogin, cmdLoginResp);
		ChannelBuffer channelBuffer = ChannelBuffers.buffer(cmdLoginResp.getCmdSize());
		if (cmdLoginResp.fillChannelBuffer(channelBuffer)) {
			tcpChannel.sendData(channelBuffer);
		}
	}
	
	private void dealCmdGetData(CmdGetData_C2S cmdGetData, TcpChannel tcpChannel) {
		tcpChannel.setUserId(cmdGetData.getUserId());
		new Thread(new ThreadSendData(tcpChannel, dbOper)).start();
	}
	
	private void dealCmdGetDataSucc(CmdGetDataSucc_C2S cmdGetDataSucc, TcpChannel tcpChannel) {
		tcpChannel.setUserId(cmdGetDataSucc.getUserId());
		List<PosData> posDatas = tcpChannel.getPosDataListUnsend();
		if (dbOper.updatePosDataState(posDatas)) {
			posDatas.clear();
		}
	}
	
//	private void fillCmdHead(TcpCmdHead cmdC2S, TcpCmdHead cmdS2C) {
//		if (cmdC2S != null && cmdS2C != null) {
//			cmdS2C.setUserId(cmdC2S.getUserId());
//			cmdS2C.setUserPass(cmdC2S.getUserPass());
//		}
//	}

	@Override
	public void doOnTimer() {
		
	}
}

