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
import static org.lwjgl.opengl.GL11.GL_NEAREST;
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
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glPixelStorei;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;

import de.matthiasmann.twl.utils.PNGDecoder;

public class Texture {
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