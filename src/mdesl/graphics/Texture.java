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

import static org.lwjgl.opengl.GL11.GL_CLAMP;
import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_LINEAR_MIPMAP_LINEAR;
import static org.lwjgl.opengl.GL11.GL_LINEAR_MIPMAP_NEAREST;
import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_NEAREST_MIPMAP_LINEAR;
import static org.lwjgl.opengl.GL11.GL_NEAREST_MIPMAP_NEAREST;
import static org.lwjgl.opengl.GL11.GL_PACK_ALIGNMENT;
import static org.lwjgl.opengl.GL11.GL_REPEAT;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.GL_UNPACK_ALIGNMENT;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glDeleteTextures;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glPixelStorei;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL11.glTexSubImage2D;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;

import de.matthiasmann.twl.utils.PNGDecoder;

/** This is a minimal implementation of an OpenGL texture loader. A more complete
 * implementation would support multiple filetypes (JPEG, BMP, TGA, etc), allow
 * for parameters such as width/height to be changed (by uploading new texture
 * data), allow for different internal formats, async loading, different targets
 * (i.e. for GL_TEXTURE_3D or array textures), mipmaps, compression, etc.
 * 
 * @author davedes */
public class Texture implements ITexture {

	protected int id;
	protected int width;
	protected int height;
	
	public static int toPowerOfTwo(int n) {
        return 1 << (32 - Integer.numberOfLeadingZeros(n-1));
    }
    
    public static boolean isPowerOfTwo(int n) {
        return (n & -n) == n;
    }
    
    public static boolean isNPOTSupported() {
        return GLContext.getCapabilities().GL_ARB_texture_non_power_of_two;
    }

	// Some filters, included here for convenience
	public static final int LINEAR = GL_LINEAR;
	public static final int NEAREST = GL_NEAREST;
	public static final int LINEAR_MIPMAP_LINEAR = GL_LINEAR_MIPMAP_LINEAR;
	public static final int LINEAR_MIPMAP_NEAREST = GL_LINEAR_MIPMAP_NEAREST;
	public static final int NEAREST_MIPMAP_NEAREST = GL_NEAREST_MIPMAP_NEAREST;
	public static final int NEAREST_MIPMAP_LINEAR = GL_NEAREST_MIPMAP_LINEAR;
	
	// Some wrap modes, included here for convenience
	public static final int CLAMP = GL_CLAMP;
	public static final int CLAMP_TO_EDGE = GL_CLAMP_TO_EDGE;
	public static final int REPEAT = GL_REPEAT;
	
	public static final int DEFAULT_FILTER = NEAREST;
	public static final int DEFAULT_WRAP = REPEAT;
	
	protected Texture() {
		//does nothing... for subclasses
	}
	
	/** Creates an empty OpenGL texture with the given width and height, where
	 * each pixel is transparent black (0, 0, 0, 0) and the wrap mode is
	 * CLAMP_TO_EDGE and the filter is NEAREST.
	 * 
	 * @param width the width of the texture
	 * @param height the height of the texture */
	public Texture(int width, int height) {
		this(width, height, DEFAULT_FILTER);
	}

	/** Creates an empty OpenGL texture with the given width and height, where
	 * each pixel is transparent black (0, 0, 0, 0) and the wrap mode is
	 * CLAMP_TO_EDGE.
	 * 
	 * @param width the width of the texture
	 * @param height the height of the texture
	 * @param filter the filter to use */
	public Texture(int width, int height, int filter) {
		this(width, height, filter, DEFAULT_WRAP);
	}
	
	/** Creates an empty OpenGL texture with the given width and height, where
	 * each pixel is transparent black (0, 0, 0, 0).
	 * 
	 * @param width the width of the texture
	 * @param height the height of the texture
	 * @param minFilter the minification filter to use
	 * @param magFilter the magnification filter to use
	 * @param wrap the wrap mode to use
	 * @param genMipmaps - whether to generate mipmaps, which requires 
	 * 		GL_EXT_framebuffer_object (or GL3+) */
	public Texture(int width, int height, int filter, int wrap) {
		glEnable(getTarget());
		id = glGenTextures();
		this.width = width;
		this.height = height;
		bind();
		
		setFilter(filter);
		setWrap(wrap);
		
		ByteBuffer buf = BufferUtils.createByteBuffer(width * height * 4);
		upload(GL_RGBA, buf);
	}

	public Texture(URL pngRef) throws IOException {
		this(pngRef, DEFAULT_FILTER);
	}

	public Texture(URL pngRef, int filter) throws IOException {
		this(pngRef, filter, DEFAULT_WRAP);
	}
	
	public Texture(URL pngRef, int filter, int wrap) throws IOException {
		this(pngRef, filter, filter, wrap, false);
	}
	
	public Texture(URL pngRef, int filter, boolean genMipmap) throws IOException {
		this(pngRef, filter, filter, DEFAULT_WRAP, genMipmap);
	}

	public Texture(URL pngRef, int minFilter, int magFilter, int wrap,
			boolean genMipmap) throws IOException {
		//TODO: npot check
		InputStream input = null;
		try {
			input = pngRef.openStream();
			PNGDecoder dec = new PNGDecoder(input);
			
			width = dec.getWidth();
			height = dec.getHeight();
			ByteBuffer buf = BufferUtils.createByteBuffer(4 * width * height);
			dec.decode(buf, width * 4, PNGDecoder.Format.RGBA);
			buf.flip();
			
			glEnable(getTarget());
			id = glGenTextures();

			bind();
			setFilter(minFilter, magFilter);
			setWrap(wrap);
			upload(GL_RGBA, buf);
			
			//use EXT since we are targeting 2.0+
			if (genMipmap) {
				EXTFramebufferObject.glGenerateMipmapEXT(getTarget());
			}
		} finally {
			if (input != null) {
				try {
					input.close();					
				} catch (IOException e) {
				}
			}
		}
	}
	
	public int getTarget() {
		return GL_TEXTURE_2D;
	}
	
	public int getID() {
		return id;
	}

	protected void setUnpackAlignment() {
		glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
		glPixelStorei(GL_PACK_ALIGNMENT, 1);
	}

	/** Uploads image data with the dimensions of this Texture.
	 * 
	 * @param dataFormat the format, e.g. GL_RGBA
	 * @param data the byte data */
	public void upload(int dataFormat, ByteBuffer data) {
		bind();
		setUnpackAlignment();
		glTexImage2D(getTarget(), 0, GL_RGBA, width, height, 0, dataFormat, GL_UNSIGNED_BYTE, data);
	}

	/** Uploads a sub-image within this texture.
	 * 
	 * @param x the destination x offset
	 * @param y the destination y offset, with lower-left origin
	 * @param width the width of the sub image data
	 * @param height the height of the sub image data
	 * @param dataFormat the format of the sub image data, e.g. GL_RGBA
	 * @param data the sub image data */
	public void upload(int x, int y, int width, int height, int dataFormat, ByteBuffer data) {
		bind();
		setUnpackAlignment();
		glTexSubImage2D(getTarget(), 0, x, y, width, height, dataFormat, GL_UNSIGNED_BYTE, data);
	}

	public void setFilter(int filter) {
		setFilter(filter, filter);
	}
	
	public void setFilter(int minFilter, int magFilter) {
		bind();
		glTexParameteri(getTarget(), GL_TEXTURE_MIN_FILTER, minFilter);
		glTexParameteri(getTarget(), GL_TEXTURE_MAG_FILTER, magFilter);
	}
	
	public void setWrap(int wrap) {
		bind();
		glTexParameteri(getTarget(), GL_TEXTURE_WRAP_S, wrap);
		glTexParameteri(getTarget(), GL_TEXTURE_WRAP_T, wrap);
	}

	public void bind() {
		if (!valid())
			throw new IllegalStateException("trying to bind a texture that was disposed");
		glBindTexture(getTarget(), id);
	}
	
	public void dispose() {
		if (valid()) {
			glDeleteTextures(id);
			id = 0;
		}
	}
	
	/**
	 * Returns true if this texture is valid, aka it has not been disposed.
	 * @return true if id!=0
	 */
	public boolean valid() {
		return id!=0;
	}

	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	/** Returns this object; used for abstraction with SpriteBatch.
	 * @return this texture object */
	public Texture getTexture() {
		return this;
	}

	@Override
	public float getU() {
		return 0f;
	}

	@Override
	public float getV() {
		return 0f;
	}

	@Override
	public float getU2() {
		return 1f;
	}

	@Override
	public float getV2() {
		return 1f;
	}
}