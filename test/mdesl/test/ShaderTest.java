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
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH;
import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glOrtho;
import static org.lwjgl.opengl.GL11.glTexCoord2f;
import static org.lwjgl.opengl.GL11.glVertex2f;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL20.GL_COMPILE_STATUS;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_INFO_LOG_LENGTH;
import static org.lwjgl.opengl.GL20.GL_LINK_STATUS;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20.glAttachShader;
import static org.lwjgl.opengl.GL20.glBindAttribLocation;
import static org.lwjgl.opengl.GL20.glCompileShader;
import static org.lwjgl.opengl.GL20.glCreateProgram;
import static org.lwjgl.opengl.GL20.glCreateShader;
import static org.lwjgl.opengl.GL20.glDeleteProgram;
import static org.lwjgl.opengl.GL20.glDeleteShader;
import static org.lwjgl.opengl.GL20.glDetachShader;
import static org.lwjgl.opengl.GL20.glGetProgramInfoLog;
import static org.lwjgl.opengl.GL20.glGetProgrami;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;
import static org.lwjgl.opengl.GL20.glGetShaderi;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glLinkProgram;
import static org.lwjgl.opengl.GL20.glShaderSource;
import static org.lwjgl.opengl.GL20.glUniform1i;
import static org.lwjgl.opengl.GL20.glUniformMatrix4;
import static org.lwjgl.opengl.GL20.glUseProgram;

import java.nio.FloatBuffer;
import java.util.Map;
import java.util.Map.Entry;

import mdesl.graphics.Texture;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.util.vector.Matrix4f;


public class ShaderTest {
	
	public static void main(String[] args) throws LWJGLException {		
		final int WIDTH = 800, HEIGHT = 600;
		Display.setDisplayMode(new DisplayMode(WIDTH, HEIGHT));
		Display.create();
		
//		
//		Texture tex = null, tex2 = null;
//		try {
//			//TODO: put this into a texture atlas!
//			tex = new Texture(ShaderTest.class.getClassLoader().getResource("res/tiles.png"), Texture.NEAREST, Texture.CLAMP_TO_EDGE);
//			tex2 = new Texture(ShaderTest.class.getClassLoader().getResource("res/grass.png"));
//		} catch (IOException e) {
//			e.printStackTrace();
//			System.exit(0);
//		}
		
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
	
	public static class ShaderProgram {

		protected static FloatBuffer buf16Pool;
		
		/**
		 * Makes the "default shader" (0) the active program. In GL 3.1+ core profile,
		 * you may run into glErrors if you try rendering with the default shader. 
		 */
		public static void unbind() {
			glUseProgram(0);
		}

		public final int program;
		public final int vertex;
		public final int fragment;
		protected String log;

		public ShaderProgram(String vertexSource, String fragmentSource) throws LWJGLException {
			this(vertexSource, fragmentSource, null);
		}

		/**
		 * Creates a new shader from vertex and fragment source, and with the given 
		 * map of <Integer, String> attrib locations
		 * @param vertexShader the vertex shader source string
		 * @param fragmentShader the fragment shader source string
		 * @param attributes a map of attrib locations for GLSL 120
		 * @throws LWJGLException if the program could not be compiled and linked
		 */
		public ShaderProgram(String vertexShader, String fragmentShader, Map<Integer, String> attributes) throws LWJGLException {
			//compile the String source
			vertex = compileShader(vertexShader, GL_VERTEX_SHADER);
			fragment = compileShader(fragmentShader, GL_FRAGMENT_SHADER);
			
			//create the program
			program = glCreateProgram();
			
			//attach the shaders
			glAttachShader(program, vertex);
			glAttachShader(program, fragment);

			//bind the attrib locations for GLSL 120
			if (attributes != null)
				for (Entry<Integer, String> e : attributes.entrySet())
					glBindAttribLocation(program, e.getKey(), e.getValue());

			//link our program
			glLinkProgram(program);

			//grab our info log
			String infoLog = glGetProgramInfoLog(program, glGetProgrami(program, GL_INFO_LOG_LENGTH));
			
			//if some log exists, append it 
			if (infoLog!=null && infoLog.trim().length()!=0)
				log += infoLog;
			
			//if the link failed, throw some sort of exception
			if (glGetProgrami(program, GL_LINK_STATUS) == GL_FALSE)
				throw new LWJGLException(
						"Failure in linking program. Error log:\n" + infoLog);
			
			//detach and delete the shaders which are no longer needed
			glDetachShader(program, vertex);
			glDetachShader(program, fragment);
			glDeleteShader(vertex);
			glDeleteShader(fragment);
		}

		/** Compile the shader source as the given type and return the shader object ID. */
		protected int compileShader(String source, int type) throws LWJGLException {
			//create a shader object
			int shader = glCreateShader(type);
			//pass the source string
			glShaderSource(shader, source);
			//compile the source
			glCompileShader(shader);

			//if info/warnings are found, append it to our shader log
			String infoLog = glGetShaderInfoLog(shader,
					glGetShaderi(shader, GL_INFO_LOG_LENGTH));
			if (infoLog!=null && infoLog.trim().length()!=0)
				log += getName(type) +": "+infoLog + "\n";
			
			//if the compiling was unsuccessful, throw an exception
			if (glGetShaderi(shader, GL_COMPILE_STATUS) == GL_FALSE)
				throw new LWJGLException("Failure in compiling " + getName(type)
						+ ". Error log:\n" + infoLog);

			return shader;
		}

		protected String getName(int shaderType) {
			if (shaderType == GL_VERTEX_SHADER)
				return "GL_VERTEX_SHADER";
			if (shaderType == GL_FRAGMENT_SHADER)
				return "GL_FRAGMENT_SHADER";
			else
				return "shader";
		}

		/**
		 * Make this shader the active program.
		 */
		public void use() {
			glUseProgram(program);
		}

		/**
		 * Destroy this shader program.
		 */
		public void destroy() {
			glDeleteProgram(program);
		}

		/**
		 * Gets the location of the specified uniform name.
		 * @param str the name of the uniform
		 * @return the location of the uniform in this program
		 */
		public int getUniformLocation(String str) {
			return glGetUniformLocation(program, str);
		}
		
		/* ------ UNIFORM SETTERS/GETTERS ------ */
		
		/**
		 * Sets the uniform data at the specified location (the uniform type may be int, bool or sampler2D). 
		 * @param loc the location of the int/bool/sampler2D uniform 
		 * @param i the value to set
		 */
		public void setUniformi(int loc, int i) {
			if (loc==-1) return;
			glUniform1i(loc, i);
		}

		/**
		 * Sends a 4x4 matrix to the shader program.
		 * @param loc the location of the mat4 uniform
		 * @param transposed whether the matrix should be transposed
		 * @param mat the matrix to send
		 */
		public void setUniformMatrix(int loc, boolean transposed, Matrix4f mat) {
			if (loc==-1) return;
			if (buf16Pool == null)
				buf16Pool = BufferUtils.createFloatBuffer(16);
			buf16Pool.clear();
			mat.store(buf16Pool);
			buf16Pool.flip();
			glUniformMatrix4(loc, transposed, buf16Pool);
		}
	}
}