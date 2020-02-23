package de.hsharz.empfehlungssystem.database;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.tomcat.dbcp.dbcp2.BasicDataSource;

public class DatabaseConnection {

	private static final String DATABASE = "HARZLABOR";
	private static final String DATABASE_URL = "jdbc:oracle:thin:@oracle.hs-harz.de:1521:" + DATABASE;
	private static final String USER = "LABOR_HARZ_007";
	private static final String PASSWORD = "DdWg0e0q2G";
	private static final String DATABASE_DRIVER = "oracle.jdbc.driver.OracleDriver";

	// Pool mit Datenbankverbindungen
	private static BasicDataSource connectionPool;

	private DatabaseConnection() {
		// Single instance
	}

	/**
	 * Liefert eine neue Connection zu der Datenbank.
	 * 
	 * @return eine neue Connection zu der Datenbank
	 * @throws SQLException Falls
	 */
	public static Connection getConnection() throws SQLException {
		if (isConnectionClosed()) {
			establishConnection();
		}
		return connectionPool.getConnection();
	}

	/**
	 * 
	 */
	private static void establishConnection() {
		BasicDataSource basicDataSource = new BasicDataSource();
		basicDataSource.setDriverClassName(DATABASE_DRIVER);
		basicDataSource.setUrl(DATABASE_URL);
		basicDataSource.setUsername(USER);
		basicDataSource.setPassword(PASSWORD);
		connectionPool = basicDataSource;
	}

	/**
	 * Gibt an, ob die Verbindung zur Datenbank getrennt wurde bzw. nicht aufgebaut
	 * ist
	 * 
	 * @return true, falls keine Verbindung besteht; andernfalls false
	 */
	public static boolean isConnectionClosed() {
		return connectionPool == null || connectionPool.isClosed();
	}

	/**
	 * Schlieﬂt alle Connections (den ConnectionPool) zu der Datenbank
	 * 
	 * @throws SQLException Fehler beim Schlieﬂen der Connections
	 */
	public static void closeConnection() throws SQLException {
		if (connectionPool != null) {
			connectionPool.close();
			connectionPool = null;
		}
	}

}
