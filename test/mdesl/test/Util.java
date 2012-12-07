package mdesl.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Util {
	private static final Random RND = new Random();
    private static ResourceLocator resourceLocator = new DefaultResourceLocator();
    
    public static float rnd() {
    	return rndFloat();
    }
    
    public static int rndInt() {
    	return RND.nextInt();
    }
    
    public static int rnd(int high) {
    	return RND.nextInt(high);
    }
    
    public static float rndFloat() {
    	return RND.nextFloat();
    }
    
    public static int rnd(int low, int high) {
        if (low==high)
            return low;
        return RND.nextInt(high - low) + low;
    }
    
    public static float rnd(float low, float high) {
        if (low==high)
            return low;
        return low + (RND.nextFloat() * (high - low));
    }
    
    public static void log(String msg) {
    	getLogger().log(Level.INFO, msg);
    }
    
    public static void warn(String msg) {
    	getLogger().log(Level.WARNING, msg);
    }
    
    public static void error(String msg) {
    	error(msg, null);
    }
    
    public static Logger getLogger() {
    	return Logger.getLogger(Util.class.getName());
    }
    
    public static void error(String msg, Throwable t) {
    	if (t!=null) getLogger().log(Level.SEVERE, msg, t);
    	else getLogger().log(Level.SEVERE, msg);
    }
    

	/**
	 * Loads the given input stream into a source code string.
	 * @param in the input stream
	 * @return the resulting source code String 
	 * @throws SlimException if there was an issue reading the source
	 * @author Nitram
	 */
    public static String readFile(InputStream in) throws IOException {
		final StringBuffer sBuffer = new StringBuffer();
		final BufferedReader br = new BufferedReader(new InputStreamReader(in));
		final char[] buffer = new char[1024];

		int cnt;
		while ((cnt = br.read(buffer, 0, buffer.length)) > -1) {
			sBuffer.append(buffer, 0, cnt);
		}
		br.close();
		in.close();
		return sBuffer.toString();
	}
    
    public static URL getResource(String str) {
    	URL u = getResourceLocator().getResource(str);
    	return u;
    }
    
    public static InputStream getResourceAsStream(String str) {
    	InputStream in = getResourceLocator().getResourceAsStream(str);
    	return in;
    }
    
    public static void setResourceLocator(ResourceLocator r) {
    	resourceLocator = r;
    }
    
    public static ResourceLocator getResourceLocator() {
    	return resourceLocator;
    }
	
    public static final class DefaultResourceLocator implements ResourceLocator {

    	public static final File ROOT = new File(".");
    	
        private static File createFile(String ref) {
            File file = new File(ROOT, ref);
            if (!file.exists()) {
                file = new File(ref);
            }
            
            return file;
        }
        
	    public InputStream getResourceAsStream(String ref) {
	        InputStream in = Util.class.getClassLoader().getResourceAsStream(ref);
	        if (in==null) { // try file system
	            try { return new FileInputStream(createFile(ref)); }
	            catch (IOException e) {}
	        }
	        return in;
	    }
	    
	    public URL getResource(String ref) {
	        URL url = Util.class.getClassLoader().getResource(ref);
	        if (url==null) {
	            try { 
	                File f = createFile(ref);
	                if (f.exists())
	                    return f.toURI().toURL();
	            } catch (IOException e) {}
	        }
	        return url;
	    }
    }
    
    public static interface ResourceLocator {
    	public URL getResource(String str);
    	public InputStream getResourceAsStream(String str);
    }
}
