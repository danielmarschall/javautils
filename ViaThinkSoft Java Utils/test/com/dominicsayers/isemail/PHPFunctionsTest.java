package com.dominicsayers.isemail;

import static org.junit.Assert.*;

import org.junit.Test;

public class PHPFunctionsTest {

	@Test
	public void preg_match_allTest() {
		String[][] a = PHPFunctions.preg_match_all("hell(.)w(.)rld",
				"helloworld, hellawirld, hellaworle, ...");
		
		assertEquals(3, a.length);
		assertEquals(2, a[0].length);
		assertEquals("helloworld", a[0][0]);
		assertEquals("hellawirld", a[0][1]);
		assertEquals(2, a[1].length);
		assertEquals("o", a[1][0]);
		assertEquals("a", a[1][1]);
		assertEquals(2, a[2].length);
		assertEquals("o", a[2][0]);
		assertEquals("i", a[2][1]);
	}
	
	@Test
	public void preg_matchTest() {
		
		assertEquals(1, PHPFunctions.preg_match("(h|e)(l|o)", "hl"));
		assertEquals(1, PHPFunctions.preg_match("(h|e)(l|o)", "eo"));
		assertEquals(1, PHPFunctions.preg_match("(h|e)(l|o)", "eol"));
		assertEquals(1, PHPFunctions.preg_match("(h|e)(l|o)", "hol"));
		assertEquals(1, PHPFunctions.preg_match("(h|e)(l|o)", "hoho"));
		assertEquals(0, PHPFunctions.preg_match("(h|e)(l|o)", "iee"));
		
	}
	
	@Test
	public void preg_splitTest() {
		String[] s = PHPFunctions.preg_split("[\\s,]+", "hypertext language, programming");
		
		assertEquals(3, s.length);
		assertEquals("hypertext", s[0]);
		assertEquals("language", s[1]);
		assertEquals("programming", s[2]);
		
		s = PHPFunctions.preg_split("\\.", ".hello.");
		
		assertEquals(3, s.length);
		assertEquals("", s[0]);
		assertEquals("hello", s[1]);
		assertEquals("", s[2]);

	}
	
	@Test
	public void preg_match_to_arrayTest() {
		String[] s = PHPFunctions.preg_match_to_array("(?i)^(?:http://)?([^/]+)", "http://www.php.net/index.html");
		
		assertEquals(2, s.length);
		assertEquals("http://www.php.net", s[0]);
		assertEquals("www.php.net", s[1]);
		
		s = PHPFunctions.preg_match_to_array("t(.st)", "test tost taste toast");
		
		assertEquals(2, s.length);
		assertEquals("test", s[0]);
		assertEquals("est", s[1]);
	}
	
	@Test
	public void substr1Test() {
		assertEquals("3456789", PHPFunctions.substr("123456789", 2));
	}

	@Test
	public void substr2Test() {
		assertEquals("345", PHPFunctions.substr("123456789", 2, 3));
	}
	
	@Test
	public void preg_repalceTest() {		
		String zeichenkette = "Der schnelle braune Fuchs sprang über den faulen Hund.";
		
		zeichenkette = PHPFunctions.preg_replace("schnelle", "langsame", zeichenkette);
		zeichenkette = PHPFunctions.preg_replace("braune", "schwarze", zeichenkette);
		zeichenkette = PHPFunctions.preg_replace("Fuchs", "Bär", zeichenkette);
		
		assertEquals("Der langsame schwarze Bär sprang über den faulen Hund.", zeichenkette);
	}

}
