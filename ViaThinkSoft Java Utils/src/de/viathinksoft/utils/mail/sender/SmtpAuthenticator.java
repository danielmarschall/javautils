package de.viathinksoft.utils.mail.sender;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

class SmtpAuthenticator extends Authenticator {
	// Ref: http://forum.javacore.de/viewtopic.php?p=60996#60996

	String pass = "";
	String login = "";

	public SmtpAuthenticator() {
		super();
	}

	public SmtpAuthenticator(String login, String pass) {
		super();

		this.login = login;
		this.pass = pass;
	}

	public PasswordAuthentication getPasswordAuthentication() {
		if ("".equals(pass)) {
			return null;
		} else {
			return new PasswordAuthentication(login, pass);
		}
	}
}