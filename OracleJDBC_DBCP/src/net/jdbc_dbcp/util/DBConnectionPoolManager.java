package net.jdbc_dbcp.util;

import java.sql.Connection;
import java.util.Hashtable;
import java.util.Vector;
// 3
public class DBConnectionPoolManager {
	// DBConnectionPoolManager 에 싱글턴 패턴을 적용하기 위해(인스턴스를 하나만 유지) static 으로 선언
	static private DBConnectionPoolManager instance;
	private Vector<String> drivers = new Vector<String>();
	private Hashtable<String, DBConnectionPool> pools = new Hashtable<String, DBConnectionPool>();
	
	//DBConnectionPoolManager의 instance를 얻음
	// @return DBConnectionManager
	static synchronized public DBConnectionPoolManager getInstance() {
		if (instance == null) {
			instance = new DBConnectionPoolManager();
		}
		return instance;
	}
	
	// Default Constructor
	private DBConnectionPoolManager() {}
	
	// 현재 Connection을 Free Connection List로 보냄
	// @param name : Pool Name
	// @param con : Connection
	public void freeConnection(String name, Connection con) {
		DBConnectionPool pool = (DBConnectionPool) pools.get(name);
		if (pool != null) {
			pool.freeConnection(con);
		}
		
		Log.out("One Connection of " + name + " was freed");
	}
	
	// Open Connection을 얻음. 현재 열린 커넥션이 없고 최대 커넥션 개수가
	// 사용 중이 아닐 때 새로운 커넥션을 생성. 현재 열린 커넥션이 없고
	// 최대 커넥션 개수가 사용 중일 때 기본 대기 시간을 기다림
	// @param name : Pool Name
	// @return Connection : The connection or null
	public Connection getConnection(String name) {
		DBConnectionPool pool = (DBConnectionPool) pools.get(name);
		if (pool != null) {
			return pool.getConnection(10);
		}
		return null;
	}
	
	// Connection Pool을 생성
	// @param poolName : 생성할 Pool Name
	// @param url : DB URL
	// @param user : DB UserID
	// @param password : DB Password
	private void createPools(String poolName,
			String url,
			String user,
			String password,
			int maxConn,
			int initConn,
			int maxWait	) {
		DBConnectionPool pool = new DBConnectionPool(poolName, url, user, password, maxConn, initConn, maxWait);
		pools.put(poolName, pool);
		Log.out("Initialized pool " + poolName);
	}
	
	// 초기화 작업
	public void init(String poolName,
			String driver,
			String url,
			String user,
			String password,
			int maxConn,
			int initConn,
			int maxWait) {
		loadDrivers(driver);
		createPools(poolName, url, user, password, maxConn, initConn, maxWait);
	}
	
	//	jDBC Driver Loading
	// @param driverClassName : 사용하고자 하는 DB의 JDBC 드라이버
	private void loadDrivers(String driverClassName) {
		try {
			Class.forName(driverClassName);
			drivers.addElement(driverClassName);
			Log.out("Registered JDBC driver " + driverClassName);
		} catch (ClassNotFoundException e) {
			Log.err(e, "Can't resgister JDBC driver: " + driverClassName);
		}
	}
	
	public Hashtable<String, DBConnectionPool> getPools() {
		return pools;
	}
	
	public int getDriverNumber() {
		return drivers.size();
	}
}
