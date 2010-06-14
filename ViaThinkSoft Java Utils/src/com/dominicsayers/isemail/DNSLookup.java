package com.dominicsayers.isemail;

import java.util.Hashtable;
import javax.naming.*;
import javax.naming.directory.*;

/**
 * This class performs DNS lookups.
 * 
 * @author Rgagnon.com and Daniel Marschall
 * @see Source: http://www.rgagnon.com/javadetails/java-0452.html (Modified)
 * @version 2010-06-14
 */
public class DNSLookup {
	public static int doLookup(String hostName, DNSType type)
			throws NamingException {
		Hashtable<String, String> env = new Hashtable<String, String>();
		env.put("java.naming.factory.initial",
				"com.sun.jndi.dns.DnsContextFactory");
		DirContext ictx = new InitialDirContext(env);
		Attributes attrs = ictx.getAttributes(hostName, new String[] { type
				.toString() });
		Attribute attr = attrs.get(type.toString());
		if (attr == null) {
			return 0;
		}
		return attr.size();
	}

	private DNSLookup() {
	}
}