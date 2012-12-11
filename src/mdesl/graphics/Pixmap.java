package mdesl.graphics;

import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;

/**
 * A simple wrapper around an array of RGBA pixel colors, which can then be passed to a GL texture.
 * @author davedes
 */
public class Pixmap {
	
	//if we were targeting Android, we might prefer to use byte[] arrays for performance
	/** The RGBA bytes backing this pixmap. */
	protected ByteBuffer pixels;
	
	public static final int BYTES_PER_PIXEL = 4;
	
	public Pixmap(int width, int height) {
		pixels = BufferUtils.createByteBuffer(width * height * BYTES_PER_PIXEL);
	}
	
	/**
     * Sets the pixel data to the given array, which should be less
     * than the size of length(), then flips the buffer.
     * 
     * @param rgbaData the new pixel data
     * @return this object, for chaining
     */
	public Pixmap set(byte[] rgbaData) {
		pixels.clear();
		pixels.put(rgbaData);
		pixels.flip();
		return this;
	}
	
	/**
     * Clears the pixel array to transparent black.
     * @return this object, for chaining
     */
    public Pixmap clear() {
            return clear(0, 0, 0, 0);
    }
   
    /**
     * Clears the pixel array to the specified color, then flips the buffer.
     * 
     * @param r the red byte
     * @param g the green byte
     * @param b the blue byte
     * @param a the alpha byte
     * @return this object, for chaining
     */
    public Pixmap clear(int r, int g, int b, int a) {
            pixels.clear();
            for (int i=0; i<pixels.capacity(); i++) 
            	pixels.put((byte)r).put((byte)g).put((byte)b).put((byte)a);
            pixels.flip();
            return this;
    }
    
    // TODO -- this class is a WIP
}
