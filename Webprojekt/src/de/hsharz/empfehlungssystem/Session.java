package de.hsharz.empfehlungssystem;

import java.util.HashMap;
import java.util.Map;

import de.hsharz.empfehlungssystem.beans.User;

public class Session {

	public static final String ATTRIBUTE_LOGGED_IN_USER = "loggedInUser";
	public static final String ATTRIBUTE_REGISTERING_USER = "registeringUser";
	public static final String ATTRIBUTE_EVENT_TO_PURCHASE = "eventToPurchase";
	public static final String ATTRIBUTE_ANALYSIS_TO_SHOW = "analysisToShow";
	public static final String ATTRIBUTE_SHOW_EVENTS_OF_TITLE = "eventsOfTitle";

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
		store(ATTRIBUTE_LOGGED_IN_USER, loggedInUser);
	}

	/**
	 * Liefert die ID des aktuell angemeldeten Benutzers (unter dem Key
	 * {@link #ATTRIBUTE_LOGGED_IN_USER})
	 * 
	 * @return UserObjekt des aktuell angemeldeten Benutzers; null falls diese nicht
	 *         vorhanden ist
	 */
	public static User getLoggedInUser() {
		return get(ATTRIBUTE_LOGGED_IN_USER);
	}

	/**
	 * Entfernt den aktuell gespeicherten Benutzer aus der Attribute-Map
	 */
	public static void removeLoggedInUser() {
		remove(ATTRIBUTE_LOGGED_IN_USER);
	}

	/**
	 * Speichert das gegebene Object unter dem gegebenen Key ab.
	 * 
	 * @param key   Key unter dem das gegebene Objekt abgespeichert werden soll
	 * @param value Objekt, das unter dem gegebenen Key abgespeichert werden soll
	 */
	public static void store(String key, Object value) {
		attributes.put(key, value);
	}

	/**
	 * 
	 * @param <T>
	 * @param key
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T get(String key) {
		return (T) attributes.get(key);
	}

	/**
	 * 
	 * @param <T>
	 * @param key
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T remove(String key) {
		return (T) attributes.remove(key);
	}
}
