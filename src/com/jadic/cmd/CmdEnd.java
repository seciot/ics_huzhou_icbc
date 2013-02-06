/**
 * @author Jadic
 * @created 2012-5-2 
 */
package com.jadic.cmd;

/**
 * 命令尾 2字节校验位
 * @author Jadic
 *
 */
public class CmdEnd {
	
	public byte lrc1;
	public byte lrc2;
	
	public CmdEnd () {
		
	}

	public byte getLrc1() {
		return lrc1;
	}

	public void setLrc1(byte lrc1) {
		this.lrc1 = lrc1;
	}

	public byte getLrc2() {
		return lrc2;
	}

	public void setLrc2(byte lrc2) {
		this.lrc2 = lrc2;
	}
}
