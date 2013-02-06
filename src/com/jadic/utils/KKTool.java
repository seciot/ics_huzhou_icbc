/**
 * @author Jadic
 * @created 2012-4-27 
 */
package com.jadic.utils;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.jboss.netty.buffer.ChannelBuffer;

public class KKTool {
	
    /*数据的大小端标识*/
    public static final byte BIT_BIGENDIAN = 0;
    public static final byte BIT_LITTLEENDIAN = 1;
    
    /*时间*/
    public static final int MILLISECOND = 1;
    public static final int SECOND_MILLISECONDS = 1000 * MILLISECOND;
    public static final int MINUTE_MILLISECONDS = 60 * SECOND_MILLISECONDS;
    public static final int HOUR_MILLISECONDS = 60 * MINUTE_MILLISECONDS;
    public static final int DAY_MILLISECONDS = 24 * HOUR_MILLISECONDS;
    
    /**
     * 字节转整型
     * @param b
     * @return
     */
    public static int byteToInt(byte b) {
        return b >= 0 ? b : 256 + b;
    }

    public static byte byteToBCD(byte b) {
        return (byte)((byte)((b/10)<<4) + (byte)(b%10));
    }
    /**
     * 截取字节数组转成字符串
     * 示例:0x10 0x12 0x01  返回  161801
     *      0xA0 0xB1 0x12  返回  16017718
     * @param data        字节数组
     * @param startIndex  截取开始的位置
     * @param len         截取的长度
     * @return
     */
    public static String bytesToString(byte[] data, int startIndex, int len) {
        StringBuffer ret = new StringBuffer();
        byte b = 0;
        int j = 0;
        for (int i = startIndex; (i < data.length) && (i < len); i ++) {
            b = (byte)data[i];
            j = (b >= 0 ? b : b + 256);
            ret.append(j >= 10 ? "" + j : "0" + j);
        }
        return ret.toString();
    }

    /**
     * 从字节数组中截取4个字节转成整型，不足4个字节不进行转换返回0
     * @param data   a2 00 00 00 04
     * @param startIndex  截取开始的下标
     * @param bigOrLittleEndian 大小端 0:大端 1:小端
     * @return
     */
    public static int getInt(byte[] data, int startIndex, byte bigOrLittleEndian) {
        int ret = 0;
        int tmp = 0;
        if ((startIndex >= 0) && (startIndex + 3 < data.length)) {
            if (bigOrLittleEndian == 0) {
                for (int i = startIndex, j= 0; i < startIndex + 4; i ++, j++) {
                    tmp = data[i] >= 0 ? data[i] : 256 + data[i];
                    ret += tmp << (24 - 8 * j);
                }
            } else {
                for (int i = startIndex, j= 0; i < startIndex + 4; i ++, j++) {
                    tmp = data[i] >= 0 ? data[i] : 256 + data[i];
                    ret += tmp << (8 * j);
                }
            }
        }
        return ret;
    }
    
    public static int getIntBigEndian(byte[] data, int startIndex) {
    	return getInt(data, startIndex, BIT_BIGENDIAN);
    }

    /**
     * 从字节数组中截取2个字节转成short，不足2个字节不进行转换返回0
     * @param data   a2 00 00 00 04
     * @param startIndex  截取开始的下标
     * @param bigOrLittleEndian 大小端 0:大端 1:小端
     * @return
     */
    public static short getShort(byte[] data, int startIndex, byte bigOrLittleEndian) {
        short ret = 0;
        int tmp = 0;
        if ((startIndex >= 0) && (startIndex + 1 < data.length)) {
            if (bigOrLittleEndian == 0) {
                for (int i = startIndex, j= 0; i < startIndex + 2; i ++, j++) {
                    tmp = data[i] >= 0 ? data[i] : 256 + data[i];
                    ret += (short)tmp << (8 - 8 * j);
                }
            } else {
                for (int i = startIndex, j= 0; i < startIndex + 2; i ++, j++) {
                    tmp = data[i] >= 0 ? data[i] : 256 + data[i];
                    ret += (short)tmp << (8 * j);
                }
            }
        }
        return ret;
    }

    public static short getShort(byte highByte, byte lowByte, byte bigOrLittle) {
        short ret = 0;
        int tmp = 0;
        if (bigOrLittle == 0) {
            tmp = highByte >= 0 ? highByte : 256 + highByte;
            ret += (short)tmp << 8;
            tmp = lowByte >= 0 ? lowByte : 256 + lowByte;
            ret += (short)tmp;
        } else {
            tmp = lowByte >= 0 ? lowByte : 256 + lowByte;
            ret += (short)tmp << 8;
            tmp = highByte >= 0 ? highByte : 256 + highByte;
            ret += (short)tmp;
        }
        return ret;
    }
    
    public static short getShortBigEndian(byte highByte, byte lowByte) {
    	return getShort(highByte, lowByte, (byte)0);
    }

    /**
     * 截取指定开始位置指定长度的字节数组转成字符串
     * @param data          字节数组
     * @param startIndex    截取开始位置
     * @param len           截取长度
     * @return
     */
    public static String getString(byte[] data, int startIndex, int len) {
        if (startIndex < 0 || startIndex >= data.length)
            return "";
        int retStrLen = (startIndex + len <= data.length ? len : data.length - startIndex);
        byte[] tmp = new byte[retStrLen];
        System.arraycopy(data, startIndex, tmp, 0, retStrLen);
        return new String(tmp);
    }

    /**
     * 获取字节数组中从指定位置开始的指定长度的字节数组
     * @param data
     * @param startIndex
     * @param len
     * @return
     */
    public static byte[] getByteAry(byte[] data, int startIndex, int len) {
        byte[] buf = null;
        if (startIndex < 0 || startIndex >= data.length)
            return null;
        int realLen = (startIndex + len <= data.length ? len : data.length - startIndex);
        buf = new byte[realLen];
        System.arraycopy(data, startIndex, buf, 0, realLen);
        return buf;
    }

    /**
     * 获取日期时间 数组存放格式 yymmddhhnnss
     * 不正常情况返回当前时间
     * @param data
     * @param startIndex
     * @return
     */
    public static Date getDateTime(byte[] data, int startIndex) {
        if (startIndex < 0 || startIndex + 6 > data.length)
            return new Date();
        int year = KKTool.byteToInt(data[startIndex ++]) + 2000;
        int month = KKTool.byteToInt(data[startIndex ++]);
        int day = KKTool.byteToInt(data[startIndex ++]);
        int hour = KKTool.byteToInt(data[startIndex ++]);
        int minute = KKTool.byteToInt(data[startIndex ++]);
        int second = KKTool.byteToInt(data[startIndex ++]);
        Calendar c = new GregorianCalendar(year, month - 1, day, hour, minute, second);
        return c.getTime();
    }
    
    public static String getFormatDateTime(Date date) {
        if (date == null)
            return "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(date);
    }

    public static String getFormatDateTime(Date date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }
    
    public static String getFormatDate(Date date) {
        if (date == null)
            return "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        return sdf.format(date);	
    }
    
    /**
     * 字节转对应的十六进制字符串
     * @param data
     * @return
     */
    public static String byteToHexStr(byte data) {
    	String s = Integer.toHexString(data & 0xFF); 
    	return (s.length() == 2 ? s : "0" + s).toUpperCase();
//        int intValue = byteToInt(data);
//        return (intValue > 15? Integer.toHexString(intValue):"0" + Integer.toHexString(intValue));
    }

    /**
     * 字节数组转对应的十六进制字符串
     * @param data
     * @return 十六进制字符串，数组长度为空时返回空字符串""
     */
    public static String byteArrayToHexStr(byte[] data) {
        StringBuffer result = new StringBuffer("");
        for (int i = 0; i < data.length; i ++) {
            result.append(byteToHexStr(data[i]));
        }
        return result.toString();
    }

    /**
     * 字节数组按指定开始位置、指定长度转成十六进制字符串
     * @param data  字节数组
     * @param startIndex 开始位置
     * @param len 需转换的长度
     * @return 十六进制字符串，开始位置、长度指定不合法返回空字符串""
     */
    public static String byteArrayToHexStr(byte[] data, int startIndex, int len) {
        if(startIndex < 0 || startIndex >= data.length)
            return "";
        if (len < 0)
            return "";
        len = (startIndex + len <= data.length ? len : data.length - startIndex);
        byte[] buf = new byte[len];
        System.arraycopy(data, startIndex, buf, 0, len);
        return byteArrayToHexStr(buf);
    }
    
    public static String channelBufferToHexStr(ChannelBuffer channelBuffer) {
    	return byteArrayToHexStr(channelBuffer.array());
    }

    public static byte[] short2Bytes(short sValue, byte bigOrLittle) {
        byte[] b = new byte[2];
        if (bigOrLittle == 0) {
            b[0] = (byte)(sValue >> 8);
            b[1] = (byte)sValue;
        } else {
            b[0] = (byte)sValue;
            b[1] = (byte)(sValue >> 8);
        }
        return b;
    }
    
    public static byte[] short2BytesBigEndian(short sValue) {
    	return short2Bytes(sValue, BIT_BIGENDIAN);
    }
    
    /**
     * 将short值填入到字节数组中
     * @param sValue  short值
     * @param bigOrLittle  0:大端 1:小端
     * @param data  字节数组
     * @param sIndex 数组中开始填充位置
     * @return 是否成功
     */
    public static boolean short2Bytes(short sValue, byte bigOrLittle, byte[] data, int sIndex) {
    	if (data == null || sIndex < 0 || sIndex + 2 > data.length) 
    		return false;
    	if (bigOrLittle == BIT_BIGENDIAN) {
    		data[sIndex] = (byte)(sValue >> 8);
    		data[sIndex + 1] = (byte)sValue;
        } else {
        	data[sIndex] = (byte)sValue;
        	data[sIndex + 1] = (byte)(sValue >> 8);
        }    		
    	
    	return true;
    }
    
    /**
     * 将short值填入字节数组中 按大端模式填充
     * @param sValue
     * @param data
     * @param sIndex 数组中开始填充位置
     * @return 是否成功
     */
    public static boolean short2BytesBigEndian(short sValue, byte[] data, int sIndex) {
    	return short2Bytes(sValue, BIT_BIGENDIAN, data, sIndex);
    }
    
    public static int getUnsignedShort(short s) {
    	return s > 0 ? s : ((Short.MAX_VALUE + 1) * 2 + s);
    }

    public static byte[] int2Bytes(int iValue, byte bigOrLittle) {
        byte[] b = new byte[4];
        if (bigOrLittle == 0) {
            b[0] = (byte)(iValue >> 24);
            b[1] = (byte)(iValue >> 16);
            b[2] = (byte)(iValue >> 8);
            b[3] = (byte)(iValue);
        } else {
            b[0] = (byte)(iValue);
            b[1] = (byte)(iValue >> 8);
            b[2] = (byte)(iValue >> 16);
            b[3] = (byte)(iValue >> 24);
        }
        return b;
    }
    
    /**
     * 将int值填入到字节数组中
     * @param iValue  int值
     * @param bigOrLittle  0:大端 1:小端
     * @param data  字节数组
     * @param sIndex 数组中开始填充位置
     * @return 是否成功
     */
    public static boolean int2Bytes(int iValue, byte bigOrLittle, byte[] data, int sIndex) {
    	if (data == null || sIndex < 0 || sIndex + 4 > data.length) 
    		return false;
    	if (bigOrLittle == BIT_BIGENDIAN) {
            data[sIndex] = (byte)(iValue >> 24);
            data[sIndex + 1] = (byte)(iValue >> 16);
            data[sIndex + 2] = (byte)(iValue >> 8);
            data[sIndex + 3] = (byte)(iValue);
        } else {
        	data[sIndex + 0] = (byte)(iValue);
        	data[sIndex + 1] = (byte)(iValue >> 8);
        	data[sIndex + 2] = (byte)(iValue >> 16);
        	data[sIndex + 3] = (byte)(iValue >> 24);
        }    		
    	
    	return true;
    }
    
    /**
     * 将int值填入字节数组中 按大端模式填充
     * @param iValue
     * @param data
     * @param sIndex 数组中开始填充位置
     * @return 是否成功
     */
    public static boolean int2BytesBigEndian(int iValue, byte[] data, int sIndex) {
    	return int2Bytes(iValue, BIT_BIGENDIAN, data, sIndex);
    }    

    /**
     * 线程睡眠指定时间
     * @param milliseconds 毫秒
     */
    public static void sleepTime(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException ex) {
            
        }
    }

    public static long getCurrTimeMilliseconds() {
        return System.currentTimeMillis();
    }

    public static String getCurrFormatDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date now = new Date();
        return sdf.format(now);
    }

    public static String getCurrFormatDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date now = new Date();
        return sdf.format(now);
    }
    
    public static String getCurrFormatDate(String format) {
		if (isStrNullOrBlank(format))
			return "00000000";//yyyyMMdd
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			Date now = new Date();
			return sdf.format(now);
		} catch (Exception e) {
			return "00000000";
		}
	}
    
    /**
     * 解析字节数组中的GPS时间
     * @param data
     * @param startIndex 开始解析的位置
     * @param isGMT 是否是格林尼治时间
     * @param dataType GPS时间数据的格式  0：正常的16进制 1：BCD码
     * @return 返回北京时间 数据有错返回当前时间
     */
    public static Date getGpsTime(byte[] data, int startIndex, boolean isGMT, byte dataType) {
        if (data == null || startIndex < 0 || data.length - startIndex < 6)
            return new Date();
        int offset = startIndex;
        int year, month, day, hour, minute, second;
        try {
            if (dataType == 0) {//16进制
                year = 2000 + data[offset++];
                month = data[offset++];
                day = data[offset++];
                hour = data[offset++];
                minute = data[offset++];
                second = data[offset++];
            } else {//BCD码  10 0x10
                year = 2000 + Integer.parseInt(KKTool.byteToHexStr(data[offset++]));
                month = Integer.parseInt(KKTool.byteToHexStr(data[offset++]));
                day = Integer.parseInt(KKTool.byteToHexStr(data[offset++]));
                hour = Integer.parseInt(KKTool.byteToHexStr(data[offset++]));
                minute = Integer.parseInt(KKTool.byteToHexStr(data[offset++]));
                second = Integer.parseInt(KKTool.byteToHexStr(data[offset++]));
            }
            Calendar c = new GregorianCalendar(year, month - 1, day, hour, minute, second);
            if (isGMT) {//是格林尼治时间加上8小时
                c.add(Calendar.HOUR_OF_DAY, 8);
            }
            return c.getTime();
        } catch (Exception e) {
            return new Date();
        }
    }

    public static byte[] getBCDGMTGpsTime(byte[] data, int startIndex, byte dataType, boolean isGMT) {
        byte[] bcdGpsTime = new byte[6];
        if (data == null || startIndex < 0 || data.length - startIndex < 6)
            return bcdGpsTime;
        int offset = startIndex;
        int year, month, day, hour, minute, second;
        try {
            if (dataType == 0) {//16进制
                year = 2000 + data[offset++];
                month = data[offset++];
                day = data[offset++];
                hour = data[offset++];
                minute = data[offset++];
                second = data[offset++];
            } else {//BCD码  10 0x10
                year = 2000 + Integer.parseInt(KKTool.byteToHexStr(data[offset++]));
                month = Integer.parseInt(KKTool.byteToHexStr(data[offset++]));
                day = Integer.parseInt(KKTool.byteToHexStr(data[offset++]));
                hour = Integer.parseInt(KKTool.byteToHexStr(data[offset++]));
                minute = Integer.parseInt(KKTool.byteToHexStr(data[offset++]));
                second = Integer.parseInt(KKTool.byteToHexStr(data[offset++]));
            }
            Calendar c = new GregorianCalendar(year, month - 1, day, hour, minute, second);
            if (!isGMT) {
                c.add(Calendar.HOUR_OF_DAY, -8);
            }
            bcdGpsTime[0] = byteToBCD((byte)(c.get(Calendar.YEAR)- 2000));
            bcdGpsTime[1] = byteToBCD((byte)(c.get(Calendar.MONTH) + 1));
            bcdGpsTime[2] = byteToBCD((byte)c.get(Calendar.DAY_OF_MONTH));
            bcdGpsTime[3] = byteToBCD((byte)c.get(Calendar.HOUR_OF_DAY));
            bcdGpsTime[4] = byteToBCD((byte)c.get(Calendar.MINUTE));
            bcdGpsTime[5] = byteToBCD((byte)c.get(Calendar.SECOND));
        } catch (Exception e) {
        }
        return bcdGpsTime;
    }

    /**
     * 解析字节数组中的GPS时间,返回格式yyyy-MM-dd hh:mm:ss
     * @param data
     * @param startIndex 开始解析的位置
     * @param isGMT 是否是格林尼治时间
     * @param dataType GPS时间数据的格式  0：正常的16进制 1：BCD码
     * @return 返回北京时间 数据有错返回当前时间
     */
    public static String getGpsTimeStr(byte[] data, int startIndex, boolean isGMT, byte dataType) {
        Date gpsTime = getGpsTime(data, startIndex, isGMT, dataType);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(gpsTime);
    }

    /**
     * 填充成指定长度的字符串
     * @param str 源字符串
     * @param len 填充后的长度
     * @param fillType 填充方式 0:左补 1:右补
     * @return
     */
    public static String formatString(String str, int len, byte fillType) {
        if (str == null)
            return null;
        int strLen = str.getBytes(Charset.forName("UTF-8")).length;
        if (strLen == len) {
            return str;
        } else if (strLen < len) {
            int blankCount = len - strLen;
            String blank = "";
            for (int i = 0; i < blankCount - 1; i ++) {
                blank = blank + " ";
            }
            if (fillType == 0) {
                return blank + str;
            } else {
                return str + blank;
            }
        } else {
            return str.substring(0, len);
        }
    }

    public static String getExceptionTip(Exception e) {
    	String ret = null;
        if (e != null) {
        	ret = "msg:" + e.getMessage() + " stacktrace:";
			StackTraceElement[] stes =  e.getStackTrace();
			for(StackTraceElement ste : stes) {
			    ret = ret + ste.toString() + " ";
			}
        }
        return ret;
    }
    
    public static boolean isOvertime(long sTime, long maxTime) {
    	return System.currentTimeMillis() - sTime >= maxTime;
    }
    
    /**
     * 字节异或和
     * @param data
     * @return
     */
    public static byte getXorSum(byte[] data) {
    	byte ret = 0;
    	for (byte b : data) {
    		ret ^= b;
    	}
    	return (byte)ret;
    }
    
    /**
     * 计算异或值是否正确
     * @param buffer 
     * @param sIndex 开始位置
     * @param eIndex 结束位置
     * @param xorVal 异或结果
     * @return true:从sIndex 到eIndex异或后的结果等于xorVal 
     */
    public static boolean checkXorValid(ChannelBuffer buffer, int sIndex, int eIndex, byte xorVal) {
    	if (buffer != null && sIndex <= eIndex && sIndex >= 0 && eIndex < buffer.capacity()) {
    		byte calXor = 0;
    		for (int i = sIndex; i <= eIndex; i ++) {
    			calXor ^= buffer.getByte(i);
    		}
    		return calXor == xorVal;
    	}
    	return false;
    }

    /**
     * 计算CRC值，此算法是公交IC卡中采用，
     * 算法中要返回2字节无符号型整数，此处将其返回4字节的int型，获取后比较时需进行相关转换
     * @param len
     * @param data
     * @param sIndex
     * @return
     */
    public static int getCRCBusIC(int len, byte[] data, int sIndex) {
    	int crc = 0x31E3;
    	for(int i = 0; i < len; i ++) {
    		crc = crc ^ (data[i + sIndex]<<8);
    		for (int j = 0; j < 8; j ++) {
    			if ((crc & 0x8000) != 0) {
    				crc = (crc << 1) ^ 0x1021;
    			} else {
    				crc = crc << 1;
    			}
    		}
    	}
    	return crc;
    }
    
    public static boolean checkLRC(byte[] data) {
    	return checkLRC1(data) && checkLRC2(data);
    }
    
    public static boolean checkLRC(ChannelBuffer buffer, int cmdSize) {
    	return checkLRC1(buffer, cmdSize) && checkLRC2(buffer, cmdSize);
    }
    
    /**
     * 校验位1是否正确   LRC1：从长度开始到数据结束的每一个字节的异或值再异或0x33
     * @param data  完整的数据包   从包类型到校验位2
     * @return
     */
    public static boolean checkLRC1(byte[] data) {
    	if (data == null || data.length <= 14)
    		return false;
    	
    	byte lrc1 = data[data.length - 2];
    	
    	byte lrc = 0;
    	
    	for(int i = 2; i < data.length - 2; i ++) {
    		lrc ^= data[i];
    	}
    	
    	return lrc1 == (lrc ^ (byte)0x33);
    }
    
    public static boolean checkLRC1(ChannelBuffer buffer, int cmdSize) {
    	if (buffer == null || buffer.readableBytes() <= 14) {
    		return false;
    	}
    	
    	int readerIndex = buffer.readerIndex();
    	byte lrc1 = buffer.getByte(readerIndex + cmdSize - 2);
    	byte lrc = 0;
    	for (int i = 2; i < cmdSize - 2; i ++) {
    		lrc ^= buffer.getByte(readerIndex + i);
    	}
    	return lrc1 == (byte)(lrc ^ (byte)0x33);
    }
    
    /**
     * 校验位2是否正确   LRC2：从长度开始到数据结束的每一个字节的累加和再加0x33
     * @param data  完整的数据包   从包类型到校验位2
     * @return
     */
    public static boolean checkLRC2(byte[] data) {
    	if (data == null || data.length <= 14)
    		return false;
    	
    	byte lrc2 = data[data.length - 1];
    	
    	byte lrc = 0;
    	
    	for(int i = 2; i < data.length - 2; i ++) {
    		lrc += data[i];
    	}
    	
    	return lrc2 == (byte)(lrc + 0x33);
    }
    
    public static boolean checkLRC2(ChannelBuffer buffer, int cmdSize) {
    	if (buffer == null || buffer.readableBytes() <= 14) {
    		return false;
    	}
    	
    	int readerIndex = buffer.readerIndex();
    	byte lrc2 = buffer.getByte(readerIndex + cmdSize - 1);
    	byte lrc = 0;
    	for (int i = 2; i < cmdSize - 2; i ++) {
    		lrc += buffer.getByte(readerIndex + i);
    	}
    	return lrc2 == (byte)(lrc + (byte)0x33);
    }
    
    public static byte getLRC1(byte[] data) {
    	byte lrc1 = 0;
    	
    	for(int i = 0; i < data.length; i ++) {
    		lrc1 ^= data[i];
    	}
    	return (byte)(lrc1 ^ 0x33);
    }
    
    public static byte getLRC2(byte[] data) {
    	byte lrc2 = 0;
    	
    	for(int i = 0; i < data.length; i ++) {
    		lrc2 += data[i];
    	}
    	return (byte)(lrc2 + 0x33);
    }
    
    /**
     * 根据卡号返回bcd字节数组
     * 如：卡号为8845879911223344，则返回0x880x450x870x990x110x220x330x44,
     * @param cardNo
     * @return
     */
    public static byte[] cardNoToBcdBytes(char[] cardNo) {
    	if (cardNo == null || cardNo.length != 16)
    		return null;
    	
    	byte[] bcdBytes = new byte[8];
    	try {
    		for (int i = 0; i < 8; i ++) {
    			bcdBytes[i] = (byte)Short.parseShort(String.copyValueOf(cardNo, 2 * i, 2), 16);
    		}
		} catch (Exception e) {
			return null;
		}
    	return bcdBytes;
    }
    
    public static void fillBytes(byte[] data, int startIndex, byte fillByte) {
    	if (data == null || data.length <= 0)
    		return;
    	
    	for (int i = startIndex; i < data.length; i ++) {
    		data[i] = fillByte; 
    	}
    }
    
    public static boolean isDateBeforeToday(String date) {
    	String today = getFormatDateTime(new Date(), "yyyyMMdd");
    	return date.compareTo(today) <= 0;
    }
    
    public static boolean deleteFile(String filePath) {
    	if (filePath == null || filePath.equals(""))
    		return false;
    	
    	File file = new File(filePath);
    	if (file.exists())
    		return file.delete();
    	return false;
    }
    
    public static boolean renameFile(String oldFileName, String newFileName) {
    	if (oldFileName == null || oldFileName.equals("") || newFileName == null || newFileName.equals(""))
    		return false;
    	
    	File oFile = new File(oldFileName);
    	File nFile = new File(newFileName);
    	return oFile.renameTo(nFile);
    }
    
    public static String getYesterdayFormatDate() {
    	Calendar c = Calendar.getInstance();
    	c.add(Calendar.DAY_OF_MONTH, -1);
    	Date date = new Date(c.getTimeInMillis());
    	return getFormatDate(date);
    }
    
    public static boolean isFileExisted(String fileName) {
		if (fileName == null || fileName.equals(""))
			return false;
		
		File file = new File(fileName);
		return file.exists();
    }
    
    public static boolean fillIntIntoBytes(int iValue, byte[] data, int offset, byte bigOrLittle) {
    	if (data == null || data.length < 4 || offset < 0 || offset + 3 < data.length)
    		return false;
    	
        if (bigOrLittle == 0) {
        	data[offset] = (byte)(iValue >> 24);
        	data[offset + 1] = (byte)(iValue >> 16);
        	data[offset + 2] = (byte)(iValue >> 8);
        	data[offset + 3] = (byte)(iValue);
        } else {
        	data[offset] = (byte)(iValue);
        	data[offset + 1] = (byte)(iValue >> 8);
        	data[offset + 2] = (byte)(iValue >> 16);
        	data[offset + 3] = (byte)(iValue >> 24);
        }
        return true;
    }
    
    public static boolean fillIntIntoBytesBigEndian(int iValue, byte[] data, int offset) {
    	return fillIntIntoBytes(iValue, data, offset, BIT_BIGENDIAN);
    }
    
    public static boolean isStrNullOrBlank(String s) {
		return s == null || s.equals("");
    }
    
    public static boolean isBytesNullOrBlank(byte[] bytes) {
    	return bytes == null || bytes.length == 0;
    }
    
    public static void initBytes(byte[] data, byte b) {
    	if (data == null || data.length == 0)
    		return;
    	for (int i = 0; i < data.length; i++) {
			data[i] = b; 
		}
    }
    
    /**
     * 将字符串转换为bcd字节数组
     * 以下几种情况返回值全为0的数组
     * 1.字符串为空
     * 2.字符串的长度不为字节数组长度的2倍
     * 3.字符串中有不合法的内容
     * @param s 字符串
     * @param bytesLen 转换后的数组长度
     * @return
     */
    public static byte[] strToBcdBytes(String s, int bytesLen) {
    	byte[] bcdBytes = new byte[bytesLen];
    	if (!KKTool.isStrNullOrBlank(s)) {
    		if (s.length() == 2 * bytesLen) {
    			try {
    				for(int i = 0; i < bytesLen; i ++) {
    					bcdBytes[i] = (byte)(Integer.parseInt(s.substring(2 * i, 2 * i + 2), 16)); 
    				}
				} catch (Exception e) {
					initBytes(bcdBytes, (byte)0);
				}
    		}
    	}
    	return bcdBytes;
    }
    
    public static void printLog(Object object) {
    	System.out.println(object);
    }
    
    /**
     * 字节数组拷贝
     * @param dst 目标数组
     * @param src 源数组
     * @param dstIndex 目标数组的开始拷贝位置
     * @return 拷贝是否成功 
     */
    public static boolean copyBytes(byte[] dst, byte[] src, int dstIndex) {
    	if (dst == null || src == null || dstIndex < 0 || dstIndex + src.length > dst.length)
    		return false;
    	try {
    		System.arraycopy(src, 0, dst, dstIndex, src.length);
    	} catch (Exception e) {
			return false;
		}
    	return true;
    }
    
    /**
     * 获取定长格式化字符串，不足补指定字符
     * 超过长度则截取
     * @param srcStr
     * @param len
     * @param fillChar
     * @param isFillLeft
     * @return
     */
    public static String getFormatStr(String srcStr, int len, String fillChar, boolean isFillLeft) {
    	if (srcStr.length() == len)
    		return srcStr;
    	
    	if (srcStr.length() > len) {//超过长度则截取
    		if(isFillLeft)
    			return srcStr.substring(srcStr.length() - len);
    		else
    			return srcStr.substring(0, len);
    	}
    	
    	StringBuilder sBuilder = null;
    	if (isFillLeft) {//左补
    		sBuilder = new StringBuilder();
			for (int i = 0; i < len - srcStr.length(); i ++) {
				sBuilder.append(fillChar);
			}    	
			sBuilder.append(srcStr);
    	} else {//右补
	    	sBuilder = new StringBuilder(srcStr);
			for (int i = 0; i < len - srcStr.length(); i ++) {
				sBuilder.append(fillChar);
			}
    	}
    	return sBuilder.toString();
    }
    
    public static char[] getFormatCharAry(String srcStr, int len) {
    	return getFormatStr(srcStr, len, "0", true).toCharArray();
    }
    
    public static byte[] getBytesFromStr(String srcStr, int len) {
    	byte[] bytes = null;
    	if (!isStrNullOrBlank(srcStr)) {
    		bytes = new byte[len];
    		try {
    			srcStr = getFormatStr(srcStr, len, "0", true);
    			for(int i = 0; i < len; i ++) {
    				bytes[i] = (byte)(Integer.parseInt((srcStr.substring(i, i + 1))));
    			}
			} catch (Exception e) {
				initBytes(bytes, (byte)0);
			}
    		
    	}
    	return bytes;
    }
    
    public static long bytesToLong(byte[] bytes) {
    	long l = 0;
    	if (!isBytesNullOrBlank(bytes) && bytes.length <= 8) {
    		for (int i = bytes.length - 1; i >= 0; i --) {
    			l |= ((long)(bytes[i] & 0x00ff)) << ((bytes.length - 1 - i) * 8);
    		}
    	}
    	return l;
    }
    
    public static byte[] getCurrBcdDateTime() {
    	byte[] dt = new byte[7];
    	Calendar c = Calendar.getInstance();
    	int year = c.get(Calendar.YEAR);
    	int month = c.get(Calendar.MONTH) + 1;
    	int day = c.get(Calendar.DAY_OF_MONTH);
    	int hour = c.get(Calendar.HOUR_OF_DAY);
    	int minute = c.get(Calendar.MINUTE);
    	int second = c.get(Calendar.SECOND);
    	dt[0] = byteToBCD((byte)(year/100));
    	dt[1] = byteToBCD((byte)(year % 100));
    	dt[2] = byteToBCD((byte)month);
    	dt[3] = byteToBCD((byte)day);
    	dt[4] = byteToBCD((byte)hour);
    	dt[5] = byteToBCD((byte)minute);
    	dt[6] = byteToBCD((byte)second);
    	return dt;
    }
    
    public static byte[] getBcdBytesFromStr(String srcStr, int len) {
    	byte[] b = new byte[len];
    	try {
			for(int i = 0; i < len; i ++) {
				b[i] = (byte)Integer.parseInt(srcStr.substring(2 * i, 2 * i + 2), 16);
			}	
    	} catch (Exception e) {
			initBytes(b, (byte)0);
			KKLog.error("getBcdBytesFromStr err:" + getExceptionTip(e));
		}
		return b;
    }
    
    public static boolean createFileDir(String dir) {
    	if (!isStrNullOrBlank(dir)) {
    		File file = new File(dir);
    		if (!file.exists())
    			return file.mkdirs();
    		return true;
    	}
    	return false;
    }
    
    public static boolean isPosDealDataValid(String carNo, String lineNo, String dealTime) {
		if (carNo == null || lineNo == null || dealTime == null)
			return false;
		if (!carNo.matches("\\w{6}"))
			return false;
		if (!lineNo.matches("\\d{7}"))
			return false;
		if (!dealTime.matches("20((([1-9][13579])|([13579][048])|([2468][26]))(((0[13578]|1[02])(0[1-9]|[12][0-9]|3[01]))|((0[469]|11)(0[1-9]|[12][0-9]|30))|(02(0[1-9]|[12][0-9]|2[0-8])))|(([2468][048])|([13579][26]))((((0[13578])|(1[02]))(0[1-9]|[12][0-9]|3[01]))|((0[469]|11)(0[1-9]|[12][0-9]|30))|(02(0[1-9]|[12][0-9]|2[0-9]))))(([0-1][0-9])|(2[0-3]))([0-5][0-9])([0-5][0-9])"))
			//if (!dealTime.matches("20[1-9][1-9]((0[1-9])|(1[0-2]))()(([0-1][0-9])|(2[0-3]))([0-5][0-9])([0-5][0-9])"))//20120202
			return false;
		return true;
	}
    
    /**
     * windows下获取正常,linux下获取虚拟机下会为127.0.0.1,真机未试过
     * @return
     */
    public static String getLocalIp() {
		try {
			InetAddress inet = InetAddress.getLocalHost();
			return inet.getHostAddress();
		} catch (UnknownHostException e1) {
			return "";
		}
    }
    
    public static String elipseTime(long sTime, long eTime) {
    	long elipseTime = Math.abs(eTime - sTime);
    	int day = (int)elipseTime/DAY_MILLISECONDS;
    	elipseTime -= day * DAY_MILLISECONDS;
    	int hour = (int)elipseTime/HOUR_MILLISECONDS;
    	elipseTime -= hour * HOUR_MILLISECONDS;
    	int min = (int)elipseTime/MINUTE_MILLISECONDS;
    	elipseTime -= min * MINUTE_MILLISECONDS;
    	int second = (int)elipseTime / SECOND_MILLISECONDS;
    	elipseTime -= second * SECOND_MILLISECONDS;
    	int millisecond = (int)elipseTime;
    	StringBuilder sb = new StringBuilder();
    	if (day > 0)
    		sb.append(day + "day");
    	if (hour > 0)
    		sb.append(hour + "hour");
    	if (min > 0)
    		sb.append(min + "minute");
    	if (second > 0)
    		sb.append(second + "second");
    	if (millisecond > 0)
    		sb.append(millisecond + "millisecond");
    	return sb.toString();
    }
    
    public static boolean isIpInvalid(String ip) {
    	if (isStrNullOrBlank(ip))
			return false;
		String[] s = ip.split("\\x2e");
		if (s.length == 4) {
			return isInputNumberInRange(s[0], 1, 223, 3) && isInputNumberInRange(s[1], 0, 255, 3) 
				&& isInputNumberInRange(s[2], 0, 255, 3) && isInputNumberInRange(s[3], 0, 255, 3);
		}
		if (s.length == 6) {
			return isInputNumberInRange(s[0], 1, 223, 3) && isInputNumberInRange(s[1], 0, 255, 3) 
					&& isInputNumberInRange(s[2], 0, 255, 3) && isInputNumberInRange(s[3], 0, 255, 3)
					&& isInputNumberInRange(s[4], 0, 255, 3) && isInputNumberInRange(s[5], 0, 255, 3);
		}
		return false;
    }
    
    public static boolean isInputNumberInRange(String input, int minValue, int maxValue, int maxLength) {
		if (isInputNumber(input) && input.length() <= maxLength) {
			int i = Integer.valueOf(input);
			return minValue <= i && i <= maxValue;
		}
		return false;
    }
    
    public static boolean isInputNumber(String input) {
		if (KKTool.isStrNullOrBlank(input))
			return false;
		
		return input.matches("\\d+");
	}
    
    public static boolean isPortInvalid(String port) {
		if (isInputNumber(port) && port.length() <= 5) {
			int iPort = Integer.valueOf(port);
			return iPort <= 65535;
		}
		
		return false;
	}
    
    public static String getFormatDatetime(long d) {
    	Date date = new Date(d);
    	return getFormatDateTime(date, "yyyyMMdd HHmmss");
    }
    
    public static boolean createFileParentDir(String fileName) {
    	if (isStrNullOrBlank(fileName)) {
    		return false;
    	}
    	File file = new File(fileName);
    	if (!file.isDirectory()) {
    		String parentPath = file.getParent();
    		if (!isStrNullOrBlank(parentPath)) {
    			File parentFile = new File(parentPath);
    			parentFile.mkdirs();
    		}
    	}
    	return true;
    }
    
    public static void main(String[] args) {
//    	System.out.println(createFileParentDir("F:\\project\\IC\\ics\\data\\1001001\\123457856\\afc1810.1001001.20120614.12347856.16.A12345.6009600.flg"));
//    	String string = "h0 + h1 + h2";
//    	string = string.replace("h0", "h24");
//    	System.out.println(string);
//    	System.out.println(Runtime.getRuntime().availableProcessors());
//    	byte[] b = new byte[]{48, 49, 50, 65, 66};
//    	System.out.println(new String(b));
//    	File f = new File("F:\\project\\IC\\ics\\data\\afc1810.1001001.20120614.12347856.16.A12345.6009600.flg");
//    	System.out.println(elipseTime(f.lastModified(), System.currentTimeMillis()));
//    	BufferedWriter bWriter = null;
//    	try {
//			bWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("f:\\1.txt")));
//			bWriter.write(new char[]{'2','0','1','2','0','5','1','7','1','2','3','4','5','6'});
//		} catch (FileNotFoundException e) {
//		} catch (IOException e) {
//		} finally {
//			try {
//				bWriter.close();
//			} catch (IOException e) {
//			}
//		}
//    	for (int i = 0; i < 100; i ++) {
//    		KKTool.sleepTime(10000);
//    	}
//    	try {
//			RandomAccessFile raf = new RandomAccessFile(new File("F:/project/IC/ics/data/upload/posdeal/afc1810.1001001.20120531.12345678.09.A12345.6009600.flg"), "r");
//			byte[] b6 = new byte[6];
//			raf.seek(105);
//			raf.read(b6);
//			printLog(new String(b6));
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//    	printLog(isPosDealDataValid("A2q456", "1134567", "20130101000000"));
//    	printLog(isPosDealDataValid("A2q456", "1134567", "20120229235959"));
//    	printLog(isPosDealDataValid("A2q456", "1134567", "20490219330500"));
//    	printLog(isPosDealDataValid("A2q456", "1134567", "20731111020302"));
//    	byte[] bytes = new byte[] {(byte)0x80, (byte)(0xFF), (byte)(0xFF), (byte)(0xFF), (byte)(0xFF), (byte)(0xFF), (byte)(0xFF), (byte)(0xFF)};
//    	printLog(bytesToLong(bytes));
//    	try {
//			RandomAccessFile f = new RandomAccessFile(new File("f:\\1.txt"), "rw");
//			byte[] b = new byte[14];
//			f.read(b);
//			printLog(new String(b));
//			
////			BufferedWriter bWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("f:\\1.txt")));
////			bWriter.write(new char[]{'2','0','1','2','0','5','1','7','1','2','3','4','5','6'});
////			bWriter.flush();
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//    	
//    	Calendar c = Calendar.getInstance();
//    	c.add(Calendar.DAY_OF_MONTH, -1);
//    	Date date = new Date(c.getTimeInMillis());
//    	System.out.println(getFormatDate(date));
//    	File f = new File("F:\\project\\IC\\doc\\wenjian\\1TXN3041.flg1");
//    	System.out.println(f.renameTo(new File("F:\\project\\IC\\doc\\wenjian\\TXN3041.flg")));
//    	long l = System.currentTimeMillis();
//    	HashMap<String, Integer> map = new HashMap<String, Integer>(40000);
//    	int j = 0;
//    	for(int i = 0; i < 40000; i ++) {
//    		map.put("00" + i, i);
//    		j += i;
//    	}
//    	System.out.println("j = " + j + ",create hashmap 耗时:" + (System.currentTimeMillis() - l));
//    	l = System.currentTimeMillis();
//    	HashMap<String, Integer> mapCopy = (HashMap<String, Integer>)map.clone();
//    	System.out.println("clone hashmap 耗时:" + (System.currentTimeMillis() - l));
//    	Set<String> set = mapCopy.keySet();
//    	System.out.println("Set.size=" + set.size());
//    	l = System.currentTimeMillis();
//    	j = 0;
//    	for (Iterator<String> iterator = set.iterator(); iterator.hasNext();) {
//    		j += mapCopy.get(iterator.next());
//    		if (j < 0){
//    			
//    		}
//		}
//    	System.out.println("j = " + j + ",loop hashmap 耗时:" + (System.currentTimeMillis() - l));

    }
}
