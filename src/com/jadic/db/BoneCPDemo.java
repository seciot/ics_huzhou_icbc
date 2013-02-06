package com.jadic.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.jadic.utils.KKLog;
import com.jolbox.bonecp.BoneCP;
import com.jolbox.bonecp.BoneCPConfig;

public class BoneCPDemo {

	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		
		BoneCP boneCP = null;
		Connection connection = null;
		
		try {
			Class.forName("oracle.jdbc.OracleDriver");
		} catch (ClassNotFoundException e) {
			KKLog.error("load database driver error:" + e.getMessage());
			return;
		}

		BoneCPConfig config = new BoneCPConfig();
		config.setJdbcUrl("jdbc:oracle:thin:@192.168.2.19:1521:tysoft");
		config.setUsername("tybus");
		config.setPassword("njtynjty");
		config.setMinConnectionsPerPartition(5);
		config.setMaxConnectionsPerPartition(10);
		config.setPartitionCount(2);
		
		try {
			boneCP = new BoneCP(config);
		} catch (SQLException e) {
			KKLog.error("create BoneCP error");
			e.printStackTrace();
		}
		
		try {
			connection = boneCP.getConnection();
			if (connection != null) {
				KKLog.info("getConnection Successfully");
				Statement statement = connection.createStatement();
				ResultSet rs = statement.executeQuery("select count(1) as carCount from tab_carinfo");
				if (rs.next()) {
					KKLog.info("CarCount:" + rs.getInt("carCount"));
				}
			}
		} catch (SQLException e) {
			KKLog.error("Get BoneCp connection error:" + e.getMessage());
			e.printStackTrace();
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					KKLog.error("close connection error:" + e.getMessage());
				}
			}
			if (boneCP != null) {
				boneCP.shutdown();
			}
		}
		
	}

}
