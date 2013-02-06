/**
 * @author Jadic
 * @created 2012-10-16 
 */
package com.jadic.cmd;

import com.jadic.utils.ConstDefine;

public class PosData {
	
	private byte[] data;
	private String recvTime;
	private String posId;
	private int id;
	
	
	public PosData() {
		data = ConstDefine.BLANKPOSDATA;
		recvTime = ConstDefine.BLANKSTR;
		posId = ConstDefine.BLANKSTR;
		id = 0;
	}

	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}


	public byte[] getData() {
		return data;
	}


	public void setData(byte[] data) {
		this.data = data;
	}


	public String getRecvTime() {
		return recvTime;
	}


	public void setRecvTime(String recvTime) {
		this.recvTime = recvTime;
	}


	public String getPosId() {
		return posId;
	}


	public void setPosId(String posId) {
		this.posId = posId;
	}
	
}
