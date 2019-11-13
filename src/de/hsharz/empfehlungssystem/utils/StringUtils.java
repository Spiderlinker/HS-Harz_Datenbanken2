package de.hsharz.empfehlungssystem.utils;

public class StringUtils {

	private StringUtils() {
		// Utils Klasse
	}

	/**
	 * Gibt an, ob der gegebene String null oder leer ist
	 * 
	 * @param s String der geprüft werden soll
	 * @return true, falls der gegebene String null oder leer ist; andernfalls false
	 */
	public static boolean isNullOrEmpty(String s) {
		return s == null || s.trim().isEmpty();
	}

}
