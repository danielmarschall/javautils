package com.dominicsayers.isemail;

import static org.junit.Assert.*;

import javax.naming.NamingException;

import org.junit.Test;

public class MXLookupTest {

	@Test
	public void doLookupTest() throws NamingException {

		// Achtung! Diese Testdaten können sich jederzeit ändern!

		// Host='' ist möglicherweise der localhost...
		assertEquals(0, DNSLookup.doLookup("", DNSType.MX));
		// try {
		// MXLookup.doLookup("");
		// fail();
		// } catch (NamingException e) {
		// }

		try {
			DNSLookup.doLookup(null, DNSType.MX);
			fail();
		} catch (NullPointerException e) {
			// Wir erwarten eine NullPointerException
		}

		try {
			DNSLookup.doLookup("invalid", DNSType.MX); // Invalid defined TLD
			fail();
		} catch (NamingException e) {
		}

		try {
			DNSLookup.doLookup("viathinkksoft.de", DNSType.MX); // Domain not
																// assigned
			fail();
		} catch (NamingException e) {
		}

		try {
			DNSLookup.doLookup("yahoo.ccc", DNSType.MX); // Invalid TLD
			fail();
		} catch (NamingException e) {
		}

		assertEquals(4, DNSLookup.doLookup("google.de", DNSType.MX));
		assertEquals(2, DNSLookup.doLookup("yahoo.de", DNSType.MX));
		assertEquals(2, DNSLookup.doLookup("example.de", DNSType.MX));
		assertEquals(0, DNSLookup.doLookup("example.com", DNSType.MX));
		assertEquals(1, DNSLookup.doLookup("ai", DNSType.MX));
		assertEquals(4, DNSLookup.doLookup("whitehouse.gov", DNSType.MX));
	}
}
