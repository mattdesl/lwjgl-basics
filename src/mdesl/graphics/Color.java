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

/** A minimal Color utility, which holds four float values representing RGBA.
 * 
 * @author davedes */
public class Color {

	/** The fixed color transparent */
	public static final Color TRANSPARENT = new Color(0.0f, 0.0f, 0.0f, 0.0f);
	/** The fixed colour white */
	public static final Color WHITE = new Color(1.0f, 1.0f, 1.0f, 1.0f);
	/** The fixed colour yellow */
	public static final Color YELLOW = new Color(1.0f, 1.0f, 0, 1.0f);
	/** The fixed colour red */
	public static final Color RED = new Color(1.0f, 0, 0, 1.0f);
	/** The fixed colour blue */
	public static final Color BLUE = new Color(0, 0, 1.0f, 1.0f);
	/** The fixed colour green */
	public static final Color GREEN = new Color(0, 1.0f, 0, 1.0f);
	/** The fixed colour black */
	public static final Color BLACK = new Color(0, 0, 0, 1.0f);
	/** The fixed colour gray */
	public static final Color GRAY = new Color(0.5f, 0.5f, 0.5f, 1.0f);
	/** The fixed colour cyan */
	public static final Color CYAN = new Color(0, 1.0f, 1.0f, 1.0f);
	/** The fixed colour dark gray */
	public static final Color DARK_GRAY = new Color(0.3f, 0.3f, 0.3f, 1.0f);
	/** The fixed colour light gray */
	public static final Color LIGHT_GRAY = new Color(0.7f, 0.7f, 0.7f, 1.0f);
	/** The fixed colour dark pink */
	public final static Color PINK = new Color(255, 175, 175, 255);
	/** The fixed colour dark orange */
	public final static Color ORANGE = new Color(255, 200, 0, 255);
	/** The fixed colour dark magenta */
	public final static Color MAGENTA = new Color(255, 0, 255, 255);
	
	/** The red component [0.0 - 1.0]. */
	public float r;
	/** The green component [0.0 - 1.0]. */
	public float g;
	/** The blue component [0.0 - 1.0]. */
	public float b;
	/** The alpha component [0.0 - 1.0]. */
	public float a;

	/** Create a 4 component colour
	 * 
	 * @param r The red component of the colour (0.0 -> 1.0)
	 * @param g The green component of the colour (0.0 -> 1.0)
	 * @param b The blue component of the colour (0.0 -> 1.0)
	 * @param a The alpha component of the colour (0.0 -> 1.0) */
	public Color(float r, float g, float b, float a) {
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
	}

	/** Create a 3 component colour; alpha is passed as 1.0 (255).
	 * 
	 * @param r The red component of the colour (0.0 -> 1.0)
	 * @param g The green component of the colour (0.0 -> 1.0)
	 * @param b The blue component of the colour (0.0 -> 1.0) */
	public Color(float r, float g, float b) {
		this(r, g, b, 1f);
	}

	/** Create a 4 component colour
	 * 
	 * @param r The red component of the colour (0 -> 255)
	 * @param g The green component of the colour (0 -> 255)
	 * @param b The blue component of the colour (0 -> 255)
	 * @param a The alpha component of the colour (0 -> 255) */
	public Color(int r, int g, int b, int a) {
		this(r / 255f, g / 255f, b / 255f, a / 255f);
	}

	/** Create a 3 component colour; alpha is passed as 255 (1.0).
	 * 
	 * @param r The red component of the colour (0 -> 255)
	 * @param g The green component of the colour (0 -> 255)
	 * @param b The blue component of the colour (0 -> 255) */
	public Color(int r, int g, int b) {
		this(r, g, b, 255);
	}

	/** Creates a WHITE color. */
	public Color() {
		this(Color.WHITE);
	}

	/** Copy constructor
	 * 
	 * @param color The color to copy into the new instance */
	public Color(Color color) {
		this(color.r, color.g, color.b, color.a);
	}

	/** Create a colour from an integer packed 0xAARRGGBB. If AA is specified as
	 * zero then it will be interpreted as unspecified and hence a value of 255
	 * will be recorded.
	 * 
	 * @param value The value to interpret for the colour */
	public Color(int value) {
		int r = (value & 0x00FF0000) >> 16;
		int g = (value & 0x0000FF00) >> 8;
		int b = (value & 0x000000FF);
		int a = (value & 0xFF000000) >> 24;
		if (a < 0)
			a += 256;
		if (a == 0)
			a = 255;
		this.r = r / 255.0f;
		this.g = g / 255.0f;
		this.b = b / 255.0f;
		this.a = a / 255.0f;
	}

	/** Decode a number in a string and process it as a colour.
	 * 
	 * @param nm The number string to decode
	 * @return The color created from the number read
	 * @throws NumberFormatException if the string was invalid */
	public static Color decode(String nm) {
		return new Color(Integer.decode(nm).intValue());
	}

	/** Get the red byte component of this colour
	 * 
	 * @return The red component (range 0-255) */
	public int red() {
		return (int) (r * 255);
	}

	/** Get the green byte component of this colour
	 * 
	 * @return The green component (range 0-255) */
	public int green() {
		return (int) (g * 255);
	}

	/** Get the blue byte component of this colour
	 * 
	 * @return The blue component (range 0-255) */
	public int blue() {
		return (int) (b * 255);
	}

	/** Get the alpha byte component of this colour
	 * 
	 * @return The alpha component (range 0-255) */
	public int alpha() {
		return (int) (a * 255);
	}
	
	public void set(Color color) {
		set(color.r, color.g, color.b, color.a);
	}
	
	public void set(float r, float g, float b, float a) {
		set(r, g, b);
		this.a = a;
	}
	
	public void set(float r, float g, float b) {
		this.r = r;
		this.g = g;
		this.b = b;
	}

	/** Packs the 4 components of this color into a 32-bit int.
	 * 
	 * @return the packed color as a 32-bit int. */
	public int toIntBits() {
		int color = ((int) (255 * a) << 24) | ((int) (255 * b) << 16) | ((int) (255 * g) << 8)
				| ((int) (255 * r));
		return color;
	}

	/** @see java.lang.Object#hashCode() */
	public int hashCode() {
		return ((int) (r + g + b + a) * 255);
	}

	/** @see java.lang.Object#equals(java.lang.Object) */
	public boolean equals(Object other) {
		if (other instanceof Color) {
			Color o = (Color) other;
			return ((o.r == r) && (o.g == g) && (o.b == b) && (o.a == a));
		}
		return false;
	}

	/** @see java.lang.Object#toString() */
	public String toString() {
		return "Color (" + r + "," + g + "," + b + "," + a + ")";
	}
}
