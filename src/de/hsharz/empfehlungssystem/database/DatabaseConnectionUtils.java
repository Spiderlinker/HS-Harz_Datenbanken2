package de.hsharz.empfehlungssystem.database;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseConnectionUtils {

	private DatabaseConnectionUtils() {
		// Utils-Klasse
	}

	public static void close(AutoCloseable c) {
		if (c != null) {
			try {
				c.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void rollback(Connection conn) {
		if (conn != null) {
			try {
				conn.rollback();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

}
