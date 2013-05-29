package com.jadic.db;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import com.jadic.cmd.PosData;
import com.jadic.utils.KKLog;
import com.jadic.utils.KKTool;
import com.jadic.utils.SysParams;


/**
 * 数据操作
 * 
 * @author Jadic Wei
 * @created Feb 23, 2011
 */
public class DBOper {
	
	private static DBOper dbOper;
	private DBConnPool dbConnPool;
	
	public synchronized static DBOper getDBOper(SysParams sysParams) {
		if (dbOper == null) {
			dbOper = new DBOper(sysParams);
		}
		return dbOper;
	}

	private DBOper(SysParams sysParams) {
		try {
			dbConnPool = DBConnPool.getInstance(sysParams);
		} catch (ClassNotFoundException e) {
			KKLog.error("DBOper create ClassNotFoundException:" + e.getMessage());
		} catch (SQLException e) {
			KKLog.error("DBOper create SQLException:" + e.getMessage());
		}
	}
	
	public void getPosDataUnsend(List<PosData> posDatas) {
		Connection conn = null;
		PosData posData = null;
		InputStream is = null;
		byte[] buf = null;
		try {
			conn = getConnection();
			if (conn != null) {
				Statement statement = conn.createStatement();
				ResultSet rs = statement.executeQuery("select id, posid, recvtime, data from TAB_POSDATA_UNSEND order by id");
				while (rs.next()) {
					posData = new PosData();
					posData.setId(rs.getInt("id"));
					posData.setPosId(rs.getString("posid"));
					posData.setRecvTime(rs.getString("recvtime"));
					is = rs.getBinaryStream("data");
					buf = new byte[256];
					is.read(buf);
					posData.setData(buf);
					//posData.setData(rs.getBytes("data"));
					posDatas.add(posData);
				}
				
				KKLog.info("查询未发送交易数据成功,记录数:" + posDatas.size());
			}
		} catch (Exception e) {
			KKLog.error("查询未发送交易数据异常" + KKTool.getExceptionTip(e));
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (Exception e2) {
				}
			}
		}
	}
	
	public boolean updatePosDataState(List<PosData> posDatas) {
		if (posDatas.size() <= 0) {
			return false;
		}
		Connection conn = null;
		PreparedStatement statement = null;
		try {
			conn = getConnection();
			if (conn != null) {
				conn.setAutoCommit(false);
				StringBuffer sql = new StringBuffer();
				sql.append("insert into tab_posdata_send (id, posid, sendtime, recvtime, data) ");
				sql.append("select s_tab_posdata_send.nextval, posid, TO_CHAR(SYSDATE, 'YYYY-MM-DD HH24:MI:SS'), recvtime, data from tab_posdata_unsend ");
				sql.append("where tab_posdata_unsend.id in (");
				for (int i = 0; i < posDatas.size(); i ++) {
					if (i == 0) {
						sql.append("?");
					} else {
						sql.append(", ?");
					}
				}
				sql.append(") ");

				statement = conn.prepareStatement(sql.toString());
				for (int i = 0; i < posDatas.size(); i ++) {
					statement.setInt(i + 1, posDatas.get(i).getId());
				}
				statement.execute();
				
				sql = new StringBuffer("delete from tab_posdata_unsend where id = ?");
				statement = conn.prepareStatement(sql.toString());
				for (PosData posData : posDatas) {
					statement.setInt(1, posData.getId());
					
					statement.addBatch();
				}
				statement.executeBatch();
				
				conn.commit();
				
				KKLog.info("成功转移交易数据条数:" + posDatas.size());
				return true;
			}
		} catch (Exception e) {
			KKLog.error("转移交易数据异常" + KKTool.getExceptionTip(e));
		} finally {
			if (statement != null) {
				try {
					statement.close();
				} catch (Exception e2) {
				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (Exception e2) {
				}
			}
		}
		return false;
	}
	
	public boolean savePosData(List<byte[]> posDatas) {
		Connection conn = null;
		try {
			conn = getConnection();
			conn.setAutoCommit(false);
			if (conn != null) {
				PreparedStatement statement = conn.prepareStatement("insert into tab_posdata_unsend values(s_tab_posdata_unsend.nextval, ?, ?, ?)");
				for (byte[] posData : posDatas) {
					statement.setString(1, KKTool.byteArrayToHexStr(posData, 0, 4));
					statement.setString(2, KKTool.byteArrayToHexStr(posData));
					statement.setString(3, KKTool.getCurrFormatDateTime());
					statement.addBatch();
				}
				statement.executeBatch();
				conn.commit();
				statement.close();
				
				KKLog.info("成功保存交易数据条数:" + posDatas.size());
				return true;
			}
		} catch (Exception e) {
			KKLog.error("保存交易数据异常" + KKTool.getExceptionTip(e));
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (Exception e2) {
				}
			}
		}
		return false;
	}
	
	public long getCarInfoMaxRowscn() {
		Connection connection = null;
		try {
			connection = getConnection();
			Statement statement = connection.createStatement();
			ResultSet rs = statement.executeQuery("select max(ora_rowscn) as maxr from tab_carinfo");
			while (rs.next()) {
				return rs.getLong("maxr");
			}
		} catch (SQLException e) {
			KKLog.error("getCarInfoMaxRowscn异常:" + e.getMessage());
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
				}
			}
		}
		return 0;
	}
	
	private Connection getConnection() throws SQLException {
		return dbConnPool.getConnection();
	}
	
	public void release() {
		KKLog.info("释放数据库连接");
		this.dbConnPool.shutDown();
	}
	
}
