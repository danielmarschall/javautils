package com.dominicsayers.isemail;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * IMPORTANT NOTE! These functions were developed during the translation process
 * of the E-Mail-Address verification class for Dominic Sayers. These functions
 * are NEITHER AN IDENTICAL NOR A OFFICIAL equivalence of PHP's functions. The
 * functionality is only as much as needed by my initial purpose. Special cases
 * are usually not implemented. Please also note that you have to use the JAVA
 * REGULAR EXPRESSION syntax! PHP's PCRE IS NOT INTERPRETED OR CONVERTED!
 * @author Daniel Marschall
 * @version 2010-06-14
 */

public class PHPFunctions {

	public static int preg_match(String regex, String input) {
		Matcher m = Pattern.compile(regex).matcher(input);

		int c = 0;
		while (m.find()) {
			return 1; // preg_match() bricht bei erster Übereinstimmung ab.
		}
		return c;
	}

	public static String[] preg_match_to_array(String regex, String input) {
		Matcher m = Pattern.compile(regex).matcher(input);

		if (m.find()) {
			String[] result = new String[m.groupCount() + 1];
			for (int i = 0; i < result.length; i++) {
				result[i] = m.group(i);
			}
			return result;
		} else {
			return new String[0];
		}
	}

	public static String[] preg_split(String regex, String input) {
		return input.split(regex, -1);
	}

	/**
	 * @returns [group#][match#]
	 */
	private static String[] appendToStringArray(String[] ary, String append) {
		if (ary == null)
			ary = new String[0];
		String[] ary2 = new String[ary.length + 1];

		for (int i = 0; i < ary.length; i++) {
			ary2[i] = ary[i];
		}
		ary2[ary.length] = append;

		return ary2;
	}

	public static String[][] preg_match_all(String regex, String input) {
		Matcher m = Pattern.compile(regex).matcher(input);

		if (m.find()) {
			int j = -1;

			String[][] result = new String[m.groupCount() + 1][];
			do {
				j++;

				for (int i = 0; i < result.length; i++) {
					result[i] = appendToStringArray(result[i], m.group(i));
				}
			} while (m.find());

			return result;
		} else {
			return new String[0][0];
		}
	}

	public static String substr(String input, int start) {
		return input.substring(start);
	}

	public static String substr(String input, int start, int length) {
		return input.substring(start, start + length);
	}

	public static String preg_replace(String pattern, String replacement,
			String subject) {
		return subject.replaceAll(pattern, replacement);
	}

	private PHPFunctions() {
	}

}
