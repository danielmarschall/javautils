package de.viathinksoft.utils.mail.sender;

// Statische Klasse, die unsere Konfiguration f�r Tests verwaltet.
// Ginge auch als Singleton

public final class TestConfiguration {
	public static String getSpamAddress() {
		// Gott habe Mitleid mit dem Eigent�mer dieser E-Mail-Adresse...
		return "b973768@owlpic.com";

		// Eine Wegwerfadresse f�r manuelle Tests kann auf
		// www.10minutemail.com erstellt werden.
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
