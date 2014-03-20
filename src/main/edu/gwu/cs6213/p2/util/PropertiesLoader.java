package edu.gwu.cs6213.p2.util;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


/**
 * 
 * @author Marco Yeung
 *
 */
public class PropertiesLoader {

	public static final String FLOWCONTROL_PROPERTY_FILE = "project2.property";


	
	private final static PropertiesParser  INSTANCE;
	static{
		INSTANCE = loadProperties();
	}
	
	public static PropertiesParser getPropertyInstance(){
		return INSTANCE;
				
	}
	
	private static PropertiesParser loadProperties() {

		String requestedFile = System.getProperty(FLOWCONTROL_PROPERTY_FILE);
		String propFileName = requestedFile != null ? requestedFile
				: "project2.property";

		Properties props = new Properties();
		InputStream in = null;
		try {
			in = new BufferedInputStream(new FileInputStream(propFileName));
			props.load(in);
		} catch (Exception e) {
			System.out.println("unable to load project property file."  + e.getMessage());
			return null;
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException ignore) { /* ignore */
				}
			}
		}

		return new PropertiesParser(props);
	}
}
