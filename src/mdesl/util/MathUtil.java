package mdesl.util;

import org.lwjgl.util.vector.Matrix4f;

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
}
