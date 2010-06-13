package de.viathinksoft.utils.mail;

import static org.junit.Assert.*;

import java.net.IDN;

import org.junit.Test;

public class EMailAddressTest {
	
	private static final String ExamplePunycode = "xn--zckzah"; // Japanese IDN Test TLD
	private static final String ExampleUnicode = IDN.toUnicode(ExamplePunycode);
	
	@Test
	public void testAddressParsing() throws InvalidMailAddressException {
		try {
			new EMailAddress(null);
			fail();
		} catch (InvalidMailAddressException e) {
		}
		
		try {
			new EMailAddress("");
			fail();
		} catch (InvalidMailAddressException e) {
		}
		
		try {
			new EMailAddress("bla");
			fail();
		} catch (InvalidMailAddressException e) {
		}
		
		EMailAddress a;
		
//		a = new EMailAddress("@");
//		assertEquals(a.getLocalPartUnicode(), "");
//		// assertEquals(a.getLocalPartASCII(), "");
//		assertEquals(a.getDomainPartUnicode(), "");
//		assertEquals(a.getDomainPartASCII(), "");
//		assertEquals(a.getTldUnicode(), "");
//		assertEquals(a.getTldASCII(), "");
//		assertEquals(a.getTldUnicode(), "");
//		assertEquals(a.getMailAddressASCII(), "");
		
		try {
			new EMailAddress("@");
			// Es wird InvalidMailAddressException anstelle von
			// local="" und domain="" ausgegeben,
			// weil .split nicht so wie .explode reagiert
			fail();
		} catch (InvalidMailAddressException e) {
		}

		a = new EMailAddress("local@domain");
		assertEquals(a.getLocalPart(), "local");
		// assertEquals(a.getLocalPartASCII(), "local");
		assertEquals(a.getDomainPartUnicode(), "domain");
		assertEquals(a.getDomainPartPunycode(), "domain");
		assertEquals(a.getTldUnicode(), "");
		assertEquals(a.getTldPunycode(), "");
		assertEquals(a.toString(), "local@domain");
		assertEquals(a.getMailAddressUnicode(), "local@domain");
		assertEquals(a.getMailAddressPunycodedDomain(), "local@domain");

		a = new EMailAddress("local@domain.tld");
		assertEquals(a.getLocalPart(), "local");
		// assertEquals(a.getlocalPartASCII(), "local");
		assertEquals(a.getDomainPartUnicode(), "domain.tld");
		assertEquals(a.getDomainPartPunycode(), "domain.tld");
		assertEquals(a.getTldUnicode(), "tld");
		assertEquals(a.getTldPunycode(), "tld");
		assertEquals(a.toString(), "local@domain.tld");
		assertEquals(a.getMailAddressUnicode(), "local@domain.tld");
		assertEquals(a.getMailAddressPunycodedDomain(), "local@domain.tld");
		
		a = new EMailAddress("local@"+ExampleUnicode+".jp");
		assertEquals(a.getLocalPart(), "local");
		// assertEquals(a.getlocalPartASCII(), "local");
		assertEquals(a.getDomainPartUnicode(), ExampleUnicode+".jp");
		assertEquals(a.getDomainPartPunycode(), ExamplePunycode+".jp");
		assertEquals(a.getTldUnicode(), "jp");
		assertEquals(a.getTldPunycode(), "jp");
		assertEquals(a.getMailAddressUnicode(), "local@"+ExampleUnicode+".jp");
		assertEquals(a.getMailAddressPunycodedDomain(), "local@"+ExamplePunycode+".jp");
		EMailAddress.USE_UNICODE_AS_STANDARD = true;
		assertEquals(a.toString(), "local@"+ExampleUnicode+".jp");
		EMailAddress.USE_UNICODE_AS_STANDARD = false;
		assertEquals(a.toString(), "local@"+ExamplePunycode+".jp");

		a = new EMailAddress("local@example."+ExampleUnicode);
		assertEquals(a.getLocalPart(), "local");
		// assertEquals(a.getlocalPartASCII(), "local");
		assertEquals(a.getDomainPartUnicode(), "example."+ExampleUnicode);
		assertEquals(a.getDomainPartPunycode(), "example."+ExamplePunycode);
		assertEquals(a.getTldUnicode(), ExampleUnicode);
		assertEquals(a.getTldPunycode(), ExamplePunycode);
		assertEquals(a.getMailAddressUnicode(), "local@example."+ExampleUnicode);
		assertEquals(a.getMailAddressPunycodedDomain(), "local@example."+ExamplePunycode);
		EMailAddress.USE_UNICODE_AS_STANDARD = true;
		assertEquals(a.toString(), "local@example."+ExampleUnicode);
		EMailAddress.USE_UNICODE_AS_STANDARD = false;
		assertEquals(a.toString(), "local@example."+ExamplePunycode);
	}
	
	@Test
	public void testIsUnicode() {
		assertFalse(EMailAddress.isUnicode(null));
		assertFalse(EMailAddress.isUnicode(""));
		assertFalse(EMailAddress.isUnicode(ExamplePunycode));
		assertTrue(EMailAddress.isUnicode(ExampleUnicode));
	}
	
	@Test
	public void testIsPunycode() {
		assertFalse(EMailAddress.isPunycode(null));
		assertFalse(EMailAddress.isPunycode(""));
		assertTrue(EMailAddress.isPunycode(ExamplePunycode));
		assertFalse(EMailAddress.isPunycode(ExampleUnicode));
	}
	
	@Test
	public void testClone() throws InvalidMailAddressException, CloneNotSupportedException {
		EMailAddress a = new EMailAddress("local@example."+ExampleUnicode);
		EMailAddress b = (EMailAddress) a.clone();
		
		assertFalse(a == b);
		assertTrue(a.equals(b));
		assertTrue(b.equals(a));
		
		assertEquals(a.getDomainPartPunycode(), b.getDomainPartPunycode());
		assertEquals(a.getDomainPartUnicode(), b.getDomainPartUnicode());
		assertEquals(a.getLocalPart(), b.getLocalPart());
		assertEquals(a.getMailAddressPunycodedDomain(), b.getMailAddressPunycodedDomain());
		assertEquals(a.getMailAddressUnicode(), b.getMailAddressUnicode());
		assertEquals(a.getTldPunycode(), b.getTldPunycode());
		assertEquals(a.getTldUnicode(), b.getTldUnicode());
		EMailAddress.USE_UNICODE_AS_STANDARD = true;
		assertEquals(a.toString(), b.toString());
		EMailAddress.USE_UNICODE_AS_STANDARD = false;
		assertEquals(a.toString(), b.toString());
	}
	
	@Test
	public void testEquals() throws InvalidMailAddressException {
		EMailAddress a = new EMailAddress("local@example."+ExampleUnicode);
		EMailAddress b = new EMailAddress("local@example."+ExampleUnicode);
		
		assertFalse(a == b);
		assertTrue(a.equals(b));
		assertTrue(b.equals(a));
	}
}
