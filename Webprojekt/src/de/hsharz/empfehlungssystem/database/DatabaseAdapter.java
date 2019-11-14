package de.hsharz.empfehlungssystem.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import de.hsharz.empfehlungssystem.beans.User;
import de.hsharz.empfehlungssystem.database.function.SqlFunction;

public class DatabaseAdapter {

	private DatabaseAdapter() {
		// Utils Klasse
	}

	public static User getUser(String username, String password) throws SQLException {
		return runWithConnection(conn -> {

			PreparedStatement prepareStatement = conn
					.prepareStatement("SELECT USERID FROM CC_Vorlage_User WHERE USERNAME = ? AND PASSWORD = ?");
			prepareStatement.setString(1, username);
			prepareStatement.setString(2, password);
			ResultSet result = prepareStatement.executeQuery();

			User user = new User();
			user.setUsername(username);

			if (result.next()) {
				int userID = result.getInt("USERID");
				user.setId(userID);
			} else {
				user = null;
			}

			return user;
		});
	}

	public static List<String> getAllGenreNames() throws SQLException {
		return runWithConnection(conn -> {
			List<String> genreNames = new ArrayList<>();

			PreparedStatement statement = conn.prepareStatement(
					"SELECT distinct p.produktname FROM BEWERTUNGUSER b, CC_Produkte p where b.prodid = p.typid");
			ResultSet result = statement.executeQuery();
			while (result.next()) {
				genreNames.add(result.getString(1));
			}

			return genreNames;
		});
	}

	/**
	 * F�hrt die gegebene Funktion aus und �bergibt dieser eine Verbindung zur
	 * Datenbank
	 * 
	 * @param <T>      R�ckgabedatentyp der gegebenen Funktion
	 * @param function Funktion, die mit einer Datenbankverbindung ausgef�hrt werden
	 *                 soll
	 * @return R�ckgabewert der gegebenen Function
	 * @throws SQLException Fehler bei der Ausf�hrung
	 */
	private static <T> T runWithConnection(SqlFunction<T, Connection> function) throws SQLException {
		try (Connection conn = getConnection()) {
			return function.apply(conn);
		}
	}

	/**
	 * F�hrt die gegebene Funktion mit einer Datenbankverbindung aus. Bei der in die
	 * gegebene Funktion �bergebene Connection ist AutoCommit deaktiviert. Der
	 * Commit erfolgt nach der Ausf�hrung der Funktion. Falls w�hrend der Ausf�hrung
	 * der Funktion etwas schief l�uft, so wird ein Rollback durchgef�hrt. Am Ende
	 * wird die Connection geschlossen und der von der Funktion zur�ckgegebene Wert
	 * zur�ckgegeben.
	 * 
	 * @param <T>   R�ckgabedatentyp der gegebenen Funktion
	 * @param query Funktion, die mit einer Connection (mit ggf. Rollback bei
	 *              Fehlern) ausgef�hrt werden soll
	 * @return R�ckgabewert der gegebenen Funktion
	 * @throws SQLException Fehler beim Verbinden zu der Datenbank
	 */
	private static <T> T runWithConnectionRollback(SqlFunction<T, Connection> query) throws SQLException {
		Connection conn = getConnection();
		T functionReturn = null;
		try {
			conn.setAutoCommit(false);
			functionReturn = query.apply(conn);
			conn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
			DatabaseConnectionUtils.close(conn);
		} finally {
			DatabaseConnectionUtils.rollback(conn);
		}
		return functionReturn;
	}

	/**
	 * Liefert eine Datenbankverbindung, auf der Operationen wie
	 * {@link Connection#prepareStatement} ausgef�hrt werden k�nnen
	 * 
	 * @return Connection zur Datenbank
	 * @throws SQLException Fehler bei der Verbindung zur Datenbank
	 */
	private static Connection getConnection() throws SQLException {
		return DatabaseConnection.getConnection();
	}

	public static List<String> getAllEmpfehlungen(User loggedInUser) {
		return new ArrayList<>();
	}
}
