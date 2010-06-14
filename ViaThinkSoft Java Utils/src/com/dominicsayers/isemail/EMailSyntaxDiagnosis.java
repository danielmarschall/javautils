package com.dominicsayers.isemail;

/**
 * This enumeration contains all email syntax diagnosis values which can be
 * returned by the email syntax validator.
 * 
 * @package isemail
 * @author Dominic Sayers <dominic_sayers@hotmail.com>; Translated from PHP into
 *         Java by Daniel Marschall [www.daniel-marschall.de]
 * @copyright 2010 Dominic Sayers; Java-Translation 2010 by Daniel Marschall
 * @license http://www.opensource.org/licenses/bsd-license.php BSD License
 * @see http://www.dominicsayers.com/isemail
 * @version 1.0 Initial release as Java code by Daniel Marschall
 */

public enum EMailSyntaxDiagnosis {

	ISEMAIL_VALID, ISEMAIL_TOOLONG, ISEMAIL_NOAT, ISEMAIL_NOLOCALPART, ISEMAIL_NODOMAIN, ISEMAIL_ZEROLENGTHELEMENT, ISEMAIL_BADCOMMENT_START, ISEMAIL_BADCOMMENT_END, ISEMAIL_UNESCAPEDDELIM, ISEMAIL_EMPTYELEMENT, ISEMAIL_UNESCAPEDSPECIAL, ISEMAIL_LOCALTOOLONG, ISEMAIL_IPV4BADPREFIX, ISEMAIL_IPV6BADPREFIXMIXED, ISEMAIL_IPV6BADPREFIX, ISEMAIL_IPV6GROUPCOUNT, ISEMAIL_IPV6DOUBLEDOUBLECOLON, ISEMAIL_IPV6BADCHAR, ISEMAIL_IPV6TOOMANYGROUPS, ISEMAIL_TLD, ISEMAIL_DOMAINEMPTYELEMENT, ISEMAIL_DOMAINELEMENTTOOLONG, ISEMAIL_DOMAINBADCHAR, ISEMAIL_DOMAINTOOLONG, ISEMAIL_TLDNUMERIC, ISEMAIL_DOMAINNOTFOUND
	/* , ISEMAIL_NOTDEFINED */

}
