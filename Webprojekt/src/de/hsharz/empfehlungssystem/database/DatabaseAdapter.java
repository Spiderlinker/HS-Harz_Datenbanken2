package de.hsharz.empfehlungssystem.database;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import de.hsharz.empfehlungssystem.beans.Event;
import de.hsharz.empfehlungssystem.beans.TicketType;
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

	public static boolean putPurchase(Event event, int amountOfTickets, String paymethod, String ticketTypeName,
			User user) throws SQLException {
		return runWithConnection(conn -> {

			PreparedStatement statement = conn.prepareStatement("INSERT INTO PURCHASES VALUES (" //
					+ "?," // 1 EventID
					+ "?," // 2 UserID
					+ "?," // 3 Timestamp
					+ "?," // 4 AmountOfTickets
					+ "?," // 5 PayedPricePerTicket
					+ "?," // 6 PayerdPricePerTicket_Cent
					+ "?," // 7 PayedPriceTotal
					+ "?," // 8 PayedPriceTotal_Cent
					+ "?," // 9 Paymethod
					+ "?," // 10 TicketType
					+ "?," // 11 Price_Advantage_Total
					+ "?" // 12 Price_Advantage_Total_Cent
					+ ")");

			statement.setInt(1, event.getId());
			statement.setInt(2, user.getId());
			statement.setDate(3, new Date(System.currentTimeMillis()));
			statement.setInt(4, amountOfTickets);

			ResultSet r = conn.createStatement()
					.executeQuery("SELECT PERCENTAGE FROM TICKETTYPES WHERE NAME = '" + ticketTypeName + "'");
			double percentage = 0;
			if (r.next()) {
				percentage = r.getInt(1);
			}

			double invPercentage = (1 - percentage / 100);
			double payedPricePT = event.getPrice() * invPercentage;
			statement.setDouble(5, payedPricePT);
			statement.setDouble(6, payedPricePT * 100);
			statement.setDouble(7, payedPricePT * amountOfTickets);
			statement.setDouble(8, payedPricePT * 100 * amountOfTickets);
			statement.setString(9, paymethod);
			statement.setString(10, ticketTypeName);
			statement.setDouble(11, (event.getPrice() - payedPricePT) * amountOfTickets);
			statement.setDouble(12, (event.getPrice() - payedPricePT) * amountOfTickets * 100);

			int result = statement.executeUpdate();

			return result == 1;
		});
	}

	public static boolean insertRating(User user, String event, Integer rating) throws SQLException {
		return runWithConnection(conn -> {

			PreparedStatement statement = conn.prepareStatement("INSERT INTO RATINGS VALUES (" //
					+ "?," // 1 UserID
					+ "?," // 2 EventID
					+ "?," // 3 Rating
					+ "?," // 4 Timestamp
					+ "(SELECT DURATION_IN_MINUTES FROM EVENTS WHERE EVENTID = ?)" // 5 Event_Duration
					+ ")");

			statement.setInt(1, user.getId());
			statement.setString(2, event);
			statement.setInt(3, rating);
			statement.setDate(4, new Date(System.currentTimeMillis()));
			statement.setString(5, event);

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

	public static List<Event> getAllEvents() throws SQLException {
		return runWithConnection(conn -> {
			List<Event> events = new ArrayList<>();

			PreparedStatement statement = conn.prepareStatement("SELECT * FROM events");

			ResultSet result = statement.executeQuery();
			while (result.next()) {
				events.add(createEventFromResult(result));
			}

			return events;
		});
	}

	public static Object getEvent(String eventID) throws SQLException {
		return runWithConnection(conn -> {

			PreparedStatement statement = conn.prepareStatement("SELECT * FROM events WHERE eventID = ?");
			statement.setString(1, eventID);

			ResultSet result = statement.executeQuery();
			while (result.next()) {
				return createEventFromResult(result);
			}

			return null;
		});
	}

	public static List<Event> getPurchasesOfUser(User user) throws SQLException {
		return runWithConnection(conn -> {
			List<Event> events = new ArrayList<>();

			PreparedStatement statement = conn.prepareStatement(
					"SELECT * FROM events WHERE eventID IN (select eventID from purchases where userID = ?) "
							+ "AND eventID not in (select eventID from ratings where userID = ? AND rating > 0)");
			statement.setInt(1, user.getId());
			statement.setInt(2, user.getId());

			ResultSet result = statement.executeQuery();
			while (result.next()) {
				events.add(createEventFromResult(result));
			}

			return events;
		});
	}

	private static Event createEventFromResult(ResultSet result) throws SQLException {
		Event event = new Event();
		event.setId(result.getInt("EVENTID"));
		event.setTitle(result.getString("TITLE"));
		event.setDescription(result.getString("DESCRIPTION"));
		event.setGenre(result.getString("GENRE"));
		event.setSubGenre(result.getString("SUB_GENRE"));
		event.setCity(result.getString("CITY"));
		event.setZIP(result.getString("ZIP"));
		event.setStreet(result.getString("STREET"));
		event.setHouseNumber(result.getString("HOUSENUMBER"));
		event.setPrice(result.getDouble("PRICE"));
		event.setDate(result.getDate("DATE"));
		event.setTime(result.getTime("DATE"));
		event.setOrganizer(result.getString("ORGANIZERNAME"));
		event.setDuration(result.getLong("DURATION_IN_MINUTES"));
		return event;
	}

	public static List<String> getPaymethods() throws SQLException {
		return runWithConnection(conn -> {

			List<String> paymethods = new ArrayList<>();
			PreparedStatement statement = conn.prepareStatement("SELECT * FROM paymethod");

			ResultSet result = statement.executeQuery();
			while (result.next()) {
				paymethods.add(result.getString(1));
			}

			return paymethods;
		});
	}

	public static List<TicketType> getTicketTypes() throws SQLException {
		return runWithConnection(conn -> {

			List<TicketType> ticketTypes = new ArrayList<>();
			PreparedStatement statement = conn.prepareStatement("SELECT * FROM tickettypes");

			ResultSet result = statement.executeQuery();
			while (result.next()) {
				TicketType type = new TicketType();
				type.setName(result.getString(1));
				type.setPercentage(result.getInt(2));
				ticketTypes.add(type);
			}

			return ticketTypes;
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
