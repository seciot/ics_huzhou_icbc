/**
 * @author Jadic
 * @created 2011-10-25 
 */
package com.jadic.db;

import java.sql.Connection;
import java.sql.SQLException;

import com.jadic.utils.KKLog;
import com.jadic.utils.SysParams;
import com.jolbox.bonecp.BoneCP;
import com.jolbox.bonecp.BoneCPConfig;

public class DBConnPool {
	private static DBConnPool connPool = null;
	private BoneCP boneCP = null;

	public static synchronized DBConnPool getInstance(SysParams sysParams)
			throws ClassNotFoundException, SQLException {
		if (connPool == null) {
			connPool = new DBConnPool(sysParams);
			KKLog.info("DBConnPool created");
		}
		return connPool;
	}

	private DBConnPool(SysParams sysParams) throws ClassNotFoundException,
			SQLException {
		Class.forName(sysParams.getJdbcDriver());
		BoneCPConfig config = new BoneCPConfig();
		config.setJdbcUrl(sysParams.getJdbcUrl());
		config.setUsername(sysParams.getDbUserName());
		config.setPassword(sysParams.getDbUserPass());
		config.setMinConnectionsPerPartition(2);
		config.setMaxConnectionsPerPartition(5);
		config.setPartitionCount(2);
		config.setAcquireIncrement(3);
		config.setIdleMaxAgeInMinutes(20);
		config.setIdleConnectionTestPeriodInMinutes(2);
		boneCP = new BoneCP(config);
	}

	public Connection getConnection() throws SQLException {
		return boneCP.getConnection();
	}

	public void shutDown() {
		boneCP.shutdown();
	}

}
