/**
 * Copyright (c) 2012, Matt DesLauriers All rights reserved.
 *
 *	Redistribution and use in source and binary forms, with or without
 *	modification, are permitted provided that the following conditions are met: 
 *
 *	* Redistributions of source code must retain the above copyright notice, this
 *	  list of conditions and the following disclaimer. 
 *
 *	* Redistributions in binary
 *	  form must reproduce the above copyright notice, this list of conditions and
 *	  the following disclaimer in the documentation and/or other materials provided
 *	  with the distribution. 
 *
 *	* Neither the name of the Matt DesLauriers nor the names
 *	  of his contributors may be used to endorse or promote products derived from
 *	  this software without specific prior written permission.
 *
 *	THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 *	AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 *	IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 *	ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 *	LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 *	CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 *	SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 *	INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 *	CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 *	ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 *	POSSIBILITY OF SUCH DAMAGE.
 */
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
