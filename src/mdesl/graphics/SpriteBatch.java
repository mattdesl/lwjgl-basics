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

import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL20.glUniform1i;

import java.nio.FloatBuffer;
import java.util.Arrays;
import java.util.List;

import mdesl.graphics.glutils.ShaderProgram;
import mdesl.graphics.glutils.VertexArray;
import mdesl.graphics.glutils.VertexAttrib;
import mdesl.graphics.glutils.VertexData;
import mdesl.util.MathUtil;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Matrix4f;


/**
 * @author Matt (mdesl) DesLauriers
 * @author matheusdev
 */
public class SpriteBatch {
	public static final String U_TEXTURE = "u_texture";
	public static final String U_PROJ_VIEW = "u_projView";

	public static final String ATTR_COLOR = "Color";
	public static final String ATTR_POSITION = "Position";
	public static final String ATTR_TEXCOORD = "TexCoord";

	public static final String DEFAULT_VERT_SHADER =
			"uniform mat4 "+U_PROJ_VIEW+";\n" +
			"attribute vec4 "+ATTR_COLOR+";\n" +
			"attribute vec2 "+ATTR_TEXCOORD+";\n" +
			"attribute vec2 "+ATTR_POSITION+";\n" +
			"varying vec4 vColor;\n" +
			"varying vec2 vTexCoord; \n" +
			"void main() {\n" +
			"	vColor = "+ATTR_COLOR+";\n" +
			"	vTexCoord = "+ATTR_TEXCOORD+";\n" +
			"	gl_Position = "+U_PROJ_VIEW+" * vec4("+ATTR_POSITION+".xy, 0, 1);\n" +
			"}";

	public static final String DEFAULT_FRAG_SHADER =
			"uniform sampler2D "+U_TEXTURE+";\n" +
			"varying vec4 vColor;\n" +
			"varying vec2 vTexCoord;\n" +
			"void main(void) {\n" +
			"	vec4 texColor = texture2D("+U_TEXTURE+", vTexCoord);\n" +
			"	gl_FragColor = vColor * texColor;\n" +
			"}";

	public static final List<VertexAttrib> ATTRIBUTES = Arrays.asList(
			new VertexAttrib(0, ATTR_POSITION, 2),
			new VertexAttrib(1, ATTR_COLOR, 4),
			new VertexAttrib(2, ATTR_TEXCOORD, 2));

	static ShaderProgram defaultShader;
	public static int renderCalls = 0;

	protected FloatBuffer buf16;
	protected Matrix4f projMatrix;
	protected Matrix4f viewMatrix;
	protected Matrix4f projViewMatrix;
	protected Matrix4f transpositionPool;

	protected Texture texture;
	protected ShaderProgram program;

	protected VertexData data;

	private int idx;
	private int maxIndex;

	private float r=1f, g=1f, b=1f, a=1f;
	private boolean drawing = false;

	static ShaderProgram getDefaultShader() {
		return defaultShader==null
				? new ShaderProgram(DEFAULT_VERT_SHADER, DEFAULT_FRAG_SHADER, ATTRIBUTES)
				: defaultShader;
	}

	public SpriteBatch(ShaderProgram program, int size) {
		this.program = program;

		//later we can do some abstraction to replace this with VBOs...
		this.data = new VertexArray(size * 6, ATTRIBUTES);

		//max indices before we need to flush the renderer
		maxIndex = size * 6;

		updateMatrices();
	}

	public SpriteBatch(int size) {
		this(getDefaultShader(), size);
	}

	public SpriteBatch() {
		this(1000);
	}

	public Matrix4f getViewMatrix() {
		return viewMatrix;
	}

	public void setColor(float r, float g, float b, float a) {
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
	}

	/**
	 * Call to multiply the the projection with the view matrix and save
	 * the result in the uniform mat4 {@value #U_PROJ_VIEW}.
	 */
	public void updateMatrices() {
		// Create projection matrix:
		projMatrix = MathUtil.toOrtho2D(projMatrix, 0, 0, Display.getWidth(), Display.getHeight());
		// Create view Matrix, if not present:
		if (viewMatrix == null) {
			viewMatrix = new Matrix4f();
		}
		// Multiply the transposed projection matrix with the view matrix:
		projViewMatrix = Matrix4f.mul(
				Matrix4f.transpose(projMatrix, transpositionPool),
				viewMatrix,
				projViewMatrix);

		program.begin();

		// Store the the multiplied matrix in the "projViewMatrix"-uniform:
		program.storeUniformMat4(U_PROJ_VIEW, projViewMatrix, false);

		//upload texcoord 0
		int tex0 = program.getUniformLocation(U_TEXTURE);
		if (tex0!=-1)
			glUniform1i(tex0, 0);

		program.end();
	}

	public void begin() {
		if (drawing) throw new IllegalStateException("must not be drawing before calling begin()");
		drawing = true;
		program.begin();
		idx = 0;
		renderCalls = 0;
		texture = null;
	}

	public void end() {
		if (!drawing) throw new IllegalStateException("must be drawing before calling end()");
		drawing = false;
		flush();
		program.end();
	}

	public void flush() {
		if (idx>0) {
			data.flip();
			render();
		    idx = 0;
		    data.clear();
		}
	}

	public void drawRegion(Texture tex, float srcX, float srcY, float srcWidth,
			float srcHeight, float dstX, float dstY) {
		drawRegion(tex, srcX, srcY, srcWidth, srcHeight, dstX, dstY, srcWidth, srcHeight);
	}

	public void drawRegion(Texture tex, float srcX, float srcY, float srcWidth,
			float srcHeight, float dstX, float dstY, float dstWidth,
			float dstHeight) {
		float u = srcX / tex.width;
		float v = srcY / tex.height;
		float u2 = (srcX+srcWidth) / tex.width;
		float v2 = (srcY+srcHeight) / tex.height;
		draw(tex, dstX, dstY, dstWidth, dstHeight, u, v, u2, v2);
	}

	public void draw(Texture tex, float x, float y) {
		draw(tex, x, y, tex.width, tex.height);
	}

	public void draw(Texture tex, float x, float y, float width, float height) {
		draw(tex, x, y, width, height, 0, 0, 1, 1);
	}

	public void draw(Texture tex, float x, float y, float width, float height,
			float u, float v, float u2, float v2) {
		checkFlush(tex);

		//top left, top right, bottom left
		vertex(x, y, r, g, b, a, u, v);
		vertex(x+width, y, r, g, b, a, u2, v);
		vertex(x, y+height, r, g, b, a, u, v2);

		//top right, bottom right, bottom left
		vertex(x+width, y, r, g, b, a, u2, v);
		vertex(x+width, y+height, r, g, b, a, u2, v2);
		vertex(x, y+height, r, g, b, a, u, v2);
	}


	/**
	 * Renders a texture using custom vertex attributes; e.g. for different vertex colours.
	 * This will ignore the current batch color.
	 *
	 * @param tex the texture to use
	 * @param vertices an array of 6 vertices, each holding 8 attributes (total = 48 elements)
	 * @param offset the offset from the vertices array to start from
	 */
	public void draw(Texture tex, float[] vertices, int offset) {
		checkFlush(tex);
		data.put(vertices, offset, data.getTotalNumComponents() * 6);
		idx += 6;
	}

	VertexData vertex(float x, float y, float r, float g, float b, float a,
			float u, float v) {
		data.put(x).put(y).put(r).put(g).put(b).put(a).put(u).put(v);
		idx++;
		return data;
	}

	protected void checkFlush(Texture texture) {
		if (texture==null || texture==null)
			throw new NullPointerException("null texture");

		//we need to bind a different texture/type. this is
		//for convenience; ideally the user should order
		//their rendering wisely to minimize texture binds
		if (texture!=this.texture || idx >= maxIndex) {
			//apply the last texture
			flush();
			this.texture = texture;
		}
	}

	private void render() {
		if (texture!=null)
			texture.bind();
		data.bind();
		data.draw(GL_TRIANGLES, 0, idx);
		data.unbind();
		renderCalls++;
	}


}
