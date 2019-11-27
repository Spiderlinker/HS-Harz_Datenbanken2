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

	public static boolean registerUser(User user) throws SQLException {
		return runWithConnection(conn -> {

			PreparedStatement statement = conn.prepareStatement("INSERT INTO CUSTOMER  values(CUSTOMERIDSEQ.NEXTVAL," //
					+ "?," // 1 Firstname
					+ "?," // 2 Lastname
					+ "?," // 3 Street
					+ "?," // 4 House Number
					+ "?," // 5 ZIP
					+ "?," // 6 City
					+ "?," // 7 Country
					+ "(SELECT code from countries where de = ?)," // 8 Country_Code Short
					+ "?," // 9 Email
					+ "?," // 10 Email_Provider
					+ "?," // 11 Password
					+ "?," // 12 Gender
					+ "?," // 13 Gender_Short
					+ "?," // 14 Birthday_Date
					+ "?," // 15 Birthday_Julian
					+ ")");

			statement.setString(1, user.getFirstname());
			statement.setString(2, user.getLastname());
			statement.setString(3, user.getStreet());
			statement.setString(4, user.getHouseNr());
			statement.setString(5, user.getZip());
			statement.setString(6, user.getCity());
			statement.setString(7, user.getCountry());
			statement.setString(8, user.getCountry());
			statement.setString(9, user.getEmail());
			statement.setString(10, user.getEmail().substring(user.getEmail().indexOf('@') + 1));

			int result = statement.executeUpdate();

			return false;
		});
	}

	public static List<String> getCountryCodes() throws SQLException {
		return runWithConnection(conn -> {
			List<String> countryCodes = new ArrayList<>();

			PreparedStatement statement = conn.prepareStatement(
					"SELECT code FROM COUNTRIES");
			ResultSet result = statement.executeQuery();
			while (result.next()) {
				countryCodes.add(result.getString(1));
			}
			
			return countryCodes;
		});
	}

	/**
	 * Führt die gegebene Funktion aus und übergibt dieser eine Verbindung zur
	 * Datenbank
	 * 
	 * @param <T>      Rückgabedatentyp der gegebenen Funktion
	 * @param function Funktion, die mit einer Datenbankverbindung ausgeführt werden
	 *                 soll
	 * @return Rückgabewert der gegebenen Function
	 * @throws SQLException Fehler bei der Ausführung
	 */
	private static <T> T runWithConnection(SqlFunction<T, Connection> function) throws SQLException {
		try (Connection conn = getConnection()) {
			return function.apply(conn);
		}
	}

	/**
	 * Führt die gegebene Funktion mit einer Datenbankverbindung aus. Bei der in die
	 * gegebene Funktion übergebene Connection ist AutoCommit deaktiviert. Der
	 * Commit erfolgt nach der Ausführung der Funktion. Falls während der Ausführung
	 * der Funktion etwas schief läuft, so wird ein Rollback durchgeführt. Am Ende
	 * wird die Connection geschlossen und der von der Funktion zurückgegebene Wert
	 * zurückgegeben.
	 * 
	 * @param <T>   Rückgabedatentyp der gegebenen Funktion
	 * @param query Funktion, die mit einer Connection (mit ggf. Rollback bei
	 *              Fehlern) ausgeführt werden soll
	 * @return Rückgabewert der gegebenen Funktion
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
	 * {@link Connection#prepareStatement} ausgeführt werden können
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
