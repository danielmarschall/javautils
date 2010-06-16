package de.viathinksoft.utils.mail.address;

import static org.junit.Assert.*;

import java.net.IDN;

import org.junit.Test;

public class EMailPreprocessorTest {

	private static final String ExamplePunycode = "xn--zckzah"; // Japanese IDN
																// Test TLD
	private static final String ExampleUnicode = IDN.toUnicode(ExamplePunycode);

	@Test
	public void preprocessTrimTest() {
		// Check that trim() works
		assertEquals(
				"test@test.de",
				EMailPreprocessor
						.preprocess("  \t \n\t\n\r test@test.de    \t  \n\r\n \r       "));
	}

	@Test
	public void preprocessIDNTest() {
		// Check that IDN addresses are decoded
		assertEquals("test@" + ExamplePunycode + "." + ExamplePunycode,
				EMailPreprocessor.preprocess("  \t \n\t\n\r test@"
						+ ExampleUnicode + "." + ExampleUnicode
						+ "    \t  \n\r\n \r       "));
	}

}
