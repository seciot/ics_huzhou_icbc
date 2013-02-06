/**
 * @author Jadic
 * @created 2012-4-10 
 */
package com.jadic;

import java.util.ArrayList;
import java.util.List;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;

import com.jadic.cmd.PosData;
import com.jadic.utils.KKLog;
import com.jadic.utils.KKTool;

public class TcpChannel {

	private Channel channel;
	private long lastConnTime;
	private byte[] posId;
	private boolean isAuthed;
	private ChannelBuffer channelBuffer = null;
	
	private int userId;
	
	private List<PosData> posDataListUnsend;
	
	volatile boolean isRecvdData;				//有接收到新数据
	volatile boolean isDisposingData;			//线程是否正在处理数据
	volatile boolean isRecvdDataWhenDisposing;  //在线程在处理数据时是否有新数据接收
	volatile boolean isDiscardedData;			//是否有清除已解析过数据
	volatile boolean isThrewData;				//是否有抛弃数据
	volatile int readerIndexBeforeDiscarded;	//清除已解析数据前的readerIndex;
	volatile boolean isDestroyed;				
	
	public TcpChannel(Channel channel) {
		this.channel = channel;
		this.lastConnTime = System.currentTimeMillis();
		this.isRecvdData = false;
		this.isRecvdDataWhenDisposing = false;
		this.isDiscardedData = false;
		this.isThrewData = false;
		this.isDestroyed = false;
		this.readerIndexBeforeDiscarded = 0;
		//channelBuffer = ChannelBuffers.buffer(1024 * 32);
		posDataListUnsend = new ArrayList<PosData>();
	}
	
	public boolean checkAuthed(ChannelBuffer buffer) {
		synchronized (channelBuffer) {
			channelBuffer.writeBytes(buffer);
		}		
		this.isAuthed = true;
		return this.isAuthed;
	}
	
	public void recvData(ChannelBuffer buffer) {
		if (!isRecvdData) {
			channelBuffer = ChannelBuffers.buffer(1024 * 32);
			isRecvdData = true;
		}
		
		KKLog.recvData("[" + getChannel().getRemoteAddress() + "](" + buffer.readableBytes()+ "):" + KKTool.channelBufferToHexStr(buffer));
		this.lastConnTime = System.currentTimeMillis();
		synchronized (channelBuffer) {
			if (channelBuffer.writableBytes() < buffer.readableBytes()) {
				this.readerIndexBeforeDiscarded = channelBuffer.readerIndex();//记录该位置以便在解析数据后进行相关的偏移
				this.isDiscardedData = true;
				channelBuffer.discardReadBytes();
				//KKLog.warn("[" + this.channel.getRemoteAddress() + "]剩余位置不够存放新收到的数据，需清除已解析的数据");
				//如果清除已解析数据后，仍不够放的，则需抛弃部分数据
				if (channelBuffer.writableBytes() < buffer.readableBytes()) {
					if (buffer.readableBytes() > channelBuffer.capacity()) {
						KKLog.warn("Recv data size is over buffer's capacity, throw recv data");
						return;
					}					
					int i = buffer.readableBytes() - channelBuffer.writableBytes();//need to skip
					int j = channelBuffer.writerIndex() - channelBuffer.readerIndex();//skip max bytes
					int skipByteCount = (i < j ? i : j);
					channelBuffer.skipBytes(skipByteCount);
					channelBuffer.discardReadBytes();
					this.isThrewData = true;
					KKLog.warn("[" + this.channel.getRemoteAddress() + "]有大量数据未解析，有新数据接收，抛弃部分前面未解析的数据");
				}
			}
			
			channelBuffer.writeBytes(buffer);//should no IndexOutOfBoundsException
			isRecvdDataWhenDisposing = isDisposingData;
		}
	}
	
	public void sendData(ChannelBuffer channelBuffer) {
		if (this.channel == null || this.isDestroyed) {
			return;
		}
		KKLog.sendData("[" + this.getPosIdStr() + " " + this.channel.getRemoteAddress() + "](" + channelBuffer.readableBytes()+ "):" + KKTool.channelBufferToHexStr(channelBuffer));
		this.channel.write(channelBuffer);
	}
	
	public void setChannelBufferReaderIndex(int newIndex) {
		if (channelBuffer == null) {
			return;
		}
		
		synchronized (channelBuffer) {
			try {
				channelBuffer.readerIndex(newIndex);
			} catch (IndexOutOfBoundsException e) {
				channelBuffer.readerIndex(0);
				KKLog.error("setChannelBufferReaderIndex error, readerIndex=" + channelBuffer.readerIndex() + ",writerIndex=" + channelBuffer.writerIndex() + ",newIndex=" + newIndex);
			}
		}
	}
	
	public void skipChannelBufferBytes(int bytesCount) {
		if (channelBuffer == null) {
			return ;
		}
		synchronized (channelBuffer) {
			try {
				channelBuffer.skipBytes(bytesCount);
			} catch (IndexOutOfBoundsException e) {
				KKLog.error("TcpChannel skipChannelBufferBytes err:" + KKTool.getExceptionTip(e));
			}
		}
	}
	
	public ChannelBuffer getChannelBufferCopy() {
		if (channelBuffer == null)
			return null;
		
		synchronized (channelBuffer) {
			try {
				if (this != null && !this.isDestroyed && channelBuffer.readable()) {
					return channelBuffer.duplicate();
				} else {
					return null;
				}
			} catch (Exception e) {
				KKLog.error("getChannelBufferCopy error:" + KKTool.getExceptionTip(e));
				this.channelBuffer.clear();
				return null;
			}
		}
	}
	
	public void close() {
		try {
			this.channel.unbind();
			this.channel.close();
		} catch (Exception e) {
		}
	}

	public Channel getChannel() {
		return this.channel;
	}

	public long getLastConnTime() {
		return lastConnTime;
	}

	public void setLastConnTime(long lastConnTime) {
		this.lastConnTime = lastConnTime;
	}

	public ChannelBuffer getChannelBuffer() {
		return channelBuffer;
	}

	public boolean isAuthed() {
		return isAuthed;
	}

	public void setAuthed(boolean isAuthed) {
		this.isAuthed = isAuthed;
	}
	
	public byte[] getPosId() {
		return posId;
	}

	public void setPosId(byte[] posId) {
		this.posId = posId;
	}
	
	public String getPosIdStr() {
		if (this.posId != null) {
			return KKTool.byteArrayToHexStr(this.posId);
		}
		return "?";
	}

	public void destroy() {
		if (this.channelBuffer != null) {
			synchronized (channelBuffer) {
				this.channelBuffer.clear();
				this.channelBuffer = null;
			}
		}
		this.isDestroyed = true;
	}

	public List<PosData> getPosDataListUnsend() {
		return posDataListUnsend;
	}

	public void setPosDataListUnsend(List<PosData> posDataListUnsend) {
		this.posDataListUnsend = posDataListUnsend;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}
}
