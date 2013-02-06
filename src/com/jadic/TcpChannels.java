/**
 * @author Jadic
 * @created 2012-4-10 
 */
package com.jadic;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.jadic.utils.KKLog;
import com.jadic.utils.KKTool;

public class TcpChannels {
	
	//private int type;//0:connected but not authenticated  1:authenticated
	
	private boolean isLoopTcpChannels = true;
	private ExecutorService threadPoolDisposeTcpData;
	//public final static long MAXOVERTIME = 60 * 60 * 1000;
	private long autoDisconnectMinutes = 60;//自动断开连接的时间
	private long autoDisconnectMilliseconds = autoDisconnectMinutes * 60 * 1000;//自动断开连接的时间
	
//	private int removeCount = 0;//
	
	private ConcurrentHashMap<Integer, TcpChannel> clients = null;
	
	private IDisposeData iDisposeData;
	
//	public TcpChannels() {
//		this.clients = new ConcurrentHashMap<Integer, TcpChannel>(100);
//	}
	
	public TcpChannels(int threadPoolSize) {
		this.clients = new ConcurrentHashMap<Integer, TcpChannel>(100);
//		if (this.type > 0) {
		threadPoolDisposeTcpData = Executors.newFixedThreadPool(threadPoolSize);
//		threadPoolDisposeTcpData = Executors.newCachedThreadPool();
//		} else if (this.type == 0) {
//			//start a thread to close tcpchannel which is not authenticated after maxovertime
//			new Thread(new Runnable() {
//				
//				Set<Entry<Integer, TcpChannel>> entrySet = null;
//				Iterator<Entry<Integer, TcpChannel>> eIter = null;
//				
//				@Override
//				public void run() {
//					while (isLoopTcpChannels) {
//						if (clients.size() > 0) {
//							entrySet = clients.entrySet();
//							eIter = entrySet.iterator();
//							while (eIter.hasNext()) {
//								Entry<Integer, TcpChannel> e = eIter.next();
//								TcpChannel tcpChannel = e.getValue();
//								if (tcpChannel != null && KKTool.isOvertime(tcpChannel.getLastConnTime(), MAXOVERTIME)) {
//									tcpChannel.close();
//								}
//							}
//						} 
//						
//						KKTool.sleepTime(5000);
//					}
//				}
//			}).start();
//		}
	}
	
	public void startThreadCheckAutoDisconnect(int autoDisconnectMins) {
		this.autoDisconnectMinutes = autoDisconnectMins;
		this.autoDisconnectMilliseconds = autoDisconnectMins * 60 * 1000;
		new Thread(new Runnable() {
			Set<Entry<Integer, TcpChannel>> entrySet = null;
			Iterator<Entry<Integer, TcpChannel>> eIter = null;
			@Override
			public void run() {
				while (isLoopTcpChannels) {
					if (clients.size() > 0) {
						entrySet = clients.entrySet();
						eIter = entrySet.iterator();
						while (eIter.hasNext()) {
							Entry<Integer, TcpChannel> entry = eIter.next();
							TcpChannel tcpChannel = entry.getValue();
							if (tcpChannel != null && KKTool.isOvertime(tcpChannel.getLastConnTime(), autoDisconnectMilliseconds)) {
								KKLog.info("Connection[" + tcpChannel.getChannel().getRemoteAddress() + "] has no data recvd over " + autoDisconnectMinutes + " minutes, disconnect this connection");//连接[" + tcpChannel.getChannel().getRemoteAddress() + "]长时间无数据，断开该连接
								tcpChannel.close();
							}
						}
					}

					KKTool.sleepTime(5000);
				}
			}
		}).start();
	}
	
	public void setIDisposeDataAndLogger(IDisposeData iDisposeData) {
		this.iDisposeData = iDisposeData;
	}
	
	public void disposeRecvdData(TcpChannel tcpChannel) {
		threadPoolDisposeTcpData.execute(new ThreadDisposeTcpChannelData(tcpChannel, this.iDisposeData));
	}
	
	public void addTcpChannel(TcpChannel tcpChannels) {
		clients.put(tcpChannels.getChannel().getId(), tcpChannels);
	}
	
	public boolean removeTcpChannel(Integer channelId) {
		TcpChannel tcpChannel = clients.remove(channelId);
		if (tcpChannel != null) {
			tcpChannel.destroy();
			tcpChannel = null;
//			if(++ this.removeCount > 20) {
//				System.gc();
//				this.removeCount = 0;
//			}
			
			return true;
		} else {
			return false;
		}
	}
	
	public TcpChannel getTcpChannel(int channelId) {
		return clients.get(channelId);
	}
	
	public int getCount() {
		return this.clients.size();
	}
	
	public void closeAllChannels() {
		Set<Map.Entry<Integer, TcpChannel>> channels = clients.entrySet();
		Iterator<Entry<Integer, TcpChannel>> ite = channels.iterator();
		while (ite.hasNext()) {
			Entry<Integer, TcpChannel> entry = ite.next();
			TcpChannel tcpChannel = entry.getValue();
			tcpChannel.close();
		}
		clients.clear();
	}

	public void stopThreadPoolWorking() {
		this.threadPoolDisposeTcpData.shutdown();
	}
	
	public boolean isThreadPoolTerminated() {
		return this.threadPoolDisposeTcpData.isTerminated();
	}
}
