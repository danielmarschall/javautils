package de.viathinksoft.utils.http;

import java.io.IOException;
import java.util.Random;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * This factory produces a HttpUtil instance with faked user agents.
 * The instance is only created once. 
 * @author Daniel Marschall
 */

public class SecretHttpFactory {

	static HttpUtils instance;
	static String userAgent;

	public static HttpUtils getInstance() {
		return instance;
	}
	
	public static String getUserAgent() {
		return userAgent;
	}

	private SecretHttpFactory() {
	}

	private static String getRandomUserAgent() {
		String userAgent = null;

		try {
			// Newest version here: http://www.user-agents.org/allagents.xml
			
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();

//			File file = new File(
//			"src/de/viathinksoft/utils/http/allagents.xml");
//			Document doc = db.parse(file);
			Document doc = db.parse(SecretHttpFactory.class.getResourceAsStream("allagents.xml"));
			doc.getDocumentElement().normalize();
			NodeList nodeLst = doc.getElementsByTagName("user-agent");
			
			if (nodeLst.getLength() == 0) {
				userAgent = null;
			} else {
				Random random = new Random();
				int s = random.nextInt(nodeLst.getLength());

				Node fstNode = nodeLst.item(s);

				if (fstNode.getNodeType() == Node.ELEMENT_NODE) {

					Element fstElmnt = (Element) fstNode;

					NodeList fstNmElmntLst = fstElmnt
							.getElementsByTagName("String");
					Element fstNmElmnt = (Element) fstNmElmntLst.item(0);
					NodeList fstNm = fstNmElmnt.getChildNodes();
					userAgent = ((Node) fstNm.item(0)).getNodeValue();
				}
			}
		} catch (ParserConfigurationException e) {
			userAgent = null;
		} catch (SAXException e) {
			userAgent = null;
		} catch (IOException e) {
			e.printStackTrace();
			userAgent = null;
		}

		return userAgent;
	}

	static {
		userAgent = getRandomUserAgent();
		if (userAgent == null) {
			instance = new HttpUtils();
		} else {
			instance = new HttpUtils(userAgent);
		}
	}

}
