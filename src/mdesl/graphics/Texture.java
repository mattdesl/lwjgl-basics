package mdesl.graphics;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL11.GL_PACK_ALIGNMENT;
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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL12;

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
			if (input!=null) {
				try { input.close(); } catch (IOException e) {}
			}
		}
	}
	
	public void bind() {
		if (id != bound)
			glBindTexture(target, id);
	}
}