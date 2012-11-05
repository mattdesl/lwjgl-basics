package mdesl.graphics;

import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL20.glUniform1i;
import static org.lwjgl.opengl.GL20.glUniformMatrix4;

import java.nio.FloatBuffer;
import java.util.Arrays;
import java.util.List;

import mdesl.graphics.glutils.ShaderProgram;
import mdesl.graphics.glutils.VertexArray;
import mdesl.graphics.glutils.VertexAttrib;
import mdesl.graphics.glutils.VertexData;
import mdesl.util.MathUtil;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Matrix4f;



public class SpriteBatch {
	public static final String TEXCOORD_0 = "tex0";
	public static final String PROJECTION_MATRIX = "projMatrix";
	
	public static final String ATTR_COLOR = "Color";
	public static final String ATTR_POSITION = "Position";
	public static final String ATTR_TEXCOORD = "TexCoord";
	
	public static final String DEFAULT_VERT_SHADER = "uniform mat4 "+PROJECTION_MATRIX+";\n" +
			"attribute vec4 "+ATTR_COLOR+";\n" + 
			"attribute vec2 "+ATTR_TEXCOORD+";\n" + 
			"attribute vec2 "+ATTR_POSITION+";\n" +
			"varying vec4 vColor;\n" + 
			"varying vec2 vTexCoord; \n" + 
			"void main() {\n" + 
			"	vColor = "+ATTR_COLOR+";\n" + 
			"	vTexCoord = "+ATTR_TEXCOORD+";\n" + 
			"	gl_Position = "+PROJECTION_MATRIX+" * vec4("+ATTR_POSITION+".xy, 0, 1);\n" + 
			"}";
	
	public static final String DEFAULT_FRAG_SHADER = "uniform sampler2D "+TEXCOORD_0+";\n" + 
			"varying vec4 vColor;\n" + 
			"varying vec2 vTexCoord;\n" +
			"void main(void) {\n" + 
			"	vec4 texColor = texture2D("+TEXCOORD_0+", vTexCoord);\n" + 
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
		
		updateProjection();
	}
	
	public SpriteBatch(int size) {
		this(getDefaultShader(), size);
	}
	
	public SpriteBatch() {
		this(1000);
	}
	
	public void setColor(float r, float g, float b, float a) {
		this.r = r; 
		this.g = g;
		this.b = b;
		this.a = a;
	}
	
	/**
	 * Call to update the projection matrix with the screen and send the uniform to the current shader.
	 */
	public void updateProjection() {
		projMatrix = MathUtil.toOrtho2D(projMatrix, 0, 0, Display.getWidth(), Display.getHeight());
		
		program.begin();
		
		//upload projection matrix
		int proj = program.getUniformLocation(PROJECTION_MATRIX);
		if (proj!=-1) { //if the uniform is active
			//ShaderProgram should probably have this as a convenience method
			if (buf16==null)
				buf16 = BufferUtils.createFloatBuffer(16);
			buf16.clear();
			projMatrix.store(buf16);
			buf16.flip();
			glUniformMatrix4(proj, true, buf16);
		}
		
		//upload texcoord 0
		int tex0 = program.getUniformLocation(TEXCOORD_0);
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
