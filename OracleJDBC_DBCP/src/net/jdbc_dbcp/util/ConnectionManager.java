package net.jdbc_dbcp.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.util.Properties;

// 4
public class ConnectionManager {
	protected DBConnectionPoolManager connMgr;
	protected String poolName, dbServer, dbName, port, userID, passwd;
	protected int maxConn, initConn, maxWait;
	private Properties dbProperties;
	private String configFile;

	public ConnectionManager(String pool) {
		poolName = pool;
		// Property 파일 디렉토리 지정
		configFile = "C:/java/jdbc/" + poolName + ".properties";

		try {
			dbProperties = readProperties();
			dbServer = getProperty("dbServer");
			port = getProperty("port");
			dbName = getProperty("dbName");
			userID = getProperty("userID");
			passwd = getProperty("passwd");
			maxConn = Integer.parseInt(getProperty("maxConn"));
			initConn = Integer.parseInt(getProperty("initConn"));
			maxWait = Integer.parseInt(getProperty("maxWait"));
		} catch (IOException ioe) {
			Log.err("Error reading properties of " + configFile);
		}
	}

	public Connection getConnection() {
		return (connMgr.getConnection(poolName));
	}

	public void freeConnection(Connection conn) {
		connMgr.freeConnection(poolName, conn);
	}

	private String getProperty(String prop) throws IOException {
		return (dbProperties.getProperty(prop));
	}

	protected synchronized Properties readProperties() throws IOException {
		Properties tempProperties = new Properties();
		FileInputStream in = new FileInputStream(configFile);
		tempProperties.load(in);
		return tempProperties;
	}

	public int getDeriverNumber() {
		return connMgr.getDriverNumber();
	}
}
