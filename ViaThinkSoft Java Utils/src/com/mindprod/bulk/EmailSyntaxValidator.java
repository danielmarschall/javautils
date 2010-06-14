/*
 * @(#)EmailSyntaxValidator.java
 *
 * Summary: Validate syntax of email addresses.
 *
 * Copyright: (c) 2002-2010 Roedy Green, Canadian Mind Products, http://mindprod.com
 *
 * Licence: This software may be copied and used freely for any purpose but military.
 *          http://mindprod.com/contact/nonmil.html
 *
 * Requires: JDK 1.5+
 *
 * Created with: IntelliJ IDEA IDE.
 *
 * Version History:
 *  1.7 2007-08-21
 */
package com.mindprod.bulk;

// Download newest version here:
// http://mindprod.com/products1.html#BULK
// SVN:
// http://wush.net/svn/mindprod/com/mindprod/bulk/EmailSyntaxValidator.java

// TODO: E-Mail-Aufbereiter... Puny, Trim
// TODO: BAD TLDS + PSEUDO (TOR: EXIT ETC)
// TODO: Awaiting bulk comit

// CHANGELOG BY DANIEL MARSCHALL
//
//Added ccTLDs
//
//.ax = Aland Islands
//.eu = European Union
//.me = Montenegro
//.rs = Serbia
//.su = Soviet Union (being phased out)
//.tl = Timor-Leste
//
//Deleted ccTLDs
//
//.bv = Bouvet Island [Allocated/unused]
//.eh = Western Sahara [Reserved/unassigned]
//.fx = UNKNOWN
//.gb = United Kingdom [Allocated/unused]
//.pm = Saint Pierre and Miquelon [Allocated/unused]
//.sj = Svalbard and Jan Mayen [Allocated/unused]
//.so = Somalia [Allocated/unused]
//.um = United States Minor Outlying Islands [Reserved/unassigned]
//.yt = Mayotte [Allocated/unused]
//.yu = Yugoslavia [Deleted/retired]
//
//Added BAD TLDs
//
//.example (RFC 2606)
//.localhost (RFC 2606)
//.test (RFC 2606)
//
//Added official TLDs
//
//.arpa (infrastructure TLD)
//.tel (sponsored TLD)               -- official TLD or rare TLD?
//.mobi (sponsored TLD)              -- official TLD or rare TLD?
//.jobs (sponsored TLD)              -- official TLD or rare TLD?
//.cat (sponsored TLD)               -- official TLD or rare TLD?
//
//Other changes
//
//* Commented out unused debugging stuff
//* Removed main procedure and syso import

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Validate syntax of email addresses.
 * <p/>
 * Does not probe to see if mailserver exists in DNS or online. See MailProber
 * for that. See ValidateEmailFile for an example of how to use this class.
 * 
 * @author Roedy Green, Canadian Mind Products
 * @version 1.7 2007-08-21
 * @since 2002
 */
// TODO: @version check validity of & in first part of email address. Appears in
// practice.

public final class EmailSyntaxValidator {
	// ------------------------------ CONSTANTS ------------------------------

	/**
	 * True if want extra debugging output.
	 */
	// @SuppressWarnings( { "UnusedDeclaration" })
	// private static final boolean DEBUGGING = false;

	/**
	 * Country where this program is running.
	 */
	private static final String THIS_COUNTRY = Locale.getDefault().getCountry()
			.toLowerCase();

	/**
	 * Bad top level domains -- ones never valid.
	 */
	private static final HashSet<String> BAD_TLDS = hmaker(new String[] {
			"invalid", "nowhere", "noone", "test", "example", "localhost", });

	/**
	 * Top level domains for countries.
	 */
	private static final HashSet<String> NATIONAL_TLDS = hmaker(new String[] {
			"ac", "ad", "ae", "af", "ag", "ai", "al", "am", "an", "ao", "aq",
			"ar", "as", "at", "au", "aw", "ax", "az", "ba", "bb", "bd", "be",
			"bf", "bg", "bh", "bi", "bj", "bm", "bn", "bo", "br", "bs", "bt",
			"bw", "by", "bz", "ca", "cc", "cd", "cf", "cg", "ch", "ci", "ck",
			"cl", "cm", "cn", "co", "cr", "cu", "cv", "cx", "cy", "cz", "de",
			"dj", "dk", "dm", "do", "dz", "ec", "ee", "eg", "er", "es", "et",
			"eu", "eu", "fi", "fj", "fk", "fm", "fo", "fr", "ga", "gd", "ge",
			"gf", "gg", "gh", "gi", "gl", "gm", "gn", "gp", "gq", "gr", "gs",
			"gt", "gu", "gw", "gy", "hk", "hm", "hn", "hr", "ht", "hu", "id",
			"ie", "il", "im", "in", "io", "iq", "ir", "is", "it", "je", "jm",
			"jo", "jp", "ke", "kg", "kh", "ki", "km", "kn", "kp", "kr", "kw",
			"ky", "kz", "la", "lb", "lc", "li", "lk", "lr", "ls", "lt", "lu",
			"lv", "ly", "ma", "mc", "md", "me", "mg", "mh", "mk", "ml", "mm",
			"mn", "mo", "mp", "mq", "mr", "ms", "mt", "mu", "mv", "mw", "mx",
			"my", "mz", "na", "nc", "ne", "nf", "ng", "ni", "nl", "no", "np",
			"nr", "nu", "nz", "om", "pa", "pe", "pf", "pg", "ph", "pk", "pl",
			"pn", "pr", "ps", "pt", "pw", "py", "qa", "re", "ro", "rs", "ru",
			"rw", "sa", "sb", "sc", "sd", "se", "sg", "sh", "si", "sk", "sl",
			"sm", "sn", "sr", "st", "su", "sv", "sy", "sz", "tc", "td", "tf",
			"tg", "th", "tj", "tk", "tl", "tm", "tn", "to", "tp", "tr", "tt",
			"tv", "tw", "tz", "ua", "ug", "uk", "us", "uy", "uz", "va", "vc",
			"ve", "vg", "vi", "vn", "vu", "wf", "ws", "ye", "za", "zm", "zw", });

	/**
	 * Official top level domains.
	 */
	private static final HashSet<String> OFFICIAL_TLDS = hmaker(new String[] {
			"aero", "biz", "coop", "com", "edu", "gov", "info", "mil",
			"museum", "name", "net", "org", "pro", "tel", "mobi", "jobs",
			"cat", "arpa", });

	/**
	 * Rarely used top level domains
	 */
	private static final HashSet<String> RARE_TLDS = hmaker(new String[] {
			"cam", "mp3", "agent", "art", "arts", "asia", "auction", "aus",
			"bank", "cam", "chat", "church", "club", "corp", "dds", "design",
			"dns2go", "e", "email", "exp", "fam", "family", "faq", "fed",
			"film", "firm", "free", "fun", "g", "game", "games", "gay", "ger",
			"globe", "gmbh", "golf", "gov", "help", "hola", "i", "inc", "int",
			"jpn", "k12", "kids", "law", "learn", "llb", "llc", "llp", "lnx",
			"love", "ltd", "mag", "mail", "med", "media", "mp3", "netz", "nic",
			"nom", "npo", "per", "pol", "prices", "radio", "rsc", "school",
			"scifi", "sea", "service", "sex", "shop", "sky", "soc", "space",
			"sport", "tech", "tour", "travel", "usvi", "video", "web", "wine",
			"wir", "wired", "zine", "zoo", });

	/**
	 * regex to allow dots anywhere, but not at start of domain name, no +
	 */
	private static final Pattern p3 = Pattern
			.compile("[a-z0-9\\-_\\.]++@[a-z0-9\\-_]++(\\.[a-z0-9\\-_]++)++");

	/**
	 * regex IP style names, no +
	 */
	private static final Pattern p4 = Pattern
			.compile("[a-z0-9\\-_]++(\\.[a-z0-9\\-_]++)*@\\[([0-9]{1,3}\\.){3}[0-9]{1,3}\\]");

	/**
	 * regex to allow - _ dots in name, no +
	 */
	private static final Pattern p5 = Pattern
			.compile("[a-z0-9\\-_]++(\\.[a-z0-9\\-_]++)*@[a-z0-9\\-_]++(\\.[a-z0-9\\-_]++)++");

	/**
	 * regex to allow _ - in name, lead and trailing ones are filtered later, no
	 * +.
	 */
	private static final Pattern p9 = Pattern
			.compile("[a-z0-9\\-_]++@[a-z0-9\\-_]++(\\.[a-z0-9\\-_]++)++");

	/**
	 * regex to split into fields
	 */
	private static final Pattern splitter = Pattern.compile("[@\\.]");

	// -------------------------- PUBLIC STATIC METHODS
	// --------------------------

	/**
	 * Check how likely an email address is to be valid. The higher the number
	 * returned, the more likely the address is valid. This method does not
	 * probe the internet in any way to see if the corresponding mail server or
	 * domain exists.
	 * 
	 * @param email
	 *            bare computer email address. e.g. roedyg@mindprod.com No
	 *            "Roedy Green" <roedyg@mindprod.com> style addresses. No local
	 *            addresses, e.g. roedy.
	 * 
	 * @return <ul>
	 *         <li>0 = email address is definitely malformed, e.g. missing
	 * @. ends in .invalid</li> <li>1 = address does not meet one of the valid
	 *    patterns below. It still might be ok according to some obscure rule in
	 *    RFC 822 Java InternetAddress accepts it as valid.</li> <li>2 = unknown
	 *    top level domain.</li> <li>3 = dots at beginning or end, doubled in
	 *    name.</li> <li>4 = address of form xxx@[209.139.205.2] using IP</li>
	 *    <li>5 = address of form xxx.xxx.xxx@xxx.xxx.xxx Dots _ or - in first
	 *    part of name</li> <li>6 = addreess of form xxx@xxx.xxx.xxx rare, but
	 *    known, domain</li> <li>7 = address of form xxx@xxx.xxx.ca or any
	 *    national suffix.</li> <li>8 = address of form xxx@xxx.xxx.xx the
	 *    matching this national suffix, e.g. .ca in Canada, .de in Germany</li>
	 *    <li>9 = address of form xxx@xxx.xxx.com .org .net .edu .gov .biz --
	 *    official domains</li>
	 *    </ul>
	 */
	public static int howValid(String email) {
		if (email == null) {
			return 0;
		}
		email = email.trim().toLowerCase();
		int dotPlace = email.lastIndexOf('.');
		if (0 < dotPlace && dotPlace < email.length() - 1) {
			String tld = email.substring(dotPlace + 1);
			if (BAD_TLDS.contains(tld)) {
				/* deliberate invalid address */
				return 0;
			}
			// make sure none of fragments start or end in _ or -
			String[] fragments = splitter.split(email);
			boolean clean = true;
			for (String fragment : fragments) {
				if (fragment.startsWith("_") || fragment.endsWith("_")
						|| fragment.startsWith("-") || fragment.endsWith("-")) {
					clean = false;
					break;
				}
			}// end for
			if (clean) {
				Matcher m9 = p9.matcher(email);
				if (m9.matches()) {
					if (OFFICIAL_TLDS.contains(tld)) {
						return 9;
					} else if (THIS_COUNTRY.equals(tld)) {
						return 8;
					} else if (NATIONAL_TLDS.contains(tld)) {
						return 7;
					} else if (RARE_TLDS.contains(tld)) {
						return 6;
					} else {
						// TODO: Why is that 3 and not 2?
						return 3;/* unknown tld */
					}
				}
				// allow dots in name
				Matcher m5 = p5.matcher(email);
				if (m5.matches()) {
					if (OFFICIAL_TLDS.contains(tld)) {
						return 5;
					} else if (THIS_COUNTRY.equals(tld)) {
						return 5;
					} else if (NATIONAL_TLDS.contains(tld)) {
						return 5;
					} else if (RARE_TLDS.contains(tld)) {
						return 5;
					} else {
						return 2;/* unknown tld */
					}
				}

				// IP
				Matcher m4 = p4.matcher(email);
				if (m4.matches()) {
					return 4;/* can't tell TLD */
				}

				// allow even lead/trail dots in name, except at start of domain
				Matcher m3 = p3.matcher(email);
				if (m3.matches()) {
					if (OFFICIAL_TLDS.contains(tld)) {
						return 3;
					} else if (THIS_COUNTRY.equals(tld)) {
						return 3;
					} else if (NATIONAL_TLDS.contains(tld)) {
						return 3;
					} else if (RARE_TLDS.contains(tld)) {
						return 3;
					} else {
						return 2;/* unknown domain */
					}
				}
			}// end if clean
		}
		// allow even unclean addresses, and addresses without a TLD to have a
		// whack at passing RFC:822
		try {
			/*
			 * see if InternetAddress likes it, it follows RFC:822. It will
			 * names without domains though.
			 */
			InternetAddress.parse(email, true/* strict */);
			// it liked it, no exception happened. Seems very sloppy.
			return 1;
		} catch (AddressException e) {
			// it did not like it
			return 0;
		}
	}

	// -------------------------- STATIC METHODS --------------------------

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

	// --------------------------- main() method ---------------------------

	/**
	 * main debugging harness.
	 * 
	 * @param args
	 *            not used
	 */
	// public static void main(String[] args) {
	// out.println(howValid("kellizer@.hotmail.com"));
	// }
}
