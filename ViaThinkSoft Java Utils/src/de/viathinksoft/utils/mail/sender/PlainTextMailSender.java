package de.viathinksoft.utils.mail.sender;

import javax.mail.MessagingException;

import de.viathinksoft.utils.mail.EMailAddress;
import de.viathinksoft.utils.mail.InvalidMailAddressException;

public class PlainTextMailSender extends RawMailSender {
	
	// --- E-Mail-Adressobjekt benutzen (dekodiert automatisch den Punycode)
	
	public void setMailFrom(String mailFrom) throws InvalidMailAddressException {
		this.setMailFrom(new EMailAddress(mailFrom));
	}

	public void setMailFrom(EMailAddress mailFrom) throws InvalidMailAddressException {
		if (mailFrom == null) throw new InvalidMailAddressException();
		super.setMailFrom(mailFrom.getMailAddressPunycodedDomain());
	}

	public void setRecipient(String recipient) throws InvalidMailAddressException {
		this.setRecipient(new EMailAddress(recipient));
	}

	public void setRecipient(EMailAddress recipient) throws InvalidMailAddressException {
		if (recipient == null) throw new InvalidMailAddressException();
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
