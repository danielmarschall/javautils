package de.viathinksoft.utils.mail.address;

/**
 * This class contains a function which "preproceses an email address. Following
 * steps will be performed: 1. The email address will be trimmed (in case of
 * user inputs) 2. E-Mail-Addresses with internationalized domain names will be
 * converted into ASCII compatible punycode (the local part will be left as it
 * is!)
 * 
 * @author Daniel Marschall
 * 
 */
public class EMailPreprocessor {
	
	public static String preprocess(String eMailAddress) {
		EMailAddress email = new EMailAddress(eMailAddress);
		
		return email.getMailAddressPunycodedDomain();
	}
	
	private EMailPreprocessor() {
	}

}
