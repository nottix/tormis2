/**
 * @author 
 */
package mis2.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class PropertiesReader {
	private static Properties properties = null;
	private static File file = null;
	private static FileInputStream fis = null;
	private static String ret = null;
	
	public static String getProperty(String id) {
		try {
			if(id=="" || id==null)
				return null;
			if(file==null) {
				file = new File(System.getProperty("user.dir")+"/data/parameters");
				fis = new FileInputStream(file);
			}
			if(properties==null) {
				properties = new Properties();
				properties.load(fis);
				fis.close();
			}

			if((ret=properties.getProperty(id))!=null)
				return ret;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static float getValue(String id) {
		return Float.parseFloat(getProperty(id));
	}

}
