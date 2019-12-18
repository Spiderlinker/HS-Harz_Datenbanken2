package de.hsharz.empfehlungssystem.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import de.hsharz.empfehlungssystem.beans.Event;
import de.hsharz.empfehlungssystem.beans.User;
import de.hsharz.empfehlungssystem.database.function.SqlFunction;

public class DatabaseAdapter {

	private DatabaseAdapter() {
		// Utils Klasse
	}

	public static User getUser(String username, String password) throws SQLException {
		return runWithConnection(conn -> {

			PreparedStatement prepareStatement = conn
					.prepareStatement("SELECT * FROM customer WHERE emailaddress = ? AND PASSWORD = ?");
			prepareStatement.setString(1, username);
			prepareStatement.setString(2, password);
			ResultSet result = prepareStatement.executeQuery();

			User user = new User();
			user.setUsername(username);

			if (result.next()) {
				user.setId(result.getInt("UserID"));
				user.setFirstname(result.getString("Firstname"));
				user.setLastname(result.getString("Lastname"));
				user.setStreet(result.getString("Street"));
				user.setHouseNr(result.getString("Housenumber"));
				user.setZip(result.getString("ZIP"));
				user.setCity(result.getString("City"));
				user.setCountry(result.getString("Country"));
				user.setCountryShort(result.getString("Country_Short"));
				user.setEmail(result.getString("Emailaddress"));
				user.setGender(result.getString("Gender"));
				user.setBirthday(result.getString("Birthday_Date"));
				user.setPreference1(result.getString("Preference1"));
				user.setPreference2(result.getString("Preference2"));
				user.setPreference3(result.getString("Preference3"));
			} else {
				user = null;
			}

			return user;
		});
	}

	public static List<String> getAllGenreNames() throws SQLException {
		return runWithConnection(conn -> {
			List<String> genreNames = new ArrayList<>();

			PreparedStatement statement = conn.prepareStatement("SELECT distinct typename FROM events");
			ResultSet result = statement.executeQuery();
			while (result.next()) {
				genreNames.add(result.getString(1));
			}

			return genreNames;
		});
	}

	public static boolean registerUser(User user) throws SQLException {
		return runWithConnection(conn -> {

			PreparedStatement statement = conn.prepareStatement("INSERT INTO CUSTOMER VALUES (CUSTOMERIDSEQ.NEXTVAL," //
					+ "?," // 1 Firstname
					+ "?," // 2 Lastname
					+ "?," // 3 Street
					+ "?," // 4 House Number
					+ "?," // 5 ZIP
					+ "?," // 6 City
					+ "(SELECT DE from countries where code = ?)," // 7 Country
					+ "?," // 8 Country_Short
					+ "?," // 9 Email
					+ "?," // 10 Email_Provider
					+ "?," // 11 Password
					+ "?," // 12 Gender
					+ "?," // 13 Gender_Short
					+ "?," // 14 Birthday_Date
					+ "to_char(to_date(?, 'DD.MM.YYYY'), 'J')," // 15 Birthday_Julian
					+ "?," // 16 Preference1
					+ "?," // 17 Preference2
					+ "?" // 18 Preference3
					+ ")");

			statement.setString(1, user.getFirstname());
			statement.setString(2, user.getLastname());
			statement.setString(3, user.getStreet());
			statement.setString(4, user.getHouseNr());
			statement.setString(5, user.getZip());
			statement.setString(6, user.getCity());
			statement.setString(7, user.getCountryShort());
			statement.setString(8, user.getCountryShort());
			statement.setString(9, user.getEmail());
			statement.setString(10, user.getEmail().substring(user.getEmail().indexOf('@') + 1));
			statement.setString(11, user.getPassword());
			statement.setString(12, user.getGender().toLowerCase());
			statement.setString(13, String.valueOf(user.getGender().charAt(0)).toLowerCase());
			statement.setString(14, user.getBirthday());
			statement.setString(15, user.getBirthday());
			statement.setString(16, user.getPreference1());
			statement.setString(17, user.getPreference2());
			statement.setString(18, user.getPreference3());

			int result = statement.executeUpdate();

			return result == 1;
		});
	}

	public static List<String> getCountryCodes() throws SQLException {
		return runWithConnection(conn -> {
			List<String> countryCodes = new ArrayList<>();

			PreparedStatement statement = conn.prepareStatement("SELECT code FROM COUNTRIES");
			ResultSet result = statement.executeQuery();
			while (result.next()) {
				countryCodes.add(result.getString(1));
			}

			return countryCodes;
		});
	}

	public static String getStatistik() throws SQLException {
		return runWithConnection(conn -> {

			PreparedStatement statement = conn.prepareStatement("SELECT * FROM ANALYTICS_ANFRAGE");
			ResultSet result = statement.executeQuery();

			result.next();

			PreparedStatement stmtStatistik = conn.prepareStatement(result.getString(1));
			ResultSet restulStatistik = stmtStatistik.executeQuery();

			restulStatistik.next();
			return restulStatistik.getString(1);
		});
	}

	public static List<Event> getAllPurchases(User loggedInUser) throws SQLException {
		return runWithConnection(conn -> {
			List<Event> events = new ArrayList<>();

			PreparedStatement statement = conn.prepareStatement("SELECT * FROM events");
//			statement.setInt(1, loggedInUser.getId());

			ResultSet result = statement.executeQuery();
			while (result.next()) {
				Event event = new Event();
				event.setId(result.getInt("EVENTID"));
				event.setTitle(result.getString("TITLE"));
				event.setDescription(result.getString("DESCRIPTION"));
				event.setGenre(result.getString("GENRE"));
				event.setSubGenre(result.getString("SUB_GENRE"));
				event.setCity(result.getString("CITY"));
				event.setPrice(result.getInt("PRICE"));
				event.setDate(result.getDate("DATE"));
				events.add(event);
			}

			return events;
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
