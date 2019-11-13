package de.hsharz.empfehlungssystem.database;

import java.nio.channels.ConnectionPendingException;
import java.sql.Connection;
import java.sql.SQLException;

import org.apache.tomcat.dbcp.dbcp2.BasicDataSource;

public class DatabaseConnection {

//	private static final String HOSTNAME = "oracle.hs-harz.de";
//	private static final String PORT = "1521";
	private static final String DATABASE = "HARZDB01";
	private static final String DATABASE_URL = "jdbc:oracle:thin:@oracle.hs-harz.de:1521:" + DATABASE;
	private static final String USER = "BROCKEN_001";
	private static final String PASSWORD = "BROCKEN_003";
	private static final String DATABASE_DRIVER = "oracle.jdbc.driver.OracleDriver";
	
	private static BasicDataSource connectionPool;

	private DatabaseConnection() {
		// Single instance
	}

	private static void establishConnection() {
		if (!isConnectionClosed()) {
			throw new ConnectionPendingException();
		}

		BasicDataSource basicDataSource = new BasicDataSource();
		basicDataSource.setDriverClassName(DATABASE_DRIVER);
		basicDataSource.setUrl(DATABASE_URL);
		basicDataSource.setUsername(USER);
		basicDataSource.setPassword(PASSWORD);
		connectionPool = basicDataSource;
	}

	public static boolean isConnectionClosed() {
		return connectionPool == null || connectionPool.isClosed();
	}

	public static void closeConnection() throws SQLException {
		if (connectionPool != null) {
			connectionPool.close();
			connectionPool = null;
		}
	}

	public static Connection getConnection() throws SQLException {
		if (isConnectionClosed()) {
			establishConnection();
		}
		return connectionPool.getConnection();
	}

}
