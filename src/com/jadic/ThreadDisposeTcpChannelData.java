/**
 * @author Jadic
 * @created 2012-4-27 
 */
package com.jadic;

import org.jboss.netty.buffer.ChannelBuffer;

import com.jadic.utils.ConstDefine;
import com.jadic.utils.KKLog;
import com.jadic.utils.KKTool;

public class ThreadDisposeTcpChannelData implements Runnable {
	
	private TcpChannel tcpChannel;
	private IDisposeData iDisposeData;
	
	public ThreadDisposeTcpChannelData(TcpChannel tcpChannel, IDisposeData iDisposeData) {
		this.tcpChannel = tcpChannel;
		this.iDisposeData = iDisposeData;
	}

	@Override
	public void run() {
		if (this.tcpChannel == null || this.tcpChannel.isDestroyed) {
			return;
		}
		short count = 0;
		tcpChannel.isDisposingData = true;
		tcpChannel.isRecvdDataWhenDisposing = false;//开始处理时就将该状态置为false
		disposeData();
		count ++;
		while (this.tcpChannel != null && tcpChannel.isRecvdDataWhenDisposing) {//如果在处理数据的过程中有新的数据过来，则接着处理
			
			//System.out.println(Thread.currentThread().getName() + " is disposing data and new data is recvd ");
			tcpChannel.isRecvdDataWhenDisposing = false;
			disposeData();
			// 暂时不考虑如果某通道一直发数据占用处理线程的情况，如果后面存在这种情况，
			// 则考虑加入一个计数器，来限制某个线程处理某个通道的次数
			if(++count > 5) {
				KKLog.warn("连续处理同一通道[" + this.tcpChannel.getChannel().getRemoteAddress() + "]数据5次*****************");
			}
		}
		tcpChannel.isDisposingData = false;
		this.tcpChannel = null;
	}
	
	private void disposeData() {
		this.tcpChannel.isDiscardedData = false;
		this.tcpChannel.isThrewData = false;
		//先将当前缓存去中的数据复制一份，readerIndex与writerIndex一致
		ChannelBuffer buffer = this.tcpChannel.getChannelBufferCopy();
		if (buffer == null) {
			return;
		}
		
		try {
			int bufLen = 0;
			int oldReaderIndex = 0;//每次新的解析前记录下readerIndex;
			byte cmdHead = 0;
			int packLen = 0;
			while (!this.tcpChannel.isDestroyed && (bufLen = buffer.readableBytes()) >= 22) {
				try {
					oldReaderIndex = buffer.readerIndex();
					cmdHead = buffer.getByte(oldReaderIndex);
					if (cmdHead == ConstDefine.CMD_HEAD) {//按MCU数据中心与工行系统通讯协议解析
						packLen = buffer.getInt(oldReaderIndex + 1);
						if (packLen >= ConstDefine.MIN_CMD_LENGTH - 2) {//长度大于8
							if (packLen + 2 > bufLen) {//包长度+2为完整包的长度								
								//当数据包长度较大时，取出命令字核对是否合法
								byte cmdFlag = buffer.getByte(oldReaderIndex + 5);//
								if(!isCmdFlagValid(cmdFlag)) {
									buffer.readerIndex(oldReaderIndex + 1);
									continue;
								}
								
								if (packLen - 20 > 1024) {//为防止异常数据，如果当前协议体数据长度大于1024则认为是不合法数据，跳过继续解析
									buffer.readerIndex(oldReaderIndex + 1);
									continue;
								}//否则将readerIndex退回这次解析前的位置
							
								buffer.readerIndex(oldReaderIndex);
								break;
							}
							
							if (buffer.getByte(oldReaderIndex + packLen + 2 - 1) != ConstDefine.CMD_END) {//判断包尾是否合法
								buffer.readerIndex(oldReaderIndex + 1);
								continue;
							}
							
							//先校验
							if (KKTool.checkXorValid(buffer, oldReaderIndex + 1, oldReaderIndex + packLen + 2 - 3, buffer.getByte(oldReaderIndex + packLen + 2 - 2))) {
								try {
									//业务解析
									this.iDisposeData.disposeData(tcpChannel, buffer);//此处直接将buffer传入，避免每次解析数据都拷贝一份数组，同时在后面的具体命令解析过程中，代码编写也更加轻松
									buffer.readerIndex(oldReaderIndex + packLen + 2);//强制将readerIndex位置赋到一包完整数据后，防止在解析过程中导致索引位置偏差
								} catch (Exception e) {
									KKLog.error("disposeData error:" + KKTool.getExceptionTip(e));
								}
							} else {//否则继续重新解析
								buffer.readerIndex(oldReaderIndex + 1);
								continue;
							}
						} else {//否则继续重新解析
							buffer.readerIndex(oldReaderIndex + 1);
						}
					} else {//否则继续重新解析
						buffer.readerIndex(oldReaderIndex + 1);
					}
				} catch (Exception e) {
					KKLog.error("Disposedata while loop error:" + e.getMessage());
				}
			}
			if (tcpChannel == null || tcpChannel.isDestroyed) {
				return;
			}
			
			try {
				if (this.tcpChannel.isThrewData) {//该通道如果有抛弃数据的行为，则此处直接从新的位置重新解析
					this.tcpChannel.isThrewData = false;
					this.tcpChannel.setChannelBufferReaderIndex(0);
				} else if (this.tcpChannel.isDiscardedData) {//如果在解析数据的过程中发生了清除过已解析的数据，则TcpChannel的当前缓冲区要跳过相应已解析的数据
					this.tcpChannel.isDiscardedData = false;
					this.tcpChannel.skipChannelBufferBytes(buffer.readerIndex() - this.tcpChannel.readerIndexBeforeDiscarded);
				} else {
					this.tcpChannel.setChannelBufferReaderIndex(buffer.readerIndex());
				}
			} catch (Exception e) {
				KKLog.error("Disposedata after while loop error:" + e.getMessage());
			}
		} catch (Exception e) {
			KKLog.error("[" + tcpChannel.getChannel() + "]线程解析数据异常:" + e.getMessage());
		}
	}

	/**
	 * @param cmdFlag
	 * @return
	 */
	private boolean isCmdFlagValid(byte cmdFlag) {
		switch (cmdFlag) {
		case ConstDefine.CMD_LOGIN://登录
		case ConstDefine.CMD_GETDATA://获取数据
		case ConstDefine.CMD_GETDATA_SUCC://本次获取数据成功
			return true;
		default:
			return false;
		}
	}

}
