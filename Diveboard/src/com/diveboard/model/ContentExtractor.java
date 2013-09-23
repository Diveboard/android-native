package com.diveboard.model;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;

/*
 * Class ContentExtractor
 * Toolbox for extracting content from HTTP requests
 */
public class					ContentExtractor
{
	/*
	 * Method getASCII
	 * Convert HttpEntity content into string.
	 */
	static public String		getASCII(final HttpEntity entity) throws IllegalStateException, IOException
	{
		InputStream	in = entity.getContent();
    	StringBuffer out = new StringBuffer();
    	int n = 1;
    	byte[] b = new byte[4096];
    	
    	while (n > 0)
    	{
    		n = in.read(b);
    		if (n > 0)
	    		out.append(new String(b, 0, n));
    	}
		return (out.toString());
	}
}
