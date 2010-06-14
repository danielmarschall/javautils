package com.iamcal.rfc3696;

import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//
// RFC3696 Email Parser
//
// By Cal Henderson <cal@iamcal.com>
//
// This code is dual licensed:
// CC Attribution-ShareAlike 2.5 - http://creativecommons.org/licenses/by-sa/2.5/
// GPLv3 - http://www.gnu.org/copyleft/gpl.html
//
// $Revision: 5039 $
//
// Translated from PHP to Java and slightly improved by Daniel Marschall
// Source: http://code.iamcal.com/php/rfc822/rfc3696.phps
// Current version: 2010-06-10
//

public class RFC3696EmailParser {

	public static boolean isValidEmailAddress(String email) { // was isValidRFC3696EmailAddress(String)
		
		if (email == null) email = "";
		
//		####################################################################################
//		#
//		# NO-WS-CTL       =       %d1-8 /         ; US-ASCII control characters
//		#                         %d11 /          ;  that do not include the
//		#                         %d12 /          ;  carriage return, line feed,
//		#                         %d14-31 /       ;  and white space characters
//		#                         %d127
//		# ALPHA          =  %x41-5A / %x61-7A   ; A-Z / a-z
//		# DIGIT          =  %x30-39

		final String no_ws_ctl	= "[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x7f]";
		final String alpha		= "[\\x41-\\x5a\\x61-\\x7a]";
		final String digit		= "[\\x30-\\x39]";
		final String cr			= "\\x0d";
		final String lf			= "\\x0a";
		final String crlf		= "(?:"+cr+lf+")";

//		####################################################################################
//		#
//		# obs-char        =       %d0-9 / %d11 /          ; %d0-127 except CR and
//		#                         %d12 / %d14-127         ;  LF
//		# obs-text        =       *LF *CR *(obs-char *LF *CR)
//		# text            =       %d1-9 /         ; Characters excluding CR and LF
//		#                         %d11 /
//		#                         %d12 /
//		#                         %d14-127 /
//		#                         obs-text
//		# obs-qp          =       "\" (%d0-127)
//		# quoted-pair     =       ("\" text) / obs-qp

		final String obs_char	= "[\\x00-\\x09\\x0b\\x0c\\x0e-\\x7f]";
//		final String obs_text	= "(?:"+lf+"*"+cr+"*(?:"+obs_char+lf+"*"+cr+"*)*)";
//		final String text 		= "(?:[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f]|"+obs_text+")";

//		#
//		# there's an issue with the definition of 'text', since 'obs_text' can
//		# be blank and that allows qp's with no character after the slash. we're
//		# treating that as bad, so this just checks we have at least one
//		# (non-CRLF) character
//		#

		final String text			= "(?:"+lf+"*"+cr+"*"+obs_char+lf+"*"+cr+"*)";
		final String obs_qp			= "(?:\\x5c[\\x00-\\x7f])";
		final String quoted_pair	= "(?:\\x5c"+text+"|"+obs_qp+")";

//		####################################################################################
//		#
//		# obs-FWS         =       1*WSP *(CRLF 1*WSP)
//		# FWS             =       ([*WSP CRLF] 1*WSP) /   ; Folding white space
//		#                         obs-FWS
//		# ctext           =       NO-WS-CTL /     ; Non white space controls
//		#                         %d33-39 /       ; The rest of the US-ASCII
//		#                         %d42-91 /       ;  characters not including "(",
//		#                         %d93-126        ;  ")", or "\"
//		# ccontent        =       ctext / quoted-pair / comment
//		# comment         =       "(" *([FWS] ccontent) [FWS] ")"
//		# CFWS            =       *([FWS] comment) (([FWS] comment) / FWS)
//
//		#
//		# note: we translate ccontent only partially to avoid an infinite loop
//		# instead, we'll recursively strip *nested* comments before processing
//		# the input. that will leave 'plain old comments' to be matched during
//		# the main parse.
//		#

		final String wsp		= "[\\x20\\x09]";
		final String obs_fws	= "(?:"+wsp+"+(?:"+crlf+wsp+"+)*)";
		final String fws		= "(?:(?:(?:"+wsp+"*"+crlf+")?"+wsp+"+)|"+obs_fws+")";
		final String ctext		= "(?:"+no_ws_ctl+"|[\\x21-\\x27\\x2A-\\x5b\\x5d-\\x7e])";
		final String ccontent	= "(?:"+ctext+"|"+quoted_pair+")";
		final String comment	= "(?:\\x28(?:"+fws+"?"+ccontent+")*"+fws+"?\\x29)";
		final String cfws		= "(?:(?:"+fws+"?"+comment+")*(?:"+fws+"?"+comment+"|"+fws+"))";

//		#
//		# these are the rules for removing *nested* comments. we'll just detect
//		# outer comment and replace it with an empty comment, and recurse until
//		# we stop.
//		#

		final String outer_ccontent_dull	= "(?:"+fws+"?"+ctext+"|"+quoted_pair+")";
		final String outer_ccontent_nest	= "(?:"+fws+"?"+comment+")";
		final String outer_comment			= "(?:\\x28"+outer_ccontent_dull+"*(?:"+outer_ccontent_nest+outer_ccontent_dull+"*)+"+fws+"?\\x29)";

//		####################################################################################
//		#
//		# atext           =       ALPHA / DIGIT / ; Any character except controls,
//		#                         "!" / "#" /     ;  SP, and specials.
//		#                         "$" / "%" /     ;  Used for atoms
//		#                         "&" / "'" /
//		#                         "*" / "+" /
//		#                         "-" / "/" /
//		#                         "=" / "?" /
//		#                         "^" / "_" /
//		#                         "`" / "{" /
//		#                         "|" / "}" /
//		#                         "~"
//		# atom            =       [CFWS] 1*atext [CFWS]

		final String atext	= "(?:"+alpha+"|"+digit+"|[\\x21\\x23-\\x27\\x2a\\x2b\\x2d\\x2f\\x3d\\x3f\\x5e\\x5f\\x60\\x7b-\\x7e])";
		final String atom	= "(?:"+cfws+"?(?:"+atext+")+"+cfws+"?)";

//		####################################################################################
//		#
//		# qtext           =       NO-WS-CTL /     ; Non white space controls
//		#                         %d33 /          ; The rest of the US-ASCII
//		#                         %d35-91 /       ;  characters not including "\"
//		#                         %d93-126        ;  or the quote character
//		# qcontent        =       qtext / quoted-pair
//		# quoted-string   =       [CFWS]
//		#                         DQUOTE *([FWS] qcontent) [FWS] DQUOTE
//		#                         [CFWS]
//		# word            =       atom / quoted-string

		final String qtext			= "(?:"+no_ws_ctl+"|[\\x21\\x23-\\x5b\\x5d-\\x7e])";
		final String qcontent		= "(?:"+qtext+"|"+quoted_pair+")";
//		final String quoted_string	= "(?:"+cfws+"?\\x22(?:"+fws+"?"+qcontent+")*"+fws+"?\\x22"+cfws+"?)";

//		#
//		# changed the '*' to a '+' to require that quoted strings are not empty
//		#

		final String quoted_string	= "(?:"+cfws+"?\\x22(?:"+fws+"?"+qcontent+")+"+fws+"?\\x22"+cfws+"?)";
		final String word			= "(?:"+atom+"|"+quoted_string+")";

//		####################################################################################
//		#
//		# obs-local-part  =       word *("." word)
//		# obs-domain      =       atom *("." atom)

		final String obs_local_part	= "(?:"+word+"(?:\\x2e"+word+")*)";
		final String obs_domain		= "(?:"+atom+"(?:\\x2e"+atom+")*)";

//		####################################################################################
//		#
//		# dot-atom-text   =       1*atext *("." 1*atext)
//		# dot-atom        =       [CFWS] dot-atom-text [CFWS]

		final String dot_atom_text	= "(?:"+atext+"+(?:\\x2e"+atext+"+)*)";
		final String dot_atom		= "(?:"+cfws+"?"+dot_atom_text+""+cfws+"?)";

//		####################################################################################
//		#
//		# domain-literal  =       [CFWS] "[" *([FWS] dcontent) [FWS] "]" [CFWS]
//		# dcontent        =       dtext / quoted-pair
//		# dtext           =       NO-WS-CTL /     ; Non white space controls
//		# 
//		#                         %d33-90 /       ; The rest of the US-ASCII
//		#                         %d94-126        ;  characters not including "[",
//		#                                         ;  "]", or "\"

		final String dtext			= "(?:"+no_ws_ctl+"|[\\x21-\\x5a\\x5e-\\x7e])";
		final String dcontent		= "(?:"+dtext+"|"+quoted_pair+")";
		final String domain_literal	= "(?:"+cfws+"?\\x5b(?:"+fws+"?"+dcontent+")*"+fws+"?\\x5d"+cfws+"?)";

//		####################################################################################
//		#
//		# local-part      =       dot-atom / quoted-string / obs-local-part
//		# domain          =       dot-atom / domain-literal / obs-domain
//		# addr-spec       =       local-part "@" domain

		final String local_part	= "(("+dot_atom+")|("+quoted_string+")|("+obs_local_part+"))";
		final String domain		= "(("+dot_atom+")|("+domain_literal+")|("+obs_domain+"))";
		final String addr_spec	= local_part+"\\x40"+domain;

//		#
//		# see http://www.dominicsayers.com/isemail/ for details, but this should probably be 254
//		#

		// TODO: Change to 254.
		// According to Errata ID 1690 of RFC 3696 (submitted by Dominik Sayers)
		// the length accepted by the IETF is 254 and not 256.
		// http://www.rfc-editor.org/errata_search.php?rfc=3696&eid=1690
		if (email.length() > 256) return false;

//		#
//		# we need to strip nested comments first - we replace them with a simple comment
//		#

		email = RFC3696StripComments(outer_comment, email, "(x)");

//		#
//		# now match what's left
//		#
		
		Matcher matcher = Pattern.compile("^"+addr_spec+"$").matcher(email);
		matcher.find();
		
		if (!matcher.matches()) {
			return false;
		}
		
		MatchResult m = matcher.toMatchResult();
		
		Bits bits = new Bits();
		bits.setLocal((m.group(1) == null) ? "" : m.group(1));
		bits.setLocalAtom((m.group(2) == null) ? "" : m.group(2));
		bits.setLocalQuoted((m.group(3) == null) ? "" : m.group(3));
		bits.setLocalObs((m.group(4) == null) ? "" : m.group(4));
		bits.setDomain((m.group(5) == null) ? "" : m.group(5));
		bits.setDomainAtom((m.group(6) == null) ? "" : m.group(6));
		bits.setDomainLiteral((m.group(7) == null) ? "" : m.group(7));
		bits.setDomainObs((m.group(8) == null) ? "" : m.group(8));

//		#
//		# we need to now strip comments from bits.getLocal() and bits.getDomain(),
//		# since we know they're i the right place and we want them out of the
//		# way for checking IPs, label sizes, etc
//		#

		bits.setLocal(RFC3696StripComments(comment, bits.getLocal()));
		bits.setDomain(RFC3696StripComments(comment, bits.getDomain()));

//		#
//		# length limits on segments
//		#

		if (bits.getLocal().length() > 64) return false;
		if (bits.getDomain().length() > 255) return false;

//		#
//		# restrictions on domain-literals from RFC2821 section 4.1.3
//		#

		if (bits.getDomainLiteral().length() > 0) {

			final String Snum					= "(\\d{1,3})";
			final String IPv4_address_literal	= Snum+"\\."+Snum+"\\."+Snum+"\\."+Snum;

			final String IPv6_hex				= "(?:[0-9a-fA-F]{1,4})";

			final String IPv6_full				= "IPv6\\:"+IPv6_hex+"(:?\\:"+IPv6_hex+"){7}";

			final String IPv6_comp_part			= "(?:"+IPv6_hex+"(?:\\:"+IPv6_hex+"){0,5})?";
			final String IPv6_comp				= "IPv6\\:("+IPv6_comp_part+"\\:\\:"+IPv6_comp_part+")";

			final String IPv6v4_full			= "IPv6\\:"+IPv6_hex+"(?:\\:"+IPv6_hex+"){5}\\:"+IPv4_address_literal;

			final String IPv6v4_comp_part		= IPv6_hex+"(?:\\:"+IPv6_hex+"){0,3}";
			final String IPv6v4_comp			= "IPv6\\:((?:"+IPv6v4_comp_part+")?\\:\\:(?:"+IPv6v4_comp_part+"\\:)?)"+IPv4_address_literal;

//			#
//			# IPv4 is simple
//			#
			
			matcher = Pattern.compile("^\\["+IPv4_address_literal+"\\]$").matcher(bits.getDomain());
			matcher.find();
			m = matcher.toMatchResult();

			if (matcher.matches()) {
				if (Integer.parseInt(m.group(1)) > 255) return false;
				if (Integer.parseInt(m.group(2)) > 255) return false;
				if (Integer.parseInt(m.group(3)) > 255) return false;
				if (Integer.parseInt(m.group(4)) > 255) return false;
			} else {

//				#
//				# this should be IPv6 - a bunch of tests are needed here :)
//				#

				while (true) {

					matcher = Pattern.compile("^\\["+IPv6_full+"\\]$").matcher(bits.getDomain());
					matcher.find();
					
					if (matcher.matches()){
						break;
					}

					matcher = Pattern.compile("^\\["+IPv6_comp+"\\]$").matcher(bits.getDomain());
					matcher.find();
					m = matcher.toMatchResult();

					if (matcher.matches()) {
						String m1 = m.group(1);
						String[] explode = m1.split("::");
						String a = "";
						String b = "";
						if (explode.length >= 2) {
							if (explode[0] != null) a = explode[0];
							if (explode[1] != null) b = explode[1];
						}

						String folded = ((a.length() > 0) && (b.length() > 0)) ? a+":"+b : a+b;
						String[] groups = folded.split(":");
						if (groups.length > 6) return false;
						break;
					}

					matcher = Pattern.compile("^\\["+IPv6v4_full+"\\]$").matcher(bits.getDomain());
					matcher.find();
					m = matcher.toMatchResult();

					if (matcher.matches()) {
						if (Integer.parseInt(m.group(1)) > 255) return false;
						if (Integer.parseInt(m.group(2)) > 255) return false;
						if (Integer.parseInt(m.group(3)) > 255) return false;
						if (Integer.parseInt(m.group(4)) > 255) return false;
						break;
					}

					matcher = Pattern.compile("^\\["+IPv6v4_comp+"\\]$").matcher(bits.getDomain());
					matcher.find();
					m = matcher.toMatchResult();

					if (matcher.matches()) {
						String m1 = m.group(1);
						String[] explode = m1.split("::");
						String a = "";
						String b = "";
						if (explode.length >= 2) {
							if (explode[0] != null) a = explode[0];
							if (explode[1] != null) b = explode[1];
						}
						
						if (b.length() > 0) /* Added by Daniel Marschall due to translation process */ {
							b = b.substring(0, b.length()-1); // remove the trailing colon before the IPv4 address
						}
						String folded = ((a.length() > 0) && (b.length() > 0)) ? a+":"+b : a+b;
						String[] groups = folded.split(":");
						if (groups.length > 4) return false;
						break;
					}

					return false;
				}
			}			
		}else{

//			#
//			# the domain is either dot-atom or obs-domain - either way, it's
//			# made up of simple labels and we split on dots
//			#

			String[] labels = bits.getDomain().split("\\.");

//			#
//			# this is allowed by both dot-atom and obs-domain, but is un-routeable on the
//			# public internet, so we'll fail it (e.g. user@localhost)
//			#

			if (labels.length == 1) return false;

//			#
//			# checks on each label
//			#

			for (String label : labels) {
				if (label.length() > 63) return false;
				if (label.charAt(0) == '-') return false;
				if (label.charAt(label.length()-1) == '-') return false;
			}
			
//			#
//			# last label can't be all numeric
//			#
			
			String arrayPopResult;
			if (labels.length > 0) {
				arrayPopResult = labels[labels.length - 1];
				String[] tmpLabels = new String[labels.length - 1];
				for (int i = 0; i < labels.length - 1; i++) {
					tmpLabels[i] = labels[i];
				}
			} else {
				arrayPopResult = "";
			}

			matcher = Pattern.compile("^[0-9]+$").matcher(arrayPopResult);
			matcher.find();
			if (matcher.matches()) return false;
		}

		return true;
	}

	private static String RFC3696StripComments(String comment, String email,
			String replace) {
		while (true) {
			String newEmail = email.replaceAll(comment, replace);
			if (newEmail.length() == email.length()) {
				return email;
			}
			email = newEmail;
		}
	}

	private static String RFC3696StripComments(String comment, String email) {
		return RFC3696StripComments(comment, email, "");
	}

	private RFC3696EmailParser() {
	}

}