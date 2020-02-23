package de.hsharz.empfehlungssystem.database;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.hsharz.empfehlungssystem.beans.Event;
import de.hsharz.empfehlungssystem.beans.TicketType;
import de.hsharz.empfehlungssystem.beans.User;
import de.hsharz.empfehlungssystem.database.function.SqlFunction;

public class DatabaseAdapter {

	private DatabaseAdapter() {
		// Utils Klasse
	}

	// #####################################################################################
	// ----- User -----
	// #####################################################################################

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
					+ "?," // 18 Preference3
					+ "?" // 18 Birthday_Year
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
			statement.setInt(19,
					Integer.parseInt(user.getBirthday().substring(user.getBirthday().lastIndexOf('.') + 1)));

			int result = statement.executeUpdate();

			return result == 1;
		});
	}

	// #####################################################################################
	// ----- Purchases -----
	// #####################################################################################

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

	public static List<Event> getPurchasesOfUser(User user) throws SQLException {
		return runWithConnection(conn -> {
			List<Event> events = new ArrayList<>();

			PreparedStatement statement = conn.prepareStatement(
					"SELECT * FROM events WHERE eventID IN (select eventID from purchases where userID = ?) ");
			statement.setInt(1, user.getId());

			ResultSet result = statement.executeQuery();
			while (result.next()) {
				events.add(createEventFromResult(result));
			}

			return events;
		});
	}

	public static List<Event> getUnratedPurchasesOfUser(User user) throws SQLException {
		return runWithConnection(conn -> {
			List<Event> events = new ArrayList<>();

			// ...

			PreparedStatement statement = conn.prepareStatement("SELECT * " //
					+ "FROM events " //
					+ "WHERE eventID IN " + "    (" //
					+ "    SELECT eventID " //
					+ "    FROM purchases " //
					+ "    WHERE userID = ? " + "    ) " //
					+ "AND eventID NOT IN " + "    (" //
					+ "    SELECT eventID " //
					+ "    FROM ratings " //
					+ "    WHERE userID = ? " //
					+ "      AND rating > 0" //
					+ "    )");
			statement.setInt(1, user.getId());
			statement.setInt(2, user.getId());

			ResultSet result = statement.executeQuery();
			while (result.next()) {
				events.add(createEventFromResult(result));
			}

			return events;
		});
	}

	// #####################################################################################
	// ----- Ratings -----
	// #####################################################################################

	public static boolean insertRating(User user, String event, Integer rating) throws SQLException {
		return runWithConnection(conn -> {

			PreparedStatement statement = conn.prepareStatement("INSERT INTO RATINGS VALUES (" //
					+ "?," // 1 UserID
					+ "?," // 2 EventID
					+ "?," // 3 Rating
					+ "?" // 4 Timestamp
					+ ")");

			statement.setInt(1, user.getId());
			statement.setString(2, event);
			statement.setInt(3, rating);
			statement.setDate(4, new Date(System.currentTimeMillis()));

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

	// #####################################################################################
	// ----- Events -----
	// #####################################################################################

	public static List<String> getAllGenreNames() throws SQLException {
		return runWithConnection(conn -> {
			List<String> genreNames = new ArrayList<>();

			PreparedStatement statement = conn.prepareStatement("SELECT distinct genre FROM events");
			ResultSet result = statement.executeQuery();
			while (result.next()) {
				genreNames.add(result.getString(1));
			}

			return genreNames;
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

	public static List<Event> getAllEventTitleGrouped() throws SQLException {
		return runWithConnection(conn -> {
			List<Event> events = new ArrayList<>();

			PreparedStatement statement = conn.prepareStatement(
					"select title, description, typename from events group by title, typename order by typename asc");

			ResultSet result = statement.executeQuery();
			while (result.next()) {
				events.add(createLightEventFromResult(result));
			}

			return events;
		});
	}

	public static List<Event> getAllEventsOf(String eventTitle) throws SQLException {
		return runWithConnection(conn -> {
			List<Event> events = new ArrayList<>();

			PreparedStatement statement = conn.prepareStatement("SELECT * FROM events where title = ?");
			statement.setString(1, eventTitle);

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

	public static List<Event> getNewEvents() throws SQLException {
		return runWithConnection(conn -> {

			List<Event> events = new ArrayList<>();
			PreparedStatement statement = conn.prepareStatement("select title, typename, MAX(eventid) " //
					+ "from events " //
					+ "group by title, typename " //
					+ "order by MAX(eventid) desc fetch next 5 rows only" //
			);

			ResultSet result = statement.executeQuery();
			while (result.next()) {
				events.add(createLightEventFromResult(result));
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

	private static Event createLightEventFromResult(ResultSet result) throws SQLException {
		Event event = new Event();
		event.setTitle(result.getString("TITLE"));
		event.setGenre(result.getString("TYPENAME"));
		return event;
	}

	// #####################################################################################
	// ----- Recommendations -----
	// #####################################################################################

	public static List<Event> getRecommendationCollaborativeFiltering(int userID, String users, double minRating)
			throws SQLException {
		return runWithConnection(conn -> {

			List<Event> events = new ArrayList<>();
			String empfohleneProdukte = String.format(Locale.US, "SELECT DISTINCT title, typename FROM " //
					+ "    (SELECT e.title, e.description, e.typename, round(avg(r.rating), 2) schnitt from " //
					+ "        RATINGS r, EVENTS e " //
					+ "        WHERE " //
					+ "            r.eventid = e.eventid " //
					+ "            and userid in (%s) " //
					+ "            and rating > %.2f " //
					+ "        group by e.title, e.description, e.typename " //
					+ "        order by schnitt desc) " //
					+ "     where description not in (select e.description from EVENTS e, RATINGS r where r.userid = %s AND e.eventid = r.eventid) " //
					+ " FETCH NEXT %d ROWS ONLY", users, minRating, userID, 10);
			ResultSet result = conn.createStatement().executeQuery(empfohleneProdukte);
			while (result.next()) {
				events.add(createLightEventFromResult(result));
			}

			return events;
		});
	}

	public static List<Integer> getAehnlicheUser(int userID, double mindestAehnlichkeit) throws SQLException {
		return runWithConnection(conn -> {

			List<Integer> user = new ArrayList<>();
			PreparedStatement aehnlicheUser = conn.prepareStatement("select userid, Aehnlichkeit " //
					+ "from ( " //
					+ "    select zaehler.userid, (zaehler.skalarProdukt / nenner.prodRating) Aehnlichkeit " //
					+ "    from  " //
					+ "        ( " //
					+ "        select userid, sum(prodRating) skalarProdukt " //
					+ "        from ( " //
					+ "            select andere.userid, nutzer.eventid, (nutzer.ratingPS * andere.ratingPS) prodRating " //
					+ "            from  " //
					+ "                ( " //
					+ "                select userid, eventid, (rating - avgRating) ratingPS " //
					+ "                from ( " //
					+ "                    select userid, eventid, rating, avg(rating) over (partition by userid) avgRating " //
					+ "                    from RATINGS " //
					+ "                    )  " //
					+ "                where userid = ? " //
					+ "                ) nutzer " //
					+ "            , " //
					+ "                ( " //
					+ "                select userid, eventid, (rating - avgRating) ratingPS " //
					+ "                from ( " //
					+ "                    select userid, eventid, rating, avg(rating) over (partition by userid) avgRating " //
					+ "                    from RATINGS " //
					+ "                    )  " //
					+ "                where userid <> ? " //
					+ "                ) andere " //
					+ "            where nutzer.eventid = andere.eventid " //
					+ "            ) " //
					+ "        group by userid " //
					+ "        ) zaehler " //
					+ "    , " //
					+ "        ( " //
					+ "        select andere.userid, nutzer.ratingPS * andere.ratingPS prodRating " //
					+ "        from  " //
					+ "            ( " //
					+ "            select userid, sqrt(sum(ratingPS)) ratingPS " //
					+ "            from ( " //
					+ "                select userid, eventid, power(rating - avgRating, 2) ratingPS " //
					+ "                from ( " //
					+ "                    select userid, eventid, rating, avg(rating) over (partition by userid) avgRating " //
					+ "                    from RATINGS " //
					+ "                    ) " //
					+ "                where userid = ? " //
					+ "                ) " //
					+ "            group by userid " //
					+ "            ) nutzer " //
					+ "        , " //
					+ "            ( " //
					+ "            select userid, sqrt(sum(ratingPS)) ratingPS " //
					+ "            from ( " //
					+ "                select userid, eventid, power(rating - avgRating, 2) ratingPS " //
					+ "                from ( " //
					+ "                    select userid, eventid, rating, avg(rating) over (partition by userid) avgRating " //
					+ "                    from RATINGS " //
					+ "                    ) " //
					+ "                where userid <> ? " //
					+ "                )  " //
					+ "            group by userid " //
					+ "            ) andere " //
					+ "        ) nenner " //
					+ "    where zaehler.userid = nenner.userid AND nenner.prodRating <> 0 " //
					+ ")  " //
					+ "where aehnlichkeit > ?");
			aehnlicheUser.setInt(1, userID);
			aehnlicheUser.setInt(2, userID);
			aehnlicheUser.setInt(3, userID);
			aehnlicheUser.setInt(4, userID);
			aehnlicheUser.setDouble(5, mindestAehnlichkeit);

			ResultSet result = aehnlicheUser.executeQuery();
			while (result.next()) {
				user.add(result.getInt(1));
			}

			return user;
		});
	}

	public static List<Event> getRecommendationFilterStrong(int userid, int minRating) throws SQLException {
		return runWithConnection(conn -> {

			List<Event> events = new ArrayList<>();
			PreparedStatement statement = conn
					.prepareStatement("select distinct title, typename from events where title in" //
							+ "	(SELECT distinct e.title" //
							+ "	from RATINGS r, EVENTS e," //
							+ "		(select c.userid userlist" //
							+ "		from customer c, " //
							+ "			(SELECT GENDER_SHORT gender, birthday_year birthday" //
							+ "			FROM CUSTOMER" //
							+ "			WHERE userid = ?) info" //
							+ "		where c.userid <> ?" //
							+ "		AND c.GENDER_SHORT = info.gender" //
							+ "		AND c.birthday_year < (info.birthday + 5)" //
							+ "		AND c.birthday_year > (info.birthday - 5)" //
							+ "		) ageGender" //
							+ "	where r.userid in ageGender.userlist" //
							+ "	AND r.eventid = e.eventid" //
							+ "	AND r.RATING >= ?" //
							+ "	and e.genre in " //
							+ "		(select distinct genre" //
							+ "		from CUSTOMER, EVENTS " //
							+ "		where userid = ?" //
							+ "		AND(" //
							+ "			preference1 = genre" //
							+ "			OR preference2 = genre" //
							+ "			OR preference3 = genre" //
							+ ")))" //
			);
			statement.setInt(1, userid);
			statement.setInt(2, userid);
			statement.setInt(3, minRating);
			statement.setInt(4, userid);

			ResultSet result = statement.executeQuery();
			while (result.next()) {
				events.add(createLightEventFromResult(result));
			}

			return events;
		});
	}

	public static List<Event> getRecommendationFilterMedium(int userid, int minRating) throws SQLException {
		return runWithConnection(conn -> {

			List<Event> events = new ArrayList<>();
			PreparedStatement statement = conn.prepareStatement("select distinct e.title, e.typename" //
					+ " from RATINGS r, EVENTS e," //
					+ "	(select c.userid userlist" //
					+ "	from customer c," //
					+ "	(select birthday_year birthday" //
					+ "	from CUSTOMER" //
					+ "	where userid = ?) info" //
					+ "	where c.userid <> ?" //
					+ "	AND c.birthday_year < (info.birthday + 5)" //
					+ "	AND c.birthday_year > (info.birthday - 5)" //
					+ "	) age" //
					+ " where r.userid in age.userlist" //
					+ " AND r.eventid = e.eventid" //
					+ " AND r.RATING >= ?" //
					+ " and e.genre in " //
					+ "		(select distinct genre" //
					+ "		from CUSTOMER, EVENTS " //
					+ "		where userid = ?" //
					+ "		AND(" //
					+ "			preference1 = genre" //
					+ "			OR preference2 = genre" //
					+ "			OR preference3 = genre" //
					+ "			)" //
					+ "		)" //
			);
			statement.setInt(1, userid);
			statement.setInt(2, userid);
			statement.setInt(3, minRating);
			statement.setInt(4, userid);

			ResultSet result = statement.executeQuery();
			while (result.next()) {
				events.add(createLightEventFromResult(result));
			}

			return events;
		});
	}

	public static List<Event> getRecommendationFilterLight(int userid, int minRating) throws SQLException {
		return runWithConnection(conn -> {

			List<Event> events = new ArrayList<>();
			PreparedStatement statement = conn.prepareStatement("select distinct e.title, e.typename" //
					+ " from RATINGS r, EVENTS e," //
					+ "	(select c.userid userlist" //
					+ "	from customer c," //
					+ "	(select birthday_year birthday" //
					+ "	from CUSTOMER" //
					+ "	where userid = ?) info" //
					+ "	where c.userid <> ?" //
					+ "	AND c.birthday_year < (info.birthday + 5)" //
					+ "	AND c.birthday_year > (info.birthday - 5)" //
					+ "	) age" //
					+ " where r.userid in age.userlist" //
					+ " AND r.eventid = e.eventid" //
					+ " AND r.RATING >= ?" //
			);
			statement.setInt(1, userid);
			statement.setInt(2, userid);
			statement.setInt(3, minRating);

			ResultSet result = statement.executeQuery();
			while (result.next()) {
				events.add(createLightEventFromResult(result));
			}

			return events;
		});
	}

	// #####################################################################################
	// ----- Statistiken -----
	// #####################################################################################

	public static String getAnalysis(String name) throws SQLException {
		return runWithConnection(conn -> {

			PreparedStatement statement = conn.prepareStatement("SELECT sqlquery FROM ANALYSIS_QUERIES where name = ?");
			statement.setString(1, name);
			ResultSet result = statement.executeQuery();

			result.next();

			PreparedStatement stmtStatistik = conn.prepareStatement(result.getString(1));
			ResultSet restulStatistik = stmtStatistik.executeQuery();

			restulStatistik.next();
			return restulStatistik.getString(1);
		});
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

	public static List<String> getAnalysisNames() throws SQLException {
		return runWithConnection(conn -> {

			List<String> names = new ArrayList<>();
			PreparedStatement statement = conn.prepareStatement("SELECT Name FROM ANALYSIS_QUERIES");

			ResultSet result = statement.executeQuery();
			while (result.next()) {
				names.add(result.getString(1));
			}

			return names;
		});
	}

	/**
	 * Führt die gegebene Funktion aus und übergibt dieser eine Verbindung zur
	 * Datenbank
	 * 
	 * @param function Funktion, die mit einer Datenbankverbindung ausgeführt werden
	 *                 soll
	 * @param <R>      Rückgabedatentyp der gegebenen Funktion
	 * @return Rückgabewert der gegebenen Function
	 * @throws SQLException Fehler bei der Ausführung
	 */
	private static <R> R runWithConnection(SqlFunction<Connection, R> function) throws SQLException {
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
	 * @param query Funktion, die mit einer Connection (mit ggf. Rollback bei
	 *              Fehlern) ausgeführt werden soll
	 * @param <R>   Rückgabedatentyp der gegebenen Funktion
	 * @return Rückgabewert der gegebenen Funktion
	 * @throws SQLException Fehler beim Verbinden zu der Datenbank
	 */
	private static <R> R runWithConnectionRollback(SqlFunction<Connection, R> query) throws SQLException {
		Connection conn = getConnection();
		R functionReturn = null;
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

}
