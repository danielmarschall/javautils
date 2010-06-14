package de.viathinksoft.utils.mail.sender;

import static org.junit.Assert.fail;

import java.net.ConnectException;

import javax.mail.AuthenticationFailedException;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import org.junit.Test;

import de.viathinksoft.utils.mail.EMailAddress;
import de.viathinksoft.utils.mail.sender.PlainTextMailSender;
import eMailTests.TestConfiguration;

public class RawMailSenderPlainTextTest {
	@Test
	public void testPostMailNullPointer() throws MessagingException,
			AuthentificateDataIncompleteException {
		PlainTextMailSender mailsender = new PlainTextMailSender();

		try {
			mailsender.sendMail();
			fail();
		} catch (AddressException e) {
		} catch (MessagingException e) {
			// if ((e.getCause() != null) && (e.getCause().getNextException() ==
			// ConnectException)) {
			if ((e.getCause() != null)
					&& (e.getCause().getClass() == ConnectException.class)) {
				// Wir erwarten eine Exception, da wir davon ausgehen, dass auf
				// Localhost kein SMTP-Server läuft!
			} else {
				e.printStackTrace();
				fail();
			}
		}

		// -----------------------------------------------------

		try {
			mailsender.setRecipient((EMailAddress)null);
			fail();
		} catch (NullPointerException e1) {
		}

		try {
			mailsender.sendMail();
			fail();
		} catch (AddressException e) {
		} catch (MessagingException e) {
			// if ((e.getCause() != null) && (e.getCause().getNextException() ==
			// ConnectException)) {
			if ((e.getCause() != null)
					&& (e.getCause().getClass() == ConnectException.class)) {
				// Wir erwarten eine Exception, da wir davon ausgehen, dass auf
				// Localhost kein SMTP-Server läuft!
			} else {
				e.printStackTrace();
				fail();
			}
		}
		
		// -----------------------------------------------------

		try {
			mailsender.setRecipient((String)null);
			fail();
		} catch (NullPointerException e1) {
		}

		try {
			mailsender.sendMail();
			fail();
		} catch (AddressException e) {
		} catch (MessagingException e) {
			// if ((e.getCause() != null) && (e.getCause().getNextException() ==
			// ConnectException)) {
			if ((e.getCause() != null)
					&& (e.getCause().getClass() == ConnectException.class)) {
				// Wir erwarten eine Exception, da wir davon ausgehen, dass auf
				// Localhost kein SMTP-Server läuft!
			} else {
				e.printStackTrace();
				fail();
			}
		}

		// -----------------------------------------------------

		mailsender.setRecipient(TestConfiguration.getSpamAddress());

		mailsender.setSmtpHost(null);
		mailsender.setSmtpUsername(null);
		mailsender.setSmtpPassword(null);
		mailsender.setSmtpPort(-1);

		try {
			mailsender.sendMail();
			fail();
		} catch (MessagingException e) {
			// if ((e.getCause() != null) && (e.getCause().getNextException() ==
			// ConnectException)) {
			if ((e.getCause() != null)
					&& (e.getCause().getClass() == ConnectException.class)) {
				// Wir erwarten eine Exception, da wir davon ausgehen, dass auf
				// Localhost kein SMTP-Server läuft!
			} else {
				e.printStackTrace();
				fail();
			}
		}

		// -----------------------------------------------------

		mailsender.setSmtpHost(TestConfiguration.getSmtpHost());
		mailsender.setSmtpUsername(null);
		mailsender.setSmtpPassword(null);
		mailsender.setSmtpPort(TestConfiguration.getSmtpPort());

		mailsender.setSmtpAuth(true);
		// mailsender.setSmtpAuthUser(null);
		// mailsender.setSmtpAuthPass(null);

		try {
			mailsender.sendMail();
			fail();
		} catch (AuthentificateDataIncompleteException e) {
		}

		// -----------------------------------------------------

		mailsender.setSmtpAuth(false);

		try {
			mailsender.setMailFrom((EMailAddress)null);
			fail();
		} catch (NullPointerException e1) {
		}
		mailsender.setSubject(null);
		mailsender.setMessage(null);

		try {
			mailsender.sendMail();
		} catch (AuthenticationFailedException e) {
			// Diese Fehlermeldung KANN vom SMTP geworfen werden. MUSS aber
			// nicht.
		}
		
		// -----------------------------------------------------

		try {
			mailsender.setMailFrom((String)null);
			fail();
		} catch (NullPointerException e1) {
		}

		try {
			mailsender.sendMail();
		} catch (AuthenticationFailedException e) {
			// Diese Fehlermeldung KANN vom SMTP geworfen werden. MUSS aber
			// nicht.
		}

		// -----------------------------------------------------

		mailsender.setSmtpAuth(true);
		mailsender.setSmtpUsername(TestConfiguration.getSmtpUsername());
		mailsender.setSmtpPassword(TestConfiguration.getSmtpPassword());

		mailsender.sendMail();
	}

	@Test
	public void testPostMailBlank()
			throws AuthentificateDataIncompleteException {
		PlainTextMailSender mailsender = new PlainTextMailSender();

		mailsender.setRecipient(TestConfiguration.getSpamAddress());
		mailsender.setSubject("JUnit-Test 1");
		mailsender.setMessage("JUnit-Test 1 von Compuglobal");

		try {
			mailsender.sendMail();
			fail();
		} catch (MessagingException e) {
			// if ((e.getCause() != null) && (e.getCause().getNextException() ==
			// ConnectException)) {
			if ((e.getCause() != null)
					&& (e.getCause().getClass() == ConnectException.class)) {
				// Wir erwarten eine Exception, da wir davon ausgehen, dass auf
				// Localhost kein SMTP-Server läuft!
			} else {
				e.printStackTrace();
				fail();
			}
		}
	}

	@Test
	public void testPostMailWithData() throws MessagingException,
			AuthentificateDataIncompleteException
			{
		PlainTextMailSender mailsender = new PlainTextMailSender();

		mailsender.setSmtpHost(TestConfiguration.getSmtpHost());
		mailsender.setSmtpPort(TestConfiguration.getSmtpPort());

		mailsender.setRecipient(TestConfiguration.getSpamAddress());
		mailsender.setSubject("JUnit-Test 2");
		mailsender.setMessage("JUnit-Test 2 von Compuglobal");

		try {
			mailsender.sendMail();
		} catch (AuthenticationFailedException e) {
			// Diese Fehlermeldung KANN vom SMTP geworfen werden. MUSS aber
			// nicht.
		}
	}

	@Test
	public void testPostMailWithDataAndOrigin() throws MessagingException,
			AuthentificateDataIncompleteException {
		PlainTextMailSender mailsender = new PlainTextMailSender();

		mailsender.setSmtpHost(TestConfiguration.getSmtpHost());
		mailsender.setSmtpPort(TestConfiguration.getSmtpPort());

		mailsender.setMailFrom(TestConfiguration.getMailFrom());

		mailsender.setRecipient(TestConfiguration.getSpamAddress());
		mailsender.setSubject("JUnit-Test 3");
		mailsender.setMessage("JUnit-Test 3 von Compuglobal");

		try {
			mailsender.sendMail();
		} catch (AuthenticationFailedException e) {
			// Diese Fehlermeldung KANN vom SMTP geworfen werden. MUSS aber
			// nicht.
		}
	}

	@Test
	public void testPostMailWithDataAndOriginAndSmtpAuthIncompleteUserAndPwd()
			throws MessagingException {
		PlainTextMailSender mailsender = new PlainTextMailSender();

		mailsender.setSmtpHost(TestConfiguration.getSmtpHost());
		// mailsender.setSmtpUsername(TestConfiguration.getSmtpUsername());
		// mailsender.setSmtpPassword(TestConfiguration.getSmtpPassword());
		mailsender.setSmtpPort(TestConfiguration.getSmtpPort());

		mailsender.setMailFrom(TestConfiguration.getMailFrom());

		mailsender.setSmtpAuth(true);

		mailsender.setRecipient(TestConfiguration.getSpamAddress());
		mailsender.setSubject("JUnit-Test 4a");
		mailsender.setMessage("JUnit-Test 4a von Compuglobal");

		try {
			mailsender.sendMail();

			fail();
		} catch (AuthentificateDataIncompleteException e) {
		}
	}

	@Test
	public void testPostMailWithDataAndOriginAndSmtpAuthIncompleteUser()
			throws MessagingException {
		PlainTextMailSender mailsender = new PlainTextMailSender();

		mailsender.setSmtpHost(TestConfiguration.getSmtpHost());
		// mailsender.setSmtpUsername(TestConfiguration.getSmtpUsername());
		mailsender.setSmtpPassword(TestConfiguration.getSmtpPassword());
		mailsender.setSmtpPort(TestConfiguration.getSmtpPort());

		mailsender.setMailFrom(TestConfiguration.getMailFrom());

		mailsender.setSmtpAuth(true);

		mailsender.setRecipient(TestConfiguration.getSpamAddress());
		mailsender.setSubject("JUnit-Test 4b");
		mailsender.setMessage("JUnit-Test 4b von Compuglobal");

		try {
			mailsender.sendMail();

			fail();
		} catch (AuthentificateDataIncompleteException e) {
		}
	}

	@Test
	public void testPostMailWithDataAndOriginAndSmtpAuthIncompletePwd()
			throws MessagingException {
		PlainTextMailSender mailsender = new PlainTextMailSender();

		mailsender.setSmtpHost(TestConfiguration.getSmtpHost());
		mailsender.setSmtpUsername(TestConfiguration.getSmtpUsername());
		// mailsender.setSmtpPassword(TestConfiguration.getSmtpPassword());
		mailsender.setSmtpPort(TestConfiguration.getSmtpPort());

		mailsender.setMailFrom(TestConfiguration.getMailFrom());

		mailsender.setSmtpAuth(true);

		mailsender.setRecipient(TestConfiguration.getSpamAddress());
		mailsender.setSubject("JUnit-Test 4c");
		mailsender.setMessage("JUnit-Test 4c von Compuglobal");

		try {
			mailsender.sendMail();

			fail();
		} catch (AuthentificateDataIncompleteException e) {
		}
	}

	@Test
	public void testPostMailWithDataAndOriginAndSmtpAuthComplete()
			throws MessagingException, AuthentificateDataIncompleteException {
		PlainTextMailSender mailsender = new PlainTextMailSender();

		mailsender.setSmtpHost(TestConfiguration.getSmtpHost());
		mailsender.setSmtpUsername(TestConfiguration.getSmtpUsername());
		mailsender.setSmtpPassword(TestConfiguration.getSmtpPassword());
		mailsender.setSmtpPort(TestConfiguration.getSmtpPort());

		mailsender.setMailFrom(TestConfiguration.getMailFrom());

		mailsender.setSmtpAuth(true);

		mailsender.setRecipient(TestConfiguration.getSpamAddress());
		mailsender.setSubject("JUnit-Test 4d");
		mailsender.setMessage("JUnit-Test 4d von Compuglobal");

		mailsender.sendMail();
	}
}
