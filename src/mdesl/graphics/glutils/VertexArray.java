package mdesl.graphics.glutils;

import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;

import java.nio.FloatBuffer;
import java.util.List;

import org.lwjgl.BufferUtils;

public class VertexArray implements VertexData {

	protected List<VertexAttrib> attributes;

	private int totalNumComponents;
	private int stride;
	private FloatBuffer buffer;
	private int vertCount;
	
	public VertexArray(int vertCount, List<VertexAttrib> attributes) {
		this.attributes = attributes;
		for (VertexAttrib a : attributes)
			totalNumComponents += a.numComponents;
		this.vertCount = vertCount;
		
		//our buffer which holds our data
		this.buffer = BufferUtils.createFloatBuffer(vertCount * totalNumComponents);
	}
	
	public VertexArray flip() {
		buffer.flip();
		return this;
	}
	
	public VertexArray clear() {
		buffer.clear();
		return this;
	}
	
	public VertexArray put(float[] verts, int offset, int length) {
		buffer.put(verts, offset, length);
		return this;
	}
	
	public VertexArray put(float f) {
		buffer.put(f);
		return this;
	}
		
	public FloatBuffer buffer() {
		return buffer;
	}
	
	public int getTotalNumComponents() {
		return totalNumComponents;
	}
	
	public int getVertexCount() {
		return vertCount;
	}
	
	public void bind() {
		int offset = 0;
		//4 bytes per float
		int stride = totalNumComponents * 4;
		
		for (int i=0; i<attributes.size(); i++) {
			VertexAttrib a = attributes.get(i);
			buffer.position(offset);
			glEnableVertexAttribArray(a.location);
			glVertexAttribPointer(a.location, a.numComponents, false, stride, buffer);			
			offset += a.numComponents;
		}
	}
	
	public void draw(int geom, int first, int count) {
		glDrawArrays(geom, first, count);
	}
	
	public void unbind() {
		for (int i=0; i<attributes.size(); i++) {
			VertexAttrib a = attributes.get(i);
			glDisableVertexAttribArray(a.location);
		}
	}
}
