/**
 * @author Jadic
 * @created 2012-5-2 
 */
package com.jadic.utils;


public final class ConstDefine {
	
  /***********************湖州对接工行************************/
	public static final int MIN_CMD_LENGTH = 22;//数据包最小长度 数据体为空的时候
	
	public static final byte CMD_HEAD = 0x7C;						//命令头
	public static final byte CMD_END = 0x7D;						//命令尾
	
	//客户端发起
	public static final byte CMD_LOGIN = 0x71;						//登录
	public static final byte CMD_GETDATA = 0x72;					//获取数据
	public static final byte CMD_GETDATA_SUCC = 0x73;				//本次获取数据成功
	
	//服务端应答
	public static final byte CMD_LOGIN_RESP = 0x01;					//登录应答
	public static final byte CMD_SEND_DATACOUNT = 0x02;				//本次发送数据的条数
	public static final byte CMD_SEND_DATA = 0x03;					//返回明细数据
	public static final byte CMD_SEND_OK = 0x04;					//本次返回数据完成
	
  /***********************湖州对接工行************************/
	
	
	public static final byte CMDTYPE1 = (byte)0x7C;
	public static final byte CMDTYPE2 = (byte)0x7D;

    /*公交Pos机发往IC卡前置的命令*/
    public static final byte DEVREG = 0x40;						//设备注册0X40
    public static final byte QUERY_COLLECTION = 0x41;			//补采表信息查询0x41
    public static final byte GET_COLLECTION = 0x42;				//取补采表0X42
    public static final byte GET_CODE = 0x43;					//取程序代码0x43
    public static final byte GET_BLACKLIST = 0x44;				//取黑名单文件数据0x44
    public static final byte GET_DISCOUNT_RATE = 0x45;			//取折扣率文件数据0x45
    public static final byte UPLOAD_DEAL_DATA = 0x46;			//上传交易数据0x46
    public static final byte COLLECT_DEAL_DATA= 0x47; 			//上传补采集交易数据0x47
    public static final byte COLLECT_DEAL_DATA_OK = 0x48;		//补采交易数据上传完成0x48
    public static final byte HEART_BEAT = 0x49;					//心跳0x49
    
    /*IC卡前置发往Pos机的应答码*/
    public static final short RETCODE_SUCC = (short)0x5500;
    public static final short RETCODE_DATAERR = (short)0x55AA;
    public static final short RETCODE_NODEV = (short)0x55FD;
    public static final short RETCODE_DATAEXC = (short)0x55FE;
    public static final short RETCODE_FAIL = (short)0x55FF;
     
    public final static String FILESPERATOR = "/";
    public final static String SDOT = ".";
    public final static String SPOSFILETYPE = "flg";
    public final static String RECOLLECTFILEHEAD = "BUS1991";//补采文件文件头
    public final static String POSDEALDATAFILEHEAD = "afc1810";//交易数据文件头
    public final static String RECOLLECTPOSDEALDATAFILEHEAD = "afc1820";//补采交易数据文件头
    public final static String BLACKLISTFILENAME = "BUS192000";// + SDOT + SPOSFILETYPE;//黑名单文件名
    public final static String BLACKLISTFILENAME_WITHFILETYPE = BLACKLISTFILENAME + SDOT + SPOSFILETYPE;//黑名单文件名带文件类型
    public final static String DISCOUNTFILENAME = "BUS191001";// + SDOT + SPOSFILETYPE;//折扣率文件名
    public final static String DISCOUNTFILENAME_WITHFILETYPE = "BUS191001" + SDOT + SPOSFILETYPE;//折扣率文件名 带文件类型
    
    public final static String POSINVALIDDATAFILENAME = "PosInvalid_";
    public final static String POSINVALIDDATAFILETYPE = "dat";
    
    public final static String BLANK = " ";
    public final static String NEXTLINE = "\n";
    
    public final static String LOCALBLACKLISTFILENAME_BASE = "localblacklist_b";//本地黑名单文件的基本库文件名
    public final static String LOCALBLACKLISTFILENAME_PLUS = "localblacklist_p";//本地黑名单文件的增量库文件名
    public final static String LOCALBLACKLISTFILENAME_SUBTRACT = "localblacklist_s";//本地黑名单文件的减量库文件名
    
    public final static byte SAVEPARAMS_INPUTINVALID = 0x00;//
    public final static byte SAVEPARAMS_SUCC = 0x01;
    public final static byte SAVEPARAMS_FAIL = 0x02;
    
    /*交易数据类型*/
    public final static byte POSDATATYPE_NORMAL = 0x00;//正常交易数据
    public final static byte POSDATATYPE_RECOLLECT = 0x01;//补采交易数据
    
    public final static int POSDATA_QUEUE_CAPACITY = 15000;//存放交易数据的队列
    
    public final static String POSCMD_LOG_DELIMITER = "$";
    
    public final static byte[] BLANKPOSDATA = new byte[256];
    public final static String BLANKSTR = "";

    /*应用更改日志*/
    public final static String APP_CHANGE_LOG = ""
    										  + " 2012-07-23:\n"
    										  + "   1.Download blacklist file, discount file, recollect file with file extension again if FtpDownloadStatus is \n"
    										  + "     DOWNLOAD_REMOTE_FILE_NOT_EXIST at first time.\n"
    										  + "   2.Add a heartbeat cmd between server and pos terminal.\n"
    										  + "\n"
    										  + " 2012-07-26:\n"
    										  + "   1.Fix bug: wrong collect station no in pos deal file head.\n"
    										  + "\n"
    										  + " 2012-08-07:\n"
    										  + "   1.Change pos deal data file sno generation rule.\n"
    										  + "\n"
    										  + " 2012-08-10:\n"
    										  + "   1.Filter pos deal data with same sno from same pos terminal since it is connected to server.\n"
    										  + "\n"
    										  + " 2012-08-22:\n"
    										  + "   1.Add FtpClient read data timeout.\n"
    										  ;
}
