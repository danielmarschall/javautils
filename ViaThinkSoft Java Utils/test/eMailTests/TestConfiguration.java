package eMailTests;

// Statische Klasse, die unsere Konfiguration für Tests verwaltet.
// Ginge auch als Singleton

public final class TestConfiguration {
	// Gott habe Mitleid mit dem Eigentümer dieser E-Mail-Adresse...
	// Eine Wegwerfadresse für manuelle Tests kann auf
	// www.10minutemail.com erstellt werden.
	private static final String SPAMMING_MAIL_ADDRESS = "a1175972@bofthew.com";

	public static String getSpamAddress() {
		return SPAMMING_MAIL_ADDRESS;
	}
	
	public static String getSmtpHost() {
		return "";
	}
	
	public static String getSmtpUsername() {
		return "";
	}
	
	public static String getSmtpPassword() {
		return "";
	}
	
	public static int getSmtpPort() {
		return 25;
	}
	
	public static String getMailFrom() {
		return getSpamAddress();
	}
	
	private TestConfiguration() {
	}
}
