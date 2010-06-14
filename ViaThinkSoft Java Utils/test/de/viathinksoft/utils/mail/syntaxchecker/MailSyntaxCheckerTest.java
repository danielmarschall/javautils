package de.viathinksoft.utils.mail.syntaxchecker;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import de.viathinksoft.utils.mail.EMailAddress;

public class MailSyntaxCheckerTest {
	
	static int errorCount;
	
	private void checkXML(String xmlFile) throws ParserConfigurationException, SAXException, IOException {
		File file = new File(xmlFile);
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(file);
		doc.getDocumentElement().normalize();
		NodeList nodeLst = doc.getElementsByTagName("test");
		
		for (int s = 0; s < nodeLst.getLength(); s++) {

			Node fstNode = nodeLst.item(s);

			if (fstNode.getNodeType() == Node.ELEMENT_NODE) {

				Element fstElmnt = (Element) fstNode;

				String id;
				String address;
				boolean expected;

				NodeList fstNmElmntLst;
				Element fstNmElmnt;
				NodeList fstNm;
				String cont;

				fstNmElmntLst = fstElmnt.getElementsByTagName("address");
				fstNmElmnt = (Element) fstNmElmntLst.item(0);
				fstNm = fstNmElmnt.getChildNodes();
				try {
					cont = ((Node) fstNm.item(0)).getNodeValue();
				} catch (NullPointerException e) {
					cont = "";
				}
				address = cont;

				fstNmElmntLst = fstElmnt.getElementsByTagName("valid");
				fstNmElmnt = (Element) fstNmElmntLst.item(0);
				fstNm = fstNmElmnt.getChildNodes();
				cont = ((Node) fstNm.item(0)).getNodeValue();
				expected = Boolean.parseBoolean(cont);

				fstNmElmntLst = fstElmnt.getElementsByTagName("id");
				fstNmElmnt = (Element) fstNmElmntLst.item(0);
				fstNm = fstNmElmnt.getChildNodes();
				cont = ((Node) fstNm.item(0)).getNodeValue();
				id = cont;

				boolean actual = MailSyntaxChecker.isMailValid(address);

				// assertEquals(expected, actual);
				if (expected != actual) {
					System.err.println("Mail Test #" + id + " FAILED! '"
							+ address + "' is '" + actual + "' instead of '"
							+ expected + "'!");
					errorCount++;
				}
			}
		}
	}

	@Test
	public void performXMLTests() throws SAXException, IOException,
			ParserConfigurationException {

		// First: Null-Pointer Test 
		
		try {
			MailSyntaxChecker.isMailValid((String)null);
			fail();
		} catch (NullPointerException e) {
		}

		try {
			MailSyntaxChecker.isMailValid((EMailAddress)null);
			fail();
		} catch (NullPointerException e) {
		}
		
		// Now check the XML testcases
		
		checkXML("test/eMailTests/SayersTests.xml");
		checkXML("test/eMailTests/emailExperimentalTests.xml");

		if (errorCount > 0) {
			System.err.println("==> " + errorCount + " ERRORS OCCOURED! <==");
			fail();
		}
	}
}
