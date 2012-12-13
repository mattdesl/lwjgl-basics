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

public class TextureRegion implements ITexture {

	protected Texture texture;
	protected float u;
	protected float v;
	protected float u2;
	protected float v2;
	
	protected float regionWidth;
	protected float regionHeight;
	
	public TextureRegion(Texture texture) {
		this(texture, 0, 0);
	}
	
	public TextureRegion(Texture texture, int x, int y) {
		this(texture, x, y, texture.getWidth(), texture.getHeight());
	}
	
	public TextureRegion(Texture texture, int x, int y, int width, int height) {
		set(texture, x, y, width, height);
	}
	
	public TextureRegion(Texture texture, float u, float v, float u2, float v2) {
		set(texture, u, v, u2, v2);
	}
	
	public void set(Texture texture, int x, int y, int width, int height) {
		set(texture, x / (float)texture.getWidth(),
					 y / (float)texture.getHeight(),
					(x + width) / (float)texture.getWidth(),
					(y + height) / (float)texture.getHeight());
		regionWidth = Math.round(width);
		regionHeight = Math.round(height);
	}
	
	public void set(Texture texture, float u, float v, float u2, float v2) {
		this.texture = texture;
		this.u = u;
		this.v = v;
		this.u2 = u2;
		this.v2 = v2;
		regionWidth = Math.round(Math.abs(u2 - u) * texture.getWidth());
		regionHeight = Math.round(Math.abs(v2 - v) * texture.getHeight());
	}
	
	public void flip (boolean x, boolean y) {
		if (x) {
			float temp = u;
			u = u2;
			u2 = temp;
		}
		if (y) {
			float temp = v;
			v = v2;
			v2 = temp;
		}
	}
	
	/**
	 * @return the texture
	 */
	public Texture getTexture() {
		return texture;
	}

	/**
	 * @param texture the texture to set
	 */
	public void setTexture(Texture texture) {
		this.texture = texture;
	}

	/**
	 * @return the u
	 */
	public float getU() {
		return u;
	}

	/**
	 * @param u the u to set
	 */
	public void setU(float u) {
		this.u = u;
	}

	/**
	 * @return the v
	 */
	public float getV() {
		return v;
	}

	/**
	 * @param v the v to set
	 */
	public void setV(float v) {
		this.v = v;
	}

	/**
	 * @return the u2
	 */
	public float getU2() {
		return u2;
	}

	/**
	 * @param u2 the u2 to set
	 */
	public void setU2(float u2) {
		this.u2 = u2;
	}

	/**
	 * @return the v2
	 */
	public float getV2() {
		return v2;
	}

	/**
	 * @param v2 the v2 to set
	 */
	public void setV2(float v2) {
		this.v2 = v2;
	}

	/**
	 * Returns the width (in pixels) of this region.
	 * @return the width of this region
	 */
	@Override
	public float getWidth() {
		return regionWidth;
	}

	/**
	 * Returns the height (in pixels) of this region.
	 * @return the height of this region
	 */
	@Override
	public float getHeight() {
		return regionHeight;
	}
	
}
