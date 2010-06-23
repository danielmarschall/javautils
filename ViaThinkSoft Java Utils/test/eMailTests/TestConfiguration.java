package eMailTests;

// Statische Klasse, die unsere Konfiguration f�r Tests verwaltet.
// Ginge auch als Singleton

public final class TestConfiguration {
	
	// Gott habe Mitleid mit dem Eigent�mer dieser E-Mail-Adresse...
	// Eine Wegwerfadresse f�r manuelle Tests kann auf
	// www.10minutemail.com erstellt werden.
	private static final String SPAMMING_MAIL_ADDRESS = "a1175972@bofthew.com";
	
	public static String getSpamAddress() {
		return SPAMMING_MAIL_ADDRESS;
	}
	
	private TestConfiguration() {
	}
}
