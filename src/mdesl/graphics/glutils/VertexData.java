package mdesl.graphics.glutils;

import java.nio.FloatBuffer;

public interface VertexData {
	public void bind();
	public void draw(int geom, int first, int count);
	public void unbind();
	
	public VertexData clear();
	public VertexData flip();
	public VertexData put(float[] verts, int offset, int length);
	public VertexData put(float v);
	
	public FloatBuffer buffer();
	
	public int getTotalNumComponents();
	public int getVertexCount();
	
}

