package de.viathinksoft.utils.mail.sender;

import javax.mail.MessagingException;

import de.viathinksoft.utils.mail.EMailAddress;

public class PlainTextMailSender extends RawMailSender {
	
	// --- E-Mail-Adressobjekt benutzen (dekodiert automatisch den Punycode)
	
	public void setMailFrom(String mailFrom) {
		this.setMailFrom(new EMailAddress(mailFrom));
	}

	public void setMailFrom(EMailAddress mailFrom) {
		super.setMailFrom(mailFrom.getMailAddressPunycodedDomain());
	}

	public void setRecipient(String recipient) {
		this.setRecipient(new EMailAddress(recipient));
	}

	public void setRecipient(EMailAddress recipient) {
		super.setRecipient(recipient.getMailAddressPunycodedDomain());
	}
	
	// --- PlainText Implementieren
	
	private String message;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
		if (this.message == null)
			this.message = "";
	}

	protected void generateMailObject() throws MessagingException,
			AuthentificateDataIncompleteException {

		super.generateMailObject();
		
		msg.setContent(message, "text/plain");
	}

}
