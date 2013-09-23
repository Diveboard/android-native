package com.diveboard.model;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;

public class					ContentExtractor
{
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
