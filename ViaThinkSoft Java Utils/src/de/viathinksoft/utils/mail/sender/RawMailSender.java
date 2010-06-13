package de.viathinksoft.utils.mail.sender;

// Ref:
// http://openbook.galileocomputing.de/javainsel8/javainsel_18_012.htm#mjb306e4632c440d0524d00f224d4fa1bb - Kapitel 18.12.6
// http://java.sun.com/developer/onlineTraining/JavaMail/contents.html

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import de.viathinksoft.utils.mail.InvalidMailAddressException;

abstract public class RawMailSender {

	private static final String TRANSPORT_PROTOCOL = "smtp";

	protected Session session;
	protected Message msg;
	protected Properties props;
	protected Authenticator auth;

	private String smtpHost = "localhost";
	private String smtpUsername = "";
	private String smtpPassword = "";
	private int smtpPort = 25;
	private boolean smtpAuth = false;
	// private String smtpAuthUser = "";
	// private String smtpAuthPass = "";
	private String recipient = "";
	private String subject = "";
	private String mailFrom = "";

	public String getMailFrom() {
		return mailFrom;
	}

	public void setMailFrom(String mailFrom) throws InvalidMailAddressException {
		if (mailFrom == null) throw new InvalidMailAddressException();
		this.mailFrom = mailFrom.trim();
	}

	public String getRecipient() {
		return recipient;
	}

	public void setRecipient(String recipient) throws InvalidMailAddressException {
		if (recipient == null) throw new InvalidMailAddressException();
		this.recipient = recipient.trim();
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
		if (this.subject == null)
			this.subject = "";
	}

	public String getSmtpHost() {
		return smtpHost;
	}

	public void setSmtpHost(String smtpHost) {
		this.smtpHost = smtpHost;
		if (this.smtpHost == null)
			this.smtpHost = "";
	}

	public String getSmtpUsername() {
		return smtpUsername;
	}

	public void setSmtpUsername(String smtpUsername) {
		this.smtpUsername = smtpUsername;
		if (this.smtpUsername == null)
			this.smtpUsername = "";
	}

	public String getSmtpPassword() {
		return smtpPassword;
	}

	public void setSmtpPassword(String smtpPassword) {
		this.smtpPassword = smtpPassword;
		if (this.smtpPassword == null)
			this.smtpPassword = "";
	}

	public boolean isSmtpAuth() {
		return smtpAuth;
	}

	public void setSmtpAuth(boolean smtpAuth) {
		this.smtpAuth = smtpAuth;
	}

	// public String getSmtpAuthUser() {
	// return smtpAuthUser;
	// }
	//
	// public void setSmtpAuthUser(String smtpAuthUser) {
	// this.smtpAuthUser = smtpAuthUser;
	// if (this.smtpAuthUser == null) this.smtpAuthUser = "";
	// }
	//
	// public String getSmtpAuthPass() {
	// return smtpAuthPass;
	// }
	//
	// public void setSmtpAuthPass(String smtpAuthPass) {
	// this.smtpAuthPass = smtpAuthPass;
	// if (this.smtpAuthPass == null) this.smtpAuthPass = "";
	// }

	public int getSmtpPort() {
		return smtpPort;
	}

	public void setSmtpPort(int smtpPort) {
		this.smtpPort = smtpPort;
	}

	protected void generateProperties()
			throws AuthentificateDataIncompleteException {
		props = new Properties();

		// http://72.5.124.55/products/javamail/javadocs/com/sun/mail/smtp/package-summary.html
		if ((smtpHost != null) && (!"".equals(smtpHost))) {
			props.put("mail.smtp.host", smtpHost);
		}
		props.setProperty("mail.transport.protocol", TRANSPORT_PROTOCOL);
		props.setProperty("mail.smtp.port", "" + smtpPort);
		if ((smtpUsername != null) && (!"".equals(smtpUsername))) {
			props.setProperty("mail.smtp.user", smtpUsername);
		}

		props.put("mail.smtp.auth", smtpAuth);
		if (smtpAuth) {
			String smtpAuthUser = smtpUsername;
			String smtpAuthPass = smtpPassword;

			if ((smtpAuthUser != null) && (!"".equals(smtpAuthUser))) {
				if ((smtpAuthPass != null) && (!"".equals(smtpAuthPass))) {
					auth = new SmtpAuthenticator(smtpAuthUser, smtpAuthPass);
				} else {
					throw new AuthentificateDataIncompleteException();
				}
			} else {
				throw new AuthentificateDataIncompleteException();
			}
		} else {
			auth = null;
		}

		if ((mailFrom != null) && (!"".equals(mailFrom))) {
			props.setProperty("mail.smtp.from", mailFrom);
		}
	}

	protected void generateSession() {
		// http://blog.dafuer.de/2006/08/22/javamail-access-to-default-session-denied/
		// final Session session = Session.getDefaultInstance(props, auth);
		session = Session.getInstance(props, auth);
	}

	protected void generateMailObject() throws MessagingException,
			AuthentificateDataIncompleteException {

		/* final Message */msg = new MimeMessage(session);

		final InternetAddress addressTo = new InternetAddress(recipient);
		msg.setRecipient(Message.RecipientType.TO, addressTo);
		msg.setSubject(subject);

		if ((mailFrom != null) && (!"".equals(mailFrom))) {
			final InternetAddress addressFrom = new InternetAddress(mailFrom);
			msg.setFrom(addressFrom);
		}
	}

	protected void doSend() throws MessagingException {
		Transport tr = session.getTransport(TRANSPORT_PROTOCOL);
		tr.connect(smtpHost, smtpPort, smtpUsername, smtpPassword);
		msg.saveChanges(); // don't forget this
		tr.sendMessage(msg, msg.getAllRecipients());
		tr.close();
	}

	public void sendMail() throws MessagingException,
			AuthentificateDataIncompleteException {

		// Damit die Funktionalität später erweitert oder verändert werden kann!
		generateProperties();
		generateSession();
		generateMailObject();
		doSend();
	}
}