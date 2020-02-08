package servlets;

import java.sql.*;
import java.util.*;

public class JdbcQueryBean {

	private Connection connection;
	private Statement statement;

	private String result = null;

	public int userid = 0;

	/*
	 * Hier Bitte die eigenen Benutzerdaten eintragen und dafür sorgen, dass die
	 * Tabellen in die Eingetragen wird existieren
	 */
	private static final String driver = "oracle.jdbc.driver.OracleDriver";
	private static final String dbUrl = "jdbc:oracle:thin:@oracle.hs-harz.de:1521:HARZDB01";
	private static final String login = "BROCKEN_001";
	private static final String password = "BROCKEN_003";

	/*
	 * Benutzte Daten auf Datenbank sind:
	 * 
	 * table vorlage_user (userid, name, password) table vorlage_produkt
	 * (produktname) (hier sollte es auch eine ID geben) table bewertungen (userid,
	 * produkt, bewertung)
	 * 
	 * user_seq (beginnt mit 1, wird um 1 incrementiert)
	 * 
	 * Diese gilt es an das eigene System anzupassen
	 * 
	 */
	

	/** @Formatter::off */
	private String getEmpfohleneProdukteQuery = "SELECT * FROM " + 
			"    (SELECT  p.produktid, p.produktname, round(avg(b.bewertung), 2) schnitt from " + 
			"        CC_Vorlage_Bewertung b, CC_Vorlage_Produkte p " + 
			"        WHERE " + 
			"            b.produktid = p.produktid  " + 
			"            and userid in (%s) " + 
			"            and bewertung > 0 " + 
			"        group by p.produktid, p.produktname " + 
			"        order by schnitt desc)  " + 
			"     where produktid in (select produktid from CC_Vorlage_Bewertung where userid = %s and bewertung = 0 )  " + 
			" FETCH NEXT %d ROWS ONLY;";

	/** @Formatter::off */
	private String getAehnlicheQuery = "select * from "
			+ "(select zaehlertabelle.userid, round(zaehler/nenner, 2) aehnlichkeit "
			+ "from (select userid, sum(ratingprod) zaehler "
			+ "from (select andere.userid, andere.produktid, nutzer.p_rating * andere.p_rating ratingprod "
			+ "from (select * from PSratings where userid = ?) nutzer, "
			+ "(select * from PSratings where userid <> ?) andere "
			+ "where andere.produktid = nutzer.produktid) group by userid) zaehlertabelle, "
			+ "(select andere.userid, andere.vektorbetrag * nutzer.vektorbetrag nenner "
			+ "from (select * from PSvektorbetrag where userid = ?) nutzer, "
			+ "(select * from PSvektorbetrag where userid <> ?) andere "
			+ "where andere.vektorbetrag <> 0 and nutzer.vektorbetrag <> 0) nennertabelle "
			+ "where zaehlertabelle.userid = nennertabelle.userid) " + "where aehnlichkeit > ?";

	private PreparedStatement psGetAehnlicheNutzer;

	boolean logged = false;

	public JdbcQueryBean() {

		this.dbConnection();
	}

	public void dbConnection() {

		try {
			Class.forName(driver);
			connection = DriverManager.getConnection(dbUrl, login, password);
			statement = connection.createStatement();

		} catch (java.lang.ClassNotFoundException e) {
			e.printStackTrace();
			System.err.print("ClassNotFoundException: ");
			System.err.println(e.getMessage());
			connection = null;

		} catch (SQLException ex) {
			ex.printStackTrace();
			System.err.print("SQLException: ");
			System.err.println(ex.getMessage());
			connection = null;
		} catch (Exception ne) {
			ne.printStackTrace();
			System.err.print("Other Error while connecting to the database : ");
			System.err.println(ne.getMessage());
			connection = null;
		}
	}

	private void initPreparedStatement() {
		try {
			psGetAehnlicheNutzer = connection.prepareStatement(this.getAehnlicheQuery);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ResultSet executePSGetAehnliche(String user, double mindestAehnlichkeit) {
		initPreparedStatement();

		ResultSet result = null;

		int userid = Integer.parseInt(user);
		try {
			psGetAehnlicheNutzer.setInt(1, userid);
			psGetAehnlicheNutzer.setInt(2, userid);
			psGetAehnlicheNutzer.setDouble(3, mindestAehnlichkeit);

			result = psGetAehnlicheNutzer.executeQuery();
		} catch (SQLException e) {
			e.printStackTrace();
			result = null;
		}

		return result;
	}

	public ResultSet executePSGetAehnlicheProdukte(String userid, String inUsers, int ranking) {

		ResultSet result = null;

		int useridInt = Integer.parseInt(userid);
		try {
			String formattedSql = String.format(getEmpfohleneProdukteQuery, inUsers, useridInt, ranking);
			result = connection.createStatement().executeQuery(formattedSql);
		} catch (SQLException e) {
			e.printStackTrace();
			result = null;
		}

		return result;
	}

	public int executePSGetCountBewertungen(int user) {
		int count = -1;

		ResultSet result = null;

		try {
			PreparedStatement stmt = connection
					.prepareStatement("SELECT COUNT(*) FROM CC_VORLAGE_BEWERTUNG WHERE userid = ? AND bewertung = 0");
			stmt.setInt(1, userid);

			result = stmt.executeQuery();
			if (result.next()) {
				count = result.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			result = null;
		}

		return count;
	}

	public String getResult() {

		return result;

	}

	protected void finalize() {
		try {
			connection.close();
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public ResultSet executeQuery(String query) {
		ResultSet rs = null;
		try {
			rs = statement.executeQuery(query);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e);
			return null;
		}
		return rs;
	}

	public void register(String[] data) {

		/*
		 * Fuer bessere Sicherheit Passwort Hashen bevor in Datenbank geschrieben wird
		 */

		result = null;

		/*
		 * Durch SQL eine Sequenz user_seq erstellen oder UserId durch eine Abfrage
		 * ermitteln
		 */

		String query = "insert into CC_Vorlage_User values(user_seq.nextval,'" + data[0] + "','" + data[1] + "')";

		try {
			statement.executeQuery(query);
			StringBuffer sb = new StringBuffer();

		} catch (SQLException e) {
			e.printStackTrace();
			result = "<P> SQL error: <PRE> " + e + " </PRE> </P>\n";
			System.out.println(e);
		} catch (Exception ignored) {
			ignored.printStackTrace();
			result = "<P> Error: <PRE> " + ignored + " </PRE> </P>\n";
			System.out.println(ignored);
		}

	}

	public String getlogin() {

		if (logged) {
			return "<center><p>Login erfolgreich</p><br><a href=\"index.jsp\">weiter</a></center>";
		} else {
			return "<center><p>Login Fehlgeschlagen</p><br><a href=\"index.jsp\">weiter</a></center>";
		}

	}

	public void login(String[] loginData, LoginBean loginBean) {

		String query = "select password from CC_Vorlage_User where username = '" + loginData[0] + "'";

		try {

			ResultSet rs = statement.executeQuery(query);
			StringBuffer sb = new StringBuffer();
			String dbPass = new String();

			while (rs.next()) {
				dbPass = rs.getString(1);
			}

			if (dbPass.equals(loginData[1])) {
				logged = true;
				loginBean.loginFailed = false;
				loginBean.logged = true;

				rs = statement.executeQuery("select userid from CC_vorlage_user where username='" + loginData[0] + "'");
				while (rs.next()) {
					userid = Integer.parseInt(rs.getString(1));
				}

			} else {
				logged = false;
				loginBean.loginFailed = true;

			}
		} catch (SQLException e) {
			e.printStackTrace();
			result = "<P> SQL error: <PRE> " + e + " </PRE> </P>\n";
			System.out.println(e);
		} catch (Exception ignored) {
			ignored.printStackTrace();
			result = "<P> Error: <PRE> " + ignored + " </PRE> </P>\n";
			System.out.println(ignored);
		}

	}

}
