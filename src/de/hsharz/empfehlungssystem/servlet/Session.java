package de.hsharz.empfehlungssystem.servlet;

import java.util.HashMap;
import java.util.Map;

import de.hsharz.empfehlungssystem.beans.User;

public class Session {

	public static final String ATTRIBUTE_LOGGED_IN_USER = "loggedInUser";

	private static Map<String, Object> attributes = new HashMap<>();

	private Session() {
		// Utils-Klasse
	}

	/**
	 * Speichert den gegebene User als aktuell angemeldeten Benutzer unter dem Key
	 * {@link #ATTRIBUTE_LOGGED_IN_USER}
	 * 
	 * @param loggedInUser Benutzer des aktuell angemeldeten Benutzers
	 */
	public static void storeLoggedInUser(User loggedInUser) {
		attributes.put(ATTRIBUTE_LOGGED_IN_USER, loggedInUser);
	}

	/**
	 * Liefert die ID des aktuell angemeldeten Benutzers (unter dem Key
	 * {@link #ATTRIBUTE_LOGGED_IN_USER})
	 * 
	 * @return UserObjekt des aktuell angemeldeten Benutzers; null falls diese nicht
	 *         vorhanden ist
	 */
	public static User getLoggedInUser() {
		return (User) attributes.get(ATTRIBUTE_LOGGED_IN_USER);
	}

	/**
	 * Entfernt den aktuell gespeicherten Benutzer aus der Attribute-Map
	 */
	public static void removeLoggedInUser() {
		attributes.remove(ATTRIBUTE_LOGGED_IN_USER);
	}

}
