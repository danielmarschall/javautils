package de.viathinksoft.utils.mail.address;

import java.net.IDN;

/**
 * 
 * This class parses an email address (trims whitespaces from it) and stores it
 * in its original form as well as store a RFC-compatible punycoded domainpart.
 * So, if you enter a Unicode-Mail-Address you can easily access the trimmed and
 * punycoded domain-part mail-address. Warning! This class does NOT check if the
 * email address is fully valid. Please use the syntax checker class for this.
 * 
 * @author Daniel Marschall
 * 
 */
public class EMailAddress {

	// Constants

	/**
	 * This constant is used by toString() and tells if whether
	 * getMailAddressUnicode() or getMailAddressPunycodedDomain() should be
	 * returned.
	 */
	static boolean USE_UNICODE_AS_STANDARD = false;

	// Attributes

	/**
	 * The local part of our parsed mail address. (Part before "@") It is
	 * allways Unicode, since the mail servers have to take care about it. Even
	 * if Unicode mail addresses will become popular in future, the local part
	 * will probably not punycoded.
	 */
	private String localPart;
	/**
	 * The domain part of our parsed mail address (part after "@") inclusive our
	 * top level domain (TLD). It is in its Unicode form.
	 */
	private String domainPartUnicode;

	/**
	 * The domain part of our parsed mail address (part after "@") inclusive our
	 * top level domain (TLD). It is in its Punycode (ASCII) form.
	 */
	private String domainPartPunycode;

	/**
	 * The top level domain (COM, ORG, BIZ...) of our parsed mail address. The
	 * dot is not included. It is in its Unicode form.
	 */
	private String tldUnicode;

	/**
	 * The top level domain (COM, ORG, BIZ...) of our parsed mail address. The
	 * dot is not included. It is in its Punycode form.
	 */
	private String tldPunycode;

	// Getter and Setter

	/**
	 * The local part of our parsed mail address. (Part before "@") It is
	 * allways Unicode, since the mail servers have to take care about it. Even
	 * if Unicode mail addresses will become popular in future, the local part
	 * will probably not punycoded.
	 * 
	 * @return The local part
	 */
	public String getLocalPart() {
		return localPart;
	}

	/**
	 * The domain part of our parsed mail address (part after "@") inclusive our
	 * top level domain (TLD). It is in its Unicode form.
	 * 
	 * @return The domain part in Unicode.
	 */
	public String getDomainPartUnicode() {
		return domainPartUnicode;
	}

	/**
	 * The domain part of our parsed mail address (part after "@") inclusive our
	 * top level domain (TLD). It is in its Punycode (ASCII) form.
	 * 
	 * @return The domain part in Punycode.
	 */
	public String getDomainPartPunycode() {
		return domainPartPunycode;
	}

	/**
	 * The top level domain (COM, ORG, BIZ...) of our parsed mail address. The
	 * dot is not included. It is in its Unicode form.
	 * 
	 * @return The TLD in Unicode.
	 */
	public String getTldUnicode() {
		return tldUnicode;
	}

	/**
	 * The top level domain (COM, ORG, BIZ...) of our parsed mail address. The
	 * dot is not included. It is in its Punycode form.
	 * 
	 * @return The TLD in Punycode.
	 */
	public String getTldPunycode() {
		return tldPunycode;
	}

	// Constructors

	/**
	 * Creates an email address object out of an email address string.
	 * 
	 * @param eMailAddress
	 *            bare computer email address. e.g. roedyg@mindprod.com No
	 *            "Roedy Green" <roedyg@mindprod.com> style addresses. No local
	 *            addresses, e.g. roedy.
	 */
	public EMailAddress(String eMailAddress) {
		super();

		// Zuerst trimmen (z.B. für Formulardaten)
		eMailAddress = eMailAddress.trim();

		// Wir splitten dann beim At-Zeichen (@)
		String localPart = "";
		String domainPart = "";
		int atIndex = eMailAddress.lastIndexOf('@');
		if (atIndex == -1) {
			localPart = eMailAddress;
			domainPart = "";
		} else {
			localPart = eMailAddress.substring(0, atIndex);
			domainPart = eMailAddress.substring(atIndex + 1);
		}

		// We parse the local part.

		if (localPart == null)
			localPart = "";
		this.localPart = localPart;

		// We parse the domainPart and allocate punycode and unicode fields.

		if (domainPart == null)
			domainPart = "";
		if (isUnicode(domainPart)) {
			this.domainPartUnicode = domainPart;
			this.domainPartPunycode = IDN.toASCII(domainPart);
		} else /* if (isPunycode(domainPart)) */{
			this.domainPartUnicode = IDN.toUnicode(domainPart);
			this.domainPartPunycode = domainPart;
		}

		// We additionally parse the TLD and also determinate if it is punycode
		// or not.

		int dotIdx;

		dotIdx = this.domainPartUnicode.lastIndexOf('.');
		if (dotIdx >= 0) {
			this.tldUnicode = this.domainPartUnicode.substring(dotIdx + 1);
		} else {
			// We do not throw an exception here because it could be an email to
			// a network computer or an IP address.
			this.tldUnicode = "";
		}

		dotIdx = this.domainPartPunycode.lastIndexOf('.');
		if (dotIdx >= 0) {
			this.tldPunycode = this.domainPartPunycode.substring(dotIdx + 1);
		} else {
			// We do not throw an exception here because it could be an email to
			// a network computer or an IP address.
			this.tldPunycode = "";
		}
	}

	// Methods

	/**
	 * Returns the email address with punycoded domain name and TLD. You should
	 * use this method to send emails.
	 * 
	 * @return The email address with punycoded domain name and TLD.
	 */
	public String getMailAddressPunycodedDomain() {
		if (this.domainPartPunycode.isEmpty()) {
			return this.localPart;
		} else {
			return this.localPart + "@" + this.domainPartPunycode;
		}
	}

	/**
	 * Returns the email address with internationalized domain names and TLD.
	 * 
	 * @return The email address with internationalized domain name and TLD.
	 */
	public String getMailAddressUnicode() {
		if (this.domainPartUnicode.isEmpty()) {
			return this.localPart;
		} else {
			return this.localPart + "@" + this.domainPartUnicode;
		}
	}

	/**
	 * Returns a string which represents the mail address. If the constant
	 * USE_UNICODE_AS_STANDARD is true, the internationalized domain names will
	 * not translated into the corresponding Punycode. If false, then not.
	 * 
	 * @return The string which represents the mail address. Warning! Since this
	 *         method is rather designed to show a formatted mail address, it
	 *         should NOT be used to send emails. Please only use this function
	 *         if you want to output.
	 */
	@Override
	public String toString() {
		if (USE_UNICODE_AS_STANDARD) {
			return this.getMailAddressUnicode();
		} else {
			return this.getMailAddressPunycodedDomain();
		}
	}

	/**
	 * Checks if an object is equal to our email address object.
	 * 
	 * @return Boolean which describes if it is equal or not.
	 */
	@Override
	public boolean equals(Object obj) {
		// Initial checks

		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (obj.getClass() != getClass())
			return false;

		// Compare the fields

		if (!this.domainPartPunycode
				.equals(((EMailAddress) obj).domainPartPunycode)) {
			return false;
		}
		if (!this.domainPartUnicode
				.equals(((EMailAddress) obj).domainPartUnicode)) {
			return false;
		}
		if (!this.localPart.equals(((EMailAddress) obj).localPart)) {
			return false;
		}
		if (!this.tldUnicode.equals(((EMailAddress) obj).tldUnicode)) {
			return false;
		}
		if (!this.tldPunycode.equals(((EMailAddress) obj).tldPunycode)) {
			return false;
		}

		// Everything's fine ^^

		return true;

		// return this.toString().equals(obj.toString());
	}

	/**
	 * Creates a deep copy of the email address object.
	 * 
	 * @return A new instance of the email address object with the same
	 *         properties.
	 */
	@Override
	protected EMailAddress clone() throws CloneNotSupportedException {
		return new EMailAddress(this.toString());
	}

	// ---------- STATIC FUNCTIONS ----------

	/**
	 * Determinates if a given string can be converted into Punycode.
	 * 
	 * @param str
	 *            The string which should be checked
	 * @return Boolean which shows if the string is not yet punicoded.
	 */
	protected static boolean isUnicode(String str) {
		if (str == null) {
			return false;
		}
		return (!IDN.toASCII(str).equals(str));
	}

	/**
	 * Determinates if a given string is in Punycode format.
	 * 
	 * @param str
	 *            The string which should be checked
	 * @return Boolean which shows if the string is punycoded or not.
	 */
	protected static boolean isPunycode(String str) {
		if (str == null) {
			return false;
		}
		return (!IDN.toUnicode(str).equals(str));
	}
}
