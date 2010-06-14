package com.dominicsayers.isemail;

import javax.naming.NamingException;

/**
 * This class checks if email addresses are valid or not.
 * 
 * @package isemail
 * @author Dominic Sayers <dominic_sayers@hotmail.com>; Translated from PHP into
 *         Java by Daniel Marschall [www.daniel-marschall.de]
 * @copyright 2010 Dominic Sayers; Java-Translation 2010 by Daniel Marschall
 * @license http://www.opensource.org/licenses/bsd-license.php BSD License
 * @see http://www.dominicsayers.com/isemail
 * @version 1.17 - Upper length limit corrected to 254 characters;
 *          Java-Translation 2010-06-14
 */

/*
 * Copyright (c) 2008-2010, Dominic Sayers All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials provided
 * with the distribution. Neither the name of Dominic Sayers nor the names of
 * its contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

public class IsEMail {

	/**
	 * Checks the syntax of an email address without DNS check.
	 * 
	 * @param email
	 *            The email address to be checked.
	 * @return True if the email address is valid.
	 */
	public static boolean is_email(String email) {
		return (is_email_diagnosis(email, false) == EMailSyntaxDiagnosis.ISEMAIL_VALID);
	}

	/**
	 * Checks the syntax of an email address.
	 * 
	 * @param email
	 *            The email address to be checked.
	 * @param checkDNS
	 *            Whether a DNS check should be performed or not.
	 * @return True if the email address is valid.
	 */
	public static boolean is_email(String email, boolean checkDNS) {
		return (is_email_diagnosis(email, checkDNS) == EMailSyntaxDiagnosis.ISEMAIL_VALID);
	}

	/**
	 * Checks the syntax of an email address with diagnosis and without DNS
	 * check.
	 * 
	 * @param email
	 *            The email address to be checked.
	 * @return A diagnosis of the email syntax.
	 */
	public static EMailSyntaxDiagnosis is_email_diagnosis(String email) {
		return is_email_diagnosis(email, false);
	}

	/**
	 * Checks the syntax of an email address with diagnosis.
	 * 
	 * @param email
	 *            The email address to be checked.
	 * @param checkDNS
	 *            Whether a DNS check should be performed or not.
	 * @return A diagnosis of the email syntax.
	 */
	public static EMailSyntaxDiagnosis is_email_diagnosis(String email,
			boolean checkDNS) {

		if (email == null)
			email = "";

		// Check that 'email' is a valid address. Read the following RFCs to
		// understand the constraints:
		// (http://tools.ietf.org/html/rfc5322)
		// (http://tools.ietf.org/html/rfc3696)
		// (http://tools.ietf.org/html/rfc5321)
		// (http://tools.ietf.org/html/rfc4291#section-2.2)
		// (http://tools.ietf.org/html/rfc1123#section-2.1)

		// the upper limit on address lengths should normally be considered to
		// be 254
		// (http://www.rfc-editor.org/errata_search.php?rfc=3696)
		// NB My erratum has now been verified by the IETF so the correct answer
		// is 254
		//
		// The maximum total length of a reverse-path or forward-path is 256
		// characters (including the punctuation and element separators)
		// (http://tools.ietf.org/html/rfc5321#section-4.5.3.1.3)
		// NB There is a mandatory 2-character wrapper round the actual address
		int emailLength = email.length();
		// revision 1.17: Max length reduced to 254 (see above)
		if (emailLength > 254) {
			return EMailSyntaxDiagnosis.ISEMAIL_TOOLONG; // Too long
		}

		// Contemporary email addresses consist of a "local part" separated from
		// a "domain part" (a fully-qualified domain name) by an at-sign ("@").
		// (http://tools.ietf.org/html/rfc3696#section-3)
		int atIndex = email.lastIndexOf('@');

		if (atIndex == -1) {
			return EMailSyntaxDiagnosis.ISEMAIL_NOAT; // No at-sign
		}
		if (atIndex == 0) {
			return EMailSyntaxDiagnosis.ISEMAIL_NOLOCALPART; // No local part
		}
		if (atIndex == emailLength - 1) {
			// No domain part
			return EMailSyntaxDiagnosis.ISEMAIL_NODOMAIN;
			// revision 1.14: Length test bug suggested by Andrew Campbell of
			// Gloucester, MA
		}

		// Sanitize comments
		// - remove nested comments, quotes and dots in comments
		// - remove parentheses and dots from quoted strings
		int braceDepth = 0;
		boolean inQuote = false;
		boolean escapeThisChar = false;

		for (int i = 0; i < emailLength; ++i) {
			char charX = email.charAt(i);
			boolean replaceChar = false;

			if (charX == '\\') {
				escapeThisChar = !escapeThisChar; // Escape the next character?
			} else {
				switch (charX) {
				case '(':
					if (escapeThisChar) {
						replaceChar = true;
					} else {
						if (inQuote) {
							replaceChar = true;
						} else {
							if (braceDepth++ > 0) {
								replaceChar = true; // Increment brace depth
							}
						}
					}

					break;
				case ')':
					if (escapeThisChar) {
						replaceChar = true;
					} else {
						if (inQuote) {
							replaceChar = true;
						} else {
							if (--braceDepth > 0)
								replaceChar = true; // Decrement brace depth
							if (braceDepth < 0) {
								braceDepth = 0;
							}
						}
					}

					break;
				case '"':
					if (escapeThisChar) {
						replaceChar = true;
					} else {
						if (braceDepth == 0) {
							// Are we inside a quoted string?
							inQuote = !inQuote;
						} else {
							replaceChar = true;
						}
					}

					break;
				case '.': // Dots don't help us either
					if (escapeThisChar) {
						replaceChar = true;
					} else {
						if (braceDepth > 0)
							replaceChar = true;
					}

					break;
				default:
				}

				escapeThisChar = false;
				if (replaceChar) {
					// Replace the offending character with something harmless
					// revision 1.12: Line above replaced because PHPLint
					// doesn't like that syntax
					email = replaceCharAt(email, i, 'x');
				}

			}
		}

		String localPart = PHPFunctions.substr(email, 0, atIndex);
		String domain = PHPFunctions.substr(email, atIndex + 1);
		// Folding white space
		final String FWS = "(?:(?:(?:[ \\t]*(?:\\r\\n))?[ \\t]+)|(?:[ \\t]+(?:(?:\\r\\n)[ \\t]+)*))";
		// Let's check the local part for RFC compliance...
		//
		// local-part = dot-atom / quoted-string / obs-local-part
		// obs-local-part = word *("." word)
		// (http://tools.ietf.org/html/rfc5322#section-3.4.1)
		//
		// Problem: need to distinguish between "first.last" and "first"."last"
		// (i.e. one element or two). And I suck at regular expressions.

		String[] dotArray = PHPFunctions.preg_split(
				"(?m)\\.(?=(?:[^\\\"]*\\\"[^\\\"]*\\\")*(?![^\\\"]*\\\"))",
				localPart);
		int partLength = 0;

		for (String element : dotArray) {
			// Remove any leading or trailing FWS
			element = PHPFunctions.preg_replace("^" + FWS + "|" + FWS + "$",
					"", element);
			int elementLength = element.length();

			if (elementLength == 0) {
				// Can't have empty element (consecutive dots or
				// dots at the start or end)
				return EMailSyntaxDiagnosis.ISEMAIL_ZEROLENGTHELEMENT;
			}
			// revision 1.15: Speed up the test and get rid of
			// "uninitialized string offset" notices from PHP

			// We need to remove any valid comments (i.e. those at the start or
			// end of the element)
			if (element.charAt(0) == '(') {
				int indexBrace = element.indexOf(')');
				if (indexBrace != -1) {
					if (PHPFunctions.preg_match("(?<!\\\\)[\\(\\)]",
							PHPFunctions.substr(element, 1, indexBrace - 1)) > 0) {
						// Illegal characters in comment
						return EMailSyntaxDiagnosis.ISEMAIL_BADCOMMENT_START;
					}
					element = PHPFunctions.substr(element, indexBrace + 1,
							elementLength - indexBrace - 1);
					elementLength = element.length();
				}
			}

			if (element.charAt(elementLength - 1) == ')') {
				int indexBrace = element.lastIndexOf('(');
				if (indexBrace != -1) {
					if (PHPFunctions.preg_match("(?<!\\\\)(?:[\\(\\)])",
							PHPFunctions.substr(element, indexBrace + 1,
									elementLength - indexBrace - 2)) > 0) {
						// Illegal characters in comment
						return EMailSyntaxDiagnosis.ISEMAIL_BADCOMMENT_END;
					}
					element = PHPFunctions.substr(element, 0, indexBrace);
					elementLength = element.length();
				}
			}

			// Remove any leading or trailing FWS around the element (inside any
			// comments)
			element = PHPFunctions.preg_replace("^" + FWS + "|" + FWS + "$",
					"", element);

			// What's left counts towards the maximum length for this part
			if (partLength > 0)
				partLength++; // for the dot
			partLength += element.length();

			// Each dot-delimited component can be an atom or a quoted string
			// (because of the obs-local-part provision)

			if (PHPFunctions.preg_match("(?s)^\"(?:.)*\"$", element) > 0) {
				// Quoted-string tests:
				//
				// Remove any FWS
				element = PHPFunctions.preg_replace("(?<!\\\\)" + FWS, "",
						element);
				// My regular expression skills aren't up to distinguishing
				// between \" \\" \\\" \\\\" etc.
				// So remove all \\ from the string first...
				element = PHPFunctions.preg_replace("\\\\\\\\", " ", element);
				if (PHPFunctions
						.preg_match(
								"(?<!\\\\|^)[\"\\r\\n\\x00](?!$)|\\\\\"$|\"\"",
								element) > 0) {
					// ", CR, LF and NUL must be escaped, "" is too short
					return EMailSyntaxDiagnosis.ISEMAIL_UNESCAPEDDELIM;
				}
			} else {
				// Unquoted string tests:
				//
				// Period (".") may...appear, but may not be used to start or
				// end the
				// local part, nor may two or more consecutive periods appear.
				// (http://tools.ietf.org/html/rfc3696#section-3)
				//
				// A zero-length element implies a period at the beginning or
				// end of the
				// local part, or two periods together. Either way it's not
				// allowed.
				if (element.isEmpty()) {
					// Dots in wrong place
					return EMailSyntaxDiagnosis.ISEMAIL_EMPTYELEMENT;
				}

				// Any ASCII graphic (printing) character other than the
				// at-sign ("@"), backslash, double quote, comma, or square
				// brackets may
				// appear without quoting. If any of that list of excluded
				// characters
				// are to appear, they must be quoted
				// (http://tools.ietf.org/html/rfc3696#section-3)
				//
				// Any excluded characters? i.e. 0x00-0x20, (, ), <, >, [, ], :,
				// ;, @, \, comma, period, "
				if (PHPFunctions.preg_match(
						"[\\x00-\\x20\\(\\)<>\\[\\]:;@\\\\,\\.\"]", element) > 0) {
					// These characters must be in a quoted string
					return EMailSyntaxDiagnosis.ISEMAIL_UNESCAPEDSPECIAL;
				}
			}
		}

		if (partLength > 64) {
			// Local part must be 64 characters or less
			return EMailSyntaxDiagnosis.ISEMAIL_LOCALTOOLONG;
		}

		// Now let's check the domain part...

		// The domain name can also be replaced by an IP address in square
		// brackets
		// (http://tools.ietf.org/html/rfc3696#section-3)
		// (http://tools.ietf.org/html/rfc5321#section-4.1.3)
		// (http://tools.ietf.org/html/rfc4291#section-2.2)

		if (PHPFunctions.preg_match("^\\[(.)+]$", domain) == 1) {
			// It's an address-literal
			String addressLiteral = PHPFunctions.substr(domain, 1, domain
					.length() - 2);

			String IPv6;
			int groupMax;

			// Extract IPv4 part from the end of the address-literal (if there
			// is one)
			String[] matchesIP = PHPFunctions
					.preg_match_to_array(
							"\\b(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$",
							addressLiteral);
			if (matchesIP.length > 0) {
				int index = addressLiteral.lastIndexOf(matchesIP[0]);

				if (index == 0) {
					// Nothing there except a valid IPv4 address, so...
					return EMailSyntaxDiagnosis.ISEMAIL_VALID;
				} else {
					// Assume it's an attempt at a mixed address (IPv6 + IPv4)
					if (addressLiteral.charAt(index - 1) != ':') {
						// Character preceding IPv4 address must be ':'
						return EMailSyntaxDiagnosis.ISEMAIL_IPV4BADPREFIX;
					}
					if (!addressLiteral.startsWith("IPv6:")) {
						// RFC5321 section 4.1.3
						return EMailSyntaxDiagnosis.ISEMAIL_IPV6BADPREFIXMIXED;
					}

					IPv6 = PHPFunctions.substr(addressLiteral, 5,
							(index == 7) ? 2 : index - 6);
					groupMax = 6;
				}
			} else {
				// It must be an attempt at pure IPv6
				if (!addressLiteral.startsWith("IPv6:")) {
					// RFC5321 section 4.1.3
					return EMailSyntaxDiagnosis.ISEMAIL_IPV6BADPREFIX;
				}
				IPv6 = PHPFunctions.substr(addressLiteral, 5);
				groupMax = 8;
			}

			String[][] matchesIP6 = PHPFunctions.preg_match_all(
					"^[0-9a-fA-F]{0,4}|\\:[0-9a-fA-F]{0,4}|(.)", IPv6);
			int groupCount = 0;
			if (matchesIP6.length > 0) {
				groupCount = matchesIP6[0].length;
			} // else: Undefined state (should never be reached)
			int index = IPv6.indexOf("::");

			if (index == -1) {
				// We need exactly the right number of groups
				if (groupCount != groupMax) {
					// RFC5321 section 4.1.3
					return EMailSyntaxDiagnosis.ISEMAIL_IPV6GROUPCOUNT;
				}
			} else {
				if (index != IPv6.lastIndexOf("::")) {
					// More than one '::'
					return EMailSyntaxDiagnosis.ISEMAIL_IPV6DOUBLEDOUBLECOLON;
				}
				groupMax = (index == 0 || index == (IPv6.length() - 2)) ? groupMax
						: groupMax - 1;
				if (groupCount > groupMax) {
					// Too many IPv6 groups in address
					return EMailSyntaxDiagnosis.ISEMAIL_IPV6TOOMANYGROUPS;
				}
			}

			// Daniel Marschall: For the Java translation, I optimized
			// the process. Instead of sorting the array (which needs
			// null-pointer checks and array-length checks) and then
			// checking element [0], I decided to directly check every
			// element.

			// Check for unmatched characters
			// array_multisort(matchesIP6[1], SORT_DESC);
			// if ($matchesIP6[1][0] !== '')) {
			// return EMailResultState.ISEMAIL_IPV6BADCHAR;
			// }

			// Check for unmatched characters
			if (matchesIP6.length > 1) {
				for (String s : matchesIP6[1]) {
					if ((s != null) && (!s.isEmpty())) {
						return EMailSyntaxDiagnosis.ISEMAIL_IPV6BADCHAR;
					}
				}
			} // else: Undefined state (should never be reached)

			// It's a valid IPv6 address, so...
			return EMailSyntaxDiagnosis.ISEMAIL_VALID;
		} else {
			// It's a domain name...

			// The syntax of a legal Internet host name was specified in RFC-952
			// One aspect of host name syntax is hereby changed: the
			// restriction on the first character is relaxed to allow either a
			// letter or a digit.
			// (http://tools.ietf.org/html/rfc1123#section-2.1)
			//
			// NB RFC 1123 updates RFC 1035, but this is not currently apparent
			// from reading RFC 1035.
			//
			// Most common applications, including email and the Web, will
			// generally not
			// permit...escaped strings
			// (http://tools.ietf.org/html/rfc3696#section-2)
			//
			// the better strategy has now become to make the
			// "at least one period" test,
			// to verify LDH conformance (including verification that the
			// apparent TLD name
			// is not all-numeric)
			// (http://tools.ietf.org/html/rfc3696#section-2)
			//
			// Characters outside the set of alphabetic characters, digits, and
			// hyphen MUST NOT appear in domain name
			// labels for SMTP clients or servers
			// (http://tools.ietf.org/html/rfc5321#section-4.1.2)
			//
			// RFC5321 precludes the use of a trailing dot in a domain name for
			// SMTP purposes
			// (http://tools.ietf.org/html/rfc5321#section-4.1.2)

			dotArray = PHPFunctions.preg_split(
					"(?m)\\.(?=(?:[^\\\"]*\\\"[^\\\"]*\\\")*(?![^\\\"]*\\\"))",
					domain);
			partLength = 0;
			// Since we use 'element' after the foreach
			// loop let's make sure it has a value
			String lastElement = "";
			// revision 1.13: Line above added because PHPLint now checks for
			// Definitely Assigned Variables

			if (dotArray.length == 1) {
				// Mail host can't be a TLD (cite? What about localhost?)
				return EMailSyntaxDiagnosis.ISEMAIL_TLD;
			}

			for (String element : dotArray) {
				lastElement = element;
				// Remove any leading or trailing FWS
				element = PHPFunctions.preg_replace(
						"^" + FWS + "|" + FWS + "$", "", element);
				int elementLength = element.length();

				// Each dot-delimited component must be of type atext
				// A zero-length element implies a period at the beginning or
				// end of the
				// local part, or two periods together. Either way it's not
				// allowed.
				if (elementLength == 0) {
					// Dots in wrong place
					return EMailSyntaxDiagnosis.ISEMAIL_DOMAINEMPTYELEMENT;
				}
				// revision 1.15: Speed up the test and get rid of
				// "uninitialized string offset" notices from PHP

				// Then we need to remove all valid comments (i.e. those at the
				// start or end of the element
				if (element.charAt(0) == '(') {
					int indexBrace = element.indexOf(')');
					if (indexBrace != -1) {
						if (PHPFunctions
								.preg_match("(?<!\\\\)[\\(\\)]", PHPFunctions
										.substr(element, 1, indexBrace - 1)) > 0) {
							// revision 1.17: Fixed name of constant (also
							// spotted by turboflash - thanks!)
							// Illegal characters in comment
							return EMailSyntaxDiagnosis.ISEMAIL_BADCOMMENT_START;
						}
						element = PHPFunctions.substr(element, indexBrace + 1,
								elementLength - indexBrace - 1);
						elementLength = element.length();
					}
				}

				if (element.charAt(elementLength - 1) == ')') {
					int indexBrace = element.lastIndexOf('(');
					if (indexBrace != -1) {
						if (PHPFunctions.preg_match("(?<!\\\\)(?:[\\(\\)])",
								PHPFunctions.substr(element, indexBrace + 1,
										elementLength - indexBrace - 2)) > 0) {
							// revision 1.17: Fixed name of constant (also
							// spotted by turboflash - thanks!)
							// Illegal characters in comment
							return EMailSyntaxDiagnosis.ISEMAIL_BADCOMMENT_END;
						}

						element = PHPFunctions.substr(element, 0, indexBrace);
						elementLength = element.length();
					}
				}

				// Remove any leading or trailing FWS around the element (inside
				// any comments)
				element = PHPFunctions.preg_replace(
						"^" + FWS + "|" + FWS + "$", "", element);

				// What's left counts towards the maximum length for this part
				if (partLength > 0)
					partLength++; // for the dot
				partLength += element.length();

				// The DNS defines domain name syntax very generally -- a
				// string of labels each containing up to 63 8-bit octets,
				// separated by dots, and with a maximum total of 255
				// octets.
				// (http://tools.ietf.org/html/rfc1123#section-6.1.3.5)
				if (elementLength > 63) {
					// Label must be 63 characters or less
					return EMailSyntaxDiagnosis.ISEMAIL_DOMAINELEMENTTOOLONG;
				}

				// Any ASCII graphic (printing) character other than the
				// at-sign ("@"), backslash, double quote, comma, or square
				// brackets may
				// appear without quoting. If any of that list of excluded
				// characters
				// are to appear, they must be quoted
				// (http://tools.ietf.org/html/rfc3696#section-3)
				//
				// If the hyphen is used, it is not permitted to appear at
				// either the beginning or end of a label.
				// (http://tools.ietf.org/html/rfc3696#section-2)
				//
				// Any excluded characters? i.e. 0x00-0x20, (, ), <, >, [, ], :,
				// ;, @, \, comma, period, "

				if (PHPFunctions.preg_match(
						"[\\x00-\\x20\\(\\)<>\\[\\]:;@\\\\,\\.\"]|^-|-$",
						element) > 0) {
					return EMailSyntaxDiagnosis.ISEMAIL_DOMAINBADCHAR;
				}
			}

			if (partLength > 255) {
				// Domain part must be 255 characters or less
				// (http://tools.ietf.org/html/rfc1123#section-6.1.3.5)
				return EMailSyntaxDiagnosis.ISEMAIL_DOMAINTOOLONG;
			}

			if (PHPFunctions.preg_match("^[0-9]+$", lastElement) > 0) {
				// TLD can't be all-numeric
				// (http://www.apps.ietf.org/rfc/rfc3696.html#sec-2)
				return EMailSyntaxDiagnosis.ISEMAIL_TLDNUMERIC;
			}

			// Check DNS?
			if (checkDNS) {
				try {
					if (!((DNSLookup.doLookup(domain, DNSType.A) > 0) || (DNSLookup
							.doLookup(domain, DNSType.MX) > 0))) {
						// Domain doesn't actually exist
						return EMailSyntaxDiagnosis.ISEMAIL_DOMAINNOTFOUND;
					}
				} catch (NamingException e) {
					// Resp.: Internal error
					return EMailSyntaxDiagnosis.ISEMAIL_DOMAINNOTFOUND;
				}
			}
		}

		// Eliminate all other factors, and the one which remains must be the
		// truth. (Sherlock Holmes, The Sign of Four)
		return EMailSyntaxDiagnosis.ISEMAIL_VALID;
	}

	/**
	 * Replaces a char in a String
	 * 
	 * @param s
	 *            The input string
	 * @param pos
	 *            The position of the char to be replaced
	 * @param c
	 *            The new char
	 * @return The new String
	 * @see Source: http://www.rgagnon.com/javadetails/java-0030.html
	 */
	public static String replaceCharAt(String s, int pos, char c) {
		return s.substring(0, pos) + c + s.substring(pos + 1);
	}

	private IsEMail() {
	}
}
