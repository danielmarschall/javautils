package de.viathinksoft.utils.mail.address;

import static org.junit.Assert.*;

import java.net.IDN;

import org.junit.Test;

import de.viathinksoft.utils.mail.address.EMailAddress;

public class EMailAddressTest {
	
	private static final String ExamplePunycode = "xn--zckzah"; // Japanese IDN Test TLD
	private static final String ExampleUnicode = IDN.toUnicode(ExamplePunycode);
	
	@Test
	public void testAddressParsing() {
		try {
			new EMailAddress(null);
			fail();
		} catch (NullPointerException e) {
		}
		
		EMailAddress a;
		
		a = new EMailAddress("");
		assertEquals("", a.getLocalPart());
		// assertEquals("", a.getLocalPartASCII());
		assertEquals("", a.getDomainPartUnicode());
		assertEquals("", a.getDomainPartPunycode());
		assertEquals("", a.getTldUnicode());
		assertEquals("", a.getTldPunycode());
		assertEquals("", a.toString());
		assertEquals("", a.getMailAddressUnicode());
		assertEquals("", a.getMailAddressPunycodedDomain());

		a = new EMailAddress("bla");
		assertEquals("bla", a.getLocalPart());
		// assertEquals("", a.getLocalPartASCII());
		assertEquals("", a.getDomainPartUnicode());
		assertEquals("", a.getDomainPartPunycode());
		assertEquals("", a.getTldUnicode());
		assertEquals("", a.getTldPunycode());
		assertEquals("bla", a.toString());
		assertEquals("bla", a.getMailAddressUnicode());
		assertEquals("bla", a.getMailAddressPunycodedDomain());
		
		a = new EMailAddress(ExampleUnicode);
		assertEquals(ExampleUnicode, a.getLocalPart());
		// assertEquals("", a.getLocalPartASCII());
		assertEquals("", a.getDomainPartUnicode());
		assertEquals("", a.getDomainPartPunycode());
		assertEquals("", a.getTldUnicode());
		assertEquals("", a.getTldPunycode());
		assertEquals(ExampleUnicode, a.toString());
		assertEquals(ExampleUnicode, a.getMailAddressUnicode());
		assertEquals(ExampleUnicode, a.getMailAddressPunycodedDomain());

		a = new EMailAddress("@");
		assertEquals("", a.getLocalPart());
		// assertEquals("", a.getLocalPartASCII());
		assertEquals("", a.getDomainPartUnicode());
		assertEquals("", a.getDomainPartPunycode());
		assertEquals("", a.getTldUnicode());
		assertEquals("", a.getTldPunycode());
		assertEquals("", a.toString());
		assertEquals("", a.getMailAddressUnicode());
		assertEquals("", a.getMailAddressPunycodedDomain());

		a = new EMailAddress("local@domain");
		assertEquals("local", a.getLocalPart());
		// assertEquals("local", a.getLocalPartASCII());
		assertEquals("domain", a.getDomainPartUnicode());
		assertEquals("domain", a.getDomainPartPunycode());
		assertEquals("", a.getTldUnicode());
		assertEquals("", a.getTldPunycode());
		assertEquals("local@domain", a.toString());
		assertEquals("local@domain", a.getMailAddressUnicode());
		assertEquals("local@domain", a.getMailAddressPunycodedDomain());

		a = new EMailAddress("local@domain.tld");
		assertEquals("local", a.getLocalPart());
		// assertEquals("local", a.getlocalPartASCII());
		assertEquals("domain.tld", a.getDomainPartUnicode());
		assertEquals("domain.tld", a.getDomainPartPunycode());
		assertEquals("tld", a.getTldUnicode());
		assertEquals("tld", a.getTldPunycode());
		assertEquals("local@domain.tld", a.toString());
		assertEquals("local@domain.tld", a.getMailAddressUnicode());
		assertEquals("local@domain.tld", a.getMailAddressPunycodedDomain());
		
		a = new EMailAddress("local@"+ExampleUnicode+".jp");
		assertEquals("local", a.getLocalPart());
		// assertEquals("local", a.getlocalPartASCII());
		assertEquals(ExampleUnicode+".jp", a.getDomainPartUnicode());
		assertEquals(ExamplePunycode+".jp", a.getDomainPartPunycode());
		assertEquals("jp", a.getTldUnicode());
		assertEquals("jp", a.getTldPunycode());
		assertEquals("local@"+ExampleUnicode+".jp", a.getMailAddressUnicode());
		assertEquals("local@"+ExamplePunycode+".jp", a.getMailAddressPunycodedDomain());
		EMailAddress.USE_UNICODE_AS_STANDARD = true;
		assertEquals("local@"+ExampleUnicode+".jp", a.toString());
		EMailAddress.USE_UNICODE_AS_STANDARD = false;
		assertEquals("local@"+ExamplePunycode+".jp", a.toString());

		a = new EMailAddress("local@example."+ExampleUnicode);
		assertEquals("local", a.getLocalPart());
		// assertEquals("local", a.getlocalPartASCII());
		assertEquals("example."+ExampleUnicode, a.getDomainPartUnicode());
		assertEquals("example."+ExamplePunycode, a.getDomainPartPunycode());
		assertEquals(ExampleUnicode, a.getTldUnicode());
		assertEquals(ExamplePunycode, a.getTldPunycode());
		assertEquals("local@example."+ExampleUnicode, a.getMailAddressUnicode());
		assertEquals("local@example."+ExamplePunycode, a.getMailAddressPunycodedDomain());
		EMailAddress.USE_UNICODE_AS_STANDARD = true;
		assertEquals("local@example."+ExampleUnicode, a.toString());
		EMailAddress.USE_UNICODE_AS_STANDARD = false;
		assertEquals("local@example."+ExamplePunycode, a.toString());
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
	public void testClone() throws CloneNotSupportedException {
		EMailAddress a = new EMailAddress("local@example."+ExampleUnicode);
		EMailAddress b = (EMailAddress) a.clone();
		
		assertFalse(a == b);
		assertTrue(a.equals(b));
		assertTrue(b.equals(a));
		
		assertEquals(b.getDomainPartPunycode(), a.getDomainPartPunycode());
		assertEquals(b.getDomainPartUnicode(), a.getDomainPartUnicode());
		assertEquals(b.getLocalPart(), a.getLocalPart());
		assertEquals(b.getMailAddressPunycodedDomain(), a.getMailAddressPunycodedDomain());
		assertEquals(b.getMailAddressUnicode(), a.getMailAddressUnicode());
		assertEquals(b.getTldPunycode(), a.getTldPunycode());
		assertEquals(b.getTldUnicode(), a.getTldUnicode());
		EMailAddress.USE_UNICODE_AS_STANDARD = true;
		assertEquals(b.toString(), a.toString());
		EMailAddress.USE_UNICODE_AS_STANDARD = false;
		assertEquals(b.toString(), a.toString());
	}
	
	@Test
	public void testEquals() {
		EMailAddress a = new EMailAddress("local@example."+ExampleUnicode);
		EMailAddress b = new EMailAddress("local@example."+ExampleUnicode);
		
		assertFalse(a == b);
		assertTrue(a.equals(b));
		assertTrue(b.equals(a));
	}
}
