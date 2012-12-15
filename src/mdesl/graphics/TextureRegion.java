/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package mdesl.graphics;

/** A trimmed down version of TextureRegion from LibGDX. 
 * Defines a rectangular area of a texture. The coordinate system used has its origin in the upper left corner with the x-axis
 * pointing to the right and the y axis pointing downwards.
 * @author mzechner
 * @author Nathan Sweet */
public class TextureRegion implements ITexture {

	protected Texture texture;
	protected float u;
	protected float v;
	protected float u2;
	protected float v2;
	
	protected int regionWidth;
	protected int regionHeight;
	
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
	
	public TextureRegion(TextureRegion region, int x, int y, int width, int height) {
		set(region, x, y, width, height);
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
	
	public void set(TextureRegion region, int x, int y, int width, int height) {
		set(region.texture, x + region.getRegionX(), y + region.getRegionY(), width, height);
	}

	public int getRegionX () {
		return Math.round(u * texture.getWidth());
	}
	
	public int getRegionY () {
		return Math.round(v * texture.getHeight());
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
	public int getWidth() {
		return regionWidth;
	}

	/**
	 * Returns the height (in pixels) of this region.
	 * @return the height of this region
	 */
	@Override
	public int getHeight() {
		return regionHeight;
	}
	
}
