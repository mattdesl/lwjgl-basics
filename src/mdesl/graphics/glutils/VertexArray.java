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
package mdesl.graphics.glutils;

import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;

import java.nio.FloatBuffer;
import java.util.List;

import org.lwjgl.BufferUtils;

public class VertexArray implements VertexData {

	protected VertexAttrib[] attributes;

	private int totalNumComponents;
	private int stride;
	private FloatBuffer buffer;
	private int vertCount;
	
	/**
	 * 
	 * @param vertCount the number of VERTICES; e.g. 3 verts to make a triangle, regardless of number of attributes
	 * @param attributes a list of attributes per vertex
	 */
	public VertexArray(int vertCount, VertexAttrib ... attributes) {
		this.attributes = attributes;
		for (VertexAttrib a : attributes)
			totalNumComponents += a.numComponents;
		this.vertCount = vertCount;
		
		//our buffer which holds our data
		this.buffer = BufferUtils.createFloatBuffer(vertCount * totalNumComponents);
	}
	
	public VertexArray(int vertCount, List<VertexAttrib> attributes) {
		this(vertCount, attributes.toArray(new VertexAttrib[attributes.size()]));
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
		
		for (int i=0; i<attributes.length; i++) {
			VertexAttrib a = attributes[i];
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
		for (int i=0; i<attributes.length; i++) {
			VertexAttrib a = attributes[i];
			glDisableVertexAttribArray(a.location);
		}
	}
}
