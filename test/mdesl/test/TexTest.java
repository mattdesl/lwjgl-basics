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
package mdesl.test;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_CLAMP;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH;
import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_PACK_ALIGNMENT;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_REPEAT;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.GL_UNPACK_ALIGNMENT;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glOrtho;
import static org.lwjgl.opengl.GL11.glPixelStorei;
import static org.lwjgl.opengl.GL11.glTexCoord2f;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL11.glVertex2f;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GLContext;

import de.matthiasmann.twl.utils.PNGDecoder;


public class TexTest {
	
	public static void main(String[] args) throws LWJGLException {		
		final int WIDTH = 800, HEIGHT = 600;
		Display.setDisplayMode(new DisplayMode(WIDTH, HEIGHT));
		Display.create();
		
		
		Texture tex = null, tex2 = null;
		try {
			//TODO: put this into a texture atlas!
			tex = new Texture(TexTest.class.getClassLoader().getResource("res/tiles.png"), Texture.NEAREST, Texture.CLAMP_TO_EDGE);
			tex2 = new Texture(TexTest.class.getClassLoader().getResource("res/grass.png"));
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}
		
		glViewport(0, 0, WIDTH, HEIGHT);
		
		//ugly deprecated OpenGL!!! use programmable pipeline instead
		
		//setup blending
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		
		//enable textures
		glEnable(GL_TEXTURE_2D);
		
		glClearColor(1f, 1f, 1f, 1f);
		glDisable(GL_DEPTH);
		
		
		while (!Display.isCloseRequested()) {
			//clear the screen
			glClear(GL_COLOR_BUFFER_BIT);
			
			debugTexture(tex, 50, 50, 64, 64);
//			drawSprite(tex2, 150, 150, 1f);
			
			
			Display.update();
			Display.sync(60);
		}
		Display.destroy();
	}
	
	public static void debugTexture(Texture tex, float x, float y, float width, float height) {
		//in a typical OpenGL game, this would be done during initialization rather than within the game loop
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glOrtho(0, Display.getWidth(), Display.getHeight(), 0, 1, -1);
		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();
		
		tex.bind();
		
		float srcX = 64;
		float srcY = 0;
		float srcWidth = 64;
		float srcHeight = 64;
		
		float u = srcX / tex.width;
		float v = srcY / tex.height;
		float u2 = (srcX + srcWidth) / tex.width;
		float v2 = (srcY + srcHeight) / tex.height;
		
		//immediate mode is deprecated -- we are only using it for quick debugging
		glColor4f(1f, 1f, 1f, 1f);
		glBegin(GL_QUADS);
			glTexCoord2f(u, v);
			glVertex2f(x, y);
			glTexCoord2f(u, v2);
			glVertex2f(x, y + height);
			glTexCoord2f(u2, v2);
			glVertex2f(x + width, y + height);
			glTexCoord2f(u2, v);
			glVertex2f(x + width, y);
		glEnd();
	}

	//put this in its own file rather than having it as an inner class
	public static class Texture {
		static int bound = 0;
	
		public final int target = GL_TEXTURE_2D;
		public final int id;
		public final int width;
		public final int height;
	
		public static final int LINEAR = GL_LINEAR;
		public static final int NEAREST = GL_NEAREST;
	
		public static final int CLAMP = GL_CLAMP;
		public static final int CLAMP_TO_EDGE = GL_CLAMP_TO_EDGE;
		public static final int REPEAT = GL_REPEAT;
	
		public static void clearLastBind() {
			bound = 0;
		}
	
		public Texture(URL pngRef) throws IOException {
			this(pngRef, GL_NEAREST);
		}
	
		public Texture(URL pngRef, int filter) throws IOException {
			this(pngRef, filter, GL_CLAMP_TO_EDGE);
		}
	
		public Texture(URL pngRef, int filter, int wrap) throws IOException {
			InputStream input = null;
			try {
				input = pngRef.openStream();
				PNGDecoder dec = new PNGDecoder(input);
	
				width = dec.getWidth();
				height = dec.getHeight();
				ByteBuffer buf = BufferUtils.createByteBuffer(4 * width * height);
				dec.decode(buf, width * 4, PNGDecoder.Format.RGBA);
				buf.flip();
	
				glEnable(target);
				id = glGenTextures();
	
				bind();
	
				glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
				glPixelStorei(GL_PACK_ALIGNMENT, 1);
	
				glTexParameteri(target, GL_TEXTURE_MIN_FILTER, filter);
				glTexParameteri(target, GL_TEXTURE_MAG_FILTER, filter);
				glTexParameteri(target, GL_TEXTURE_WRAP_S, wrap);
				glTexParameteri(target, GL_TEXTURE_WRAP_T, wrap);
	
				glTexImage2D(target, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, buf);
			} finally {
				if (input != null) {
					try { input.close(); } catch (IOException e) { }
				}
			}
		}
	
		public void bind() {
			if (id != bound)
				glBindTexture(target, id);
		}
	}
}