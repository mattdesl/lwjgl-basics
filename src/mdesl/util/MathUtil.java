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
package mdesl.util;

import org.lwjgl.util.vector.Matrix4f;

/**
 * Math utilities; adapted from LibGDX's vector classes for use with LWJGL Vector utilities
 */
public class MathUtil {
	
	/**
	 * Sets the given matrix to an orthographic 2D projection matrix, and returns it. If the given matrix
	 * is null, a new one will be created and returned. 
	 * 
	 * @param m the matrix to re-use, or null to create a new matrix
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @return the given matrix, or a newly created matrix if none was specified
	 */
	public static Matrix4f toOrtho2D(Matrix4f m, float x, float y, float width, float height) {
		return toOrtho(m, x, x + width, y + height, y, 1, -1);
	}
	
	/**
	 * Sets the given matrix to an orthographic 2D projection matrix, and returns it. If the given matrix
	 * is null, a new one will be created and returned. 
	 * 
	 * @param m the matrix to re-use, or null to create a new matrix
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param near near clipping plane
	 * @param far far clipping plane
	 * @return the given matrix, or a newly created matrix if none was specified
	 */
	public static Matrix4f toOrtho2D(Matrix4f m, float x, float y, float width, float height, float near, float far) {
		return toOrtho(m, x, x + width, y, y + height, near, far);
	}
	
	/**
	 * Sets the given matrix to an orthographic projection matrix, and returns it. If the given matrix
	 * is null, a new one will be created and returned. 
	 * 
	 * @param m the matrix to re-use, or null to create a new matrix
	 * @param left 
	 * @param right
	 * @param bottom
	 * @param top
	 * @param near near clipping plane
	 * @param far far clipping plane
	 * @return the given matrix, or a newly created matrix if none was specified
	 */
	public static Matrix4f toOrtho(Matrix4f m, float left, float right, float bottom, float top,
			float near, float far) {
		if (m==null)
			m = new Matrix4f();
		float x_orth = 2 / (right - left);
		float y_orth = 2 / (top - bottom);
		float z_orth = -2 / (far - near);

		float tx = -(right + left) / (right - left);
		float ty = -(top + bottom) / (top - bottom);
		float tz = -(far + near) / (far - near);

		m.m00 = x_orth;
		m.m10 = 0;
		m.m20 = 0;
		m.m30 = 0;
		m.m01 = 0;
		m.m11 = y_orth;
		m.m21 = 0;
		m.m31 = 0;
		m.m02 = 0;
		m.m12 = 0;
		m.m22 = z_orth;
		m.m32 = 0;
		m.m03 = tx;
		m.m13 = ty;
		m.m23 = tz;
		m.m33 = 1;
		return m;
	}
	
	
	/** Sets the matrix to a projection matrix with a near- and far plane, a field of view in degrees and an aspect ratio.
	 * 
	 * @param near The near plane
	 * @param far The far plane
	 * @param fov The field of view in degrees
	 * @param aspectRatio The aspect ratio
	 * @return This matrix for the purpose of chaining methods together. */
	public static Matrix4f setToProjection (Matrix4f m, float near, float far, float fov, float aspectRatio) {
		if (m==null)
			m = new Matrix4f();
		m.setIdentity();
		float l_fd = (float)(1.0 / Math.tan((fov * (Math.PI / 180)) / 2.0));
		float l_a1 = (far + near) / (near - far);
		float l_a2 = (2 * far * near) / (near - far);
		m.m00 = l_fd / aspectRatio;
		m.m10 = 0;
		m.m20 = 0;
		m.m30 = 0;
		m.m01 = 0;
		m.m11 = l_fd;
		m.m21 = 0;
		m.m31 = 0;
		m.m02 = 0;
		m.m12 = 0;
		m.m22 = l_a1;
		m.m32 = -1;
		m.m03 = 0;
		m.m13 = 0;
		m.m23 = l_a2;
		m.m33 = 0;
		return m;
	}
}
