package de.viathinksoft.utils.mail.syntaxchecker;

import java.util.Arrays;
import java.util.HashSet;
import java.util.regex.Pattern;

import de.viathinksoft.utils.mail.EMailAddress;

/**
 * This class is not stable. For a good syntax check, please use the classes of
 * Dominic Sayers or Cal Henderson.
 * 
 * @author Daniel Marschall
 * @version 0.1
 * 
 */
public class MailSyntaxChecker {

	private static final String REGEX_IP = "\\b(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\b";

	// Führt eine Prüfung der E-Mail-Adresse gemäß SMTP-Spezifikation RFC 5321
	// aus
	private static final boolean CHECK_SMTP_SIZE_LIMITS = false;

	// Führt eine Prüfung der TLD gemäß IANA-Daten aus
	private static final boolean CHECK_TLD_RECOGNIZED = true;

	// Führt eine DNS-Prüfung durch
	private static final boolean CHECK_DNS = true;

	// http://data.iana.org/TLD/tlds-alpha-by-domain.txt
	// Version 2010052500, Last Updated Tue May 25 14:07:02 2010 UTC
	private static final HashSet<String> RECOGNIZED_TLDS_PUNYCODE = hmaker(new String[] {
			"AC", "AD", "AE", "AERO", "AF", "AG", "AI", "AL", "AM", "AN", "AO",
			"AQ", "AR", "ARPA", "AS", "ASIA", "AT", "AU", "AW", "AX", "AZ",
			"BA", "BB", "BD", "BE", "BF", "BG", "BH", "BI", "BIZ", "BJ", "BM",
			"BN", "BO", "BR", "BS", "BT", "BV", "BW", "BY", "BZ", "CA", "CAT",
			"CC", "CD", "CF", "CG", "CH", "CI", "CK", "CL", "CM", "CN", "CO",
			"COM", "COOP", "CR", "CU", "CV", "CX", "CY", "CZ", "DE", "DJ",
			"DK", "DM", "DO", "DZ", "EC", "EDU", "EE", "EG", "ER", "ES", "ET",
			"EU", "FI", "FJ", "FK", "FM", "FO", "FR", "GA", "GB", "GD", "GE",
			"GF", "GG", "GH", "GI", "GL", "GM", "GN", "GOV", "GP", "GQ", "GR",
			"GS", "GT", "GU", "GW", "GY", "HK", "HM", "HN", "HR", "HT", "HU",
			"ID", "IE", "IL", "IM", "IN", "INFO", "INT", "IO", "IQ", "IR",
			"IS", "IT", "JE", "JM", "JO", "JOBS", "JP", "KE", "KG", "KH", "KI",
			"KM", "KN", "KP", "KR", "KW", "KY", "KZ", "LA", "LB", "LC", "LI",
			"LK", "LR", "LS", "LT", "LU", "LV", "LY", "MA", "MC", "MD", "ME",
			"MG", "MH", "MIL", "MK", "ML", "MM", "MN", "MO", "MOBI", "MP",
			"MQ", "MR", "MS", "MT", "MU", "MUSEUM", "MV", "MW", "MX", "MY",
			"MZ", "NA", "NAME", "NC", "NE", "NET", "NF", "NG", "NI", "NL",
			"NO", "NP", "NR", "NU", "NZ", "OM", "ORG", "PA", "PE", "PF", "PG",
			"PH", "PK", "PL", "PM", "PN", "PR", "PRO", "PS", "PT", "PW", "PY",
			"QA", "RE", "RO", "RS", "RU", "RW", "SA", "SB", "SC", "SD", "SE",
			"SG", "SH", "SI", "SJ", "SK", "SL", "SM", "SN", "SO", "SR", "ST",
			"SU", "SV", "SY", "SZ", "TC", "TD", "TEL", "TF", "TG", "TH", "TJ",
			"TK", "TL", "TM", "TN", "TO", "TP", "TR", "TRAVEL", "TT", "TV",
			"TW", "TZ", "UA", "UG", "UK", "US", "UY", "UZ", "VA", "VC", "VE",
			"VG", "VI", "VN", "VU", "WF", "WS", "XN--0ZWM56D",
			"XN--11B5BS3A9AJ6G", "XN--80AKHBYKNJ4F", "XN--9T4B11YI5A",
			"XN--DEBA0AD", "XN--G6W251D", "XN--HGBK6AJ7F53BBA",
			"XN--HLCJ6AYA9ESC7A", "XN--JXALPDLP", "XN--KGBECHTV",
			"XN--MGBAAM7A8H", "XN--MGBERP4A5D4AR", "XN--P1AI", "XN--WGBH1C",
			"XN--ZCKZAH", "YE", "YT", "ZA", "ZM", "ZW", });

	private static boolean checkSmtpSizeLimits(EMailAddress email) {
		// RFC 5321: 4.5.3.1.1. Local-part Längenbegrenzung bei SMTP: 64
		// Byte
		// QUE: Soll das auch als Punicode-Variante geprüft werden?
		if ((email.getLocalPart().length() > 64)
				|| (email.getLocalPart().length() < 1)) {
			return false;
		}

		// RFC 5321: 4.5.3.1.2. Domain-part Längenbegrenzung bei SMTP: 255
		// Byte
		if ((email.getDomainPartPunycode().length() > 255)
				|| (email.getDomainPartPunycode().length() < 1)) {
			return false;
		}

		// RFC 5321: 4.5.3.1.5. Reply-Line Längenbegrenzung bei SMTP: 512
		// Byte. Laut
		// http://de.wikipedia.org/wiki/E-Mail-Adresse#L.C3.A4nge_der_E-Mail-Adresse
		// folgt daraus: Länge der MailAddresse ist 254 Bytes.
		if (email.getMailAddressPunycodedDomain().length() > 254) {
			return false;
		}

		return true;
	}

	private static boolean checkTldRecognized(EMailAddress email) {
		// TODO: Mailadressen sind aber auch als ...@[IP] gültig. Dann keine
		// TLD!
		return RECOGNIZED_TLDS_PUNYCODE.contains(email.getTldPunycode()
				.toUpperCase());
	}

	private static boolean preg_match(String regex, String data) {
		return Pattern.compile(regex).matcher(data).matches();
	}

	private static boolean checkDns(String domainOrIP) {
		// TODO

		return true;
	}

	public static boolean isMailValid(String email) {
		return isMailValid(new EMailAddress(email));
	}

	/**
	 * Checks if an E-Mail-Address is valid
	 * 
	 * @param email
	 * @return
	 */
	public static boolean isMailValid(EMailAddress email) {
		if (CHECK_SMTP_SIZE_LIMITS) {
			if (!checkSmtpSizeLimits(email))
				return false;
		}

		// Begin RFC-Checks

		final String address = email.getMailAddressUnicode();
		final String localPart = email.getLocalPart();
		final String domainPart = email.getDomainPartPunycode();

		// Weder localPart noch domainPart dürfen zwei aufeinanderfolgende
		// Punkte besitzen.

		if (address.contains("..")) {
			return false;
		}

		// localPart darf keine Punkte am Anfang oder Ende besitzen
		
		if (localPart.length() == 0) {
			return false;
		}
		if (localPart.startsWith(".") || localPart.endsWith(".")) {
			return false;
		}

		// domainPart darf keine Punkte am Anfang oder Ende besitzen

		if (domainPart.startsWith(".") || domainPart.endsWith(".")) {
			return false;
		}

		// domainPart prüfen

		if (preg_match("^" + REGEX_IP + "$", domainPart)) {
			// domainPart is <IP>
			// QUE: Ist das überhaupt gemäß RFC gültig?

			String ip = ""; // TODO

			if (CHECK_DNS) {
				if (!checkDns(ip))
					return false;
			}
		} else if (preg_match("^\\[" + REGEX_IP + "\\]$", domainPart)) {
			// domainPart is [<IP>]

			String ip = ""; // TODO

			if (CHECK_DNS) {
				if (!checkDns(ip))
					return false;
			}
		} else {
			if (!preg_match("^[A-Za-z0-9\\-\\.]+$", domainPart)) {
				return false;
			}

			if (CHECK_TLD_RECOGNIZED) {
				if (!checkTldRecognized(email))
					return false;
			}

			if (CHECK_DNS) {
				if (!checkDns(domainPart))
					return false;
			}
		}

		// localPart prüfen

		if (!preg_match("^(\\\\.|[A-Za-z0-9!#%&`_=\\/$\'*+?^{}|~.-])+$",
				localPart.replaceAll("\\\\", "").replaceAll("@", ""))) {
			// character not valid in local part unless
			// local part is quoted
			if (!preg_match("^\"(\\\\\"|[^\"])+\"$", localPart.replaceAll(
					"\\\\", "").replaceAll("@", ""))) {
				return false;
			}
		}

		// TODO: Weitere Tests gemäß RFC?

		return true;
	}

	/**
	 * build a HashSet from a array of String literals.
	 * 
	 * @param list
	 *            array of strings
	 * 
	 * @return HashSet you can use to test if a string is in the set.
	 */
	private static HashSet<String> hmaker(String[] list) {
		HashSet<String> map = new HashSet<String>(Math.max(
				(int) (list.length / .75f) + 1, 16));
		map.addAll(Arrays.asList(list));
		return map;
	}
}
