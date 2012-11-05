package mdesl.graphics.glutils;

public class VertexAttrib {
	
	public final String name;
	public final int numComponents;
	public final int location;
	
	public VertexAttrib(int location, String name, int numComponents) {
		this.location = location;
		this.name = name;
		this.numComponents = numComponents;
	}
	
	public String toString() {
		return name +" (" + numComponents+")";
	}
}