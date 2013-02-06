/**
 * @author Jadic
 * @created 2012-5-8 
 */
package com.jadic.utils;


public class SysParams {

	/* tcp相关 */
	private int localTcpPort;// 本地监听tcp端口
	private int autoDisconnectMinutes = 2 * 60;// 自动断开的时间
	
	/*tcp客户端连接的Ip，工行连接的限制IP*/
	private String clientIp = "192.168.6.19";
	
	/* 数据库相关参数 */
	private String jdbcDriver;
	private String jdbcUrl;
	private String dbUserName;
	private String dbUserPass;

	private KKConfig kkConfig;

	public SysParams() {
		String configFilePath = "." + System.getProperty("file.separator")
				+ "ics.properties";
		kkConfig = new KKConfig(configFilePath);
	}

	public void loadSysParams() {
		this.localTcpPort = kkConfig.getIntValue("localTcpPort");
		this.autoDisconnectMinutes = kkConfig
				.getIntValue("autoDisconnectMinutes");
		if (this.autoDisconnectMinutes <= 0) {
			this.autoDisconnectMinutes = 3;
		}
		
		this.clientIp = kkConfig.getStrValue("clientIp");
		
		this.jdbcDriver = kkConfig.getStrValue("jdbcDriver");
		this.jdbcUrl = kkConfig.getStrValue("jdbcUrl");
		this.dbUserName = kkConfig.getStrValue("dbUserName");
		this.dbUserPass = kkConfig.getStrValue("dbUserPass");
	}

	public int getLocalTcpPort() {
		return localTcpPort;
	}

	public void setLocalTcpPort(int localTcpPort) {
		this.localTcpPort = localTcpPort;
	}

	public int getAutoDisconnectMinutes() {
		return autoDisconnectMinutes;
	}

	public void setAutoDisconnectMinutes(int autoDisconnectMinutes) {
		this.autoDisconnectMinutes = autoDisconnectMinutes;
	}

	public KKConfig getKkConfig() {
		return kkConfig;
	}

	public void setKkConfig(KKConfig kkConfig) {
		this.kkConfig = kkConfig;
	}

	public String getJdbcDriver() {
		return jdbcDriver;
	}

	public void setJdbcDriver(String jdbcDriver) {
		this.jdbcDriver = jdbcDriver;
	}

	public String getJdbcUrl() {
		return jdbcUrl;
	}

	public void setJdbcUrl(String jdbcUrl) {
		this.jdbcUrl = jdbcUrl;
	}

	public String getDbUserName() {
		return dbUserName;
	}

	public void setDbUserName(String dbUserName) {
		this.dbUserName = dbUserName;
	}

	public String getDbUserPass() {
		return dbUserPass;
	}

	public void setDbUserPass(String dbUserPass) {
		this.dbUserPass = dbUserPass;
	}

	public String getClientIp() {
		return clientIp;
	}

	public void setClientIp(String clientIp) {
		this.clientIp = clientIp;
	}
	
}
