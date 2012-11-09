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
package mdesl.test;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glViewport;

import java.io.IOException;
import java.net.URL;

import mdesl.graphics.SpriteBatch;
import mdesl.graphics.Texture;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.util.vector.Vector3f;

public class VertexArrayExample {

	public static void main(String[] args) throws LWJGLException {
		new VertexArrayExample().start();
	}

	public void start() throws LWJGLException {
		Display.setTitle("Vertex Array Example");
		Display.setResizable(true);
		Display.setDisplayMode(new DisplayMode(800, 600));
		Display.setVSyncEnabled(true);
		Display.create();

		create();

		while (!Display.isCloseRequested()) {
			if (Display.wasResized())
				resize();
			render();

			Display.update();
			Display.sync(60);
		}

		Display.destroy();
	}

	Texture tex, tex2;
	SpriteBatch batch;

	static URL getResource(String ref) {
		URL url = VertexArrayExample.class.getClassLoader().getResource(ref);
		if (url==null)
			throw new RuntimeException("could not find resource: "+ref);
		return url;
	}

	protected void resize() {
		glViewport(0, 0, Display.getWidth(), Display.getHeight());
		batch.updateProjection();
	}

	protected void create() {
		glDisable(GL_DEPTH_TEST);
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glClearColor(0f, 0f, 0f, 0f);
		glViewport(0, 0, Display.getWidth(), Display.getHeight());

		//Load some textures
		try {
			tex = new Texture(getResource("res/tiles.png"), Texture.LINEAR);
			tex2 = new Texture(getResource("res/font0.png"));
		} catch (IOException e) {
			throw new RuntimeException("couldn't decode textures");
		}

		batch = new SpriteBatch(1000);
	}

	private static float rotation = 0f;
	private static final Vector3f axis = new Vector3f(0, 0, 1f);
	private static final Vector3f translate = new Vector3f(0, 0, 0);

	protected void render() {
		glClear(GL_COLOR_BUFFER_BIT);

		batch.getViewMatrix().setIdentity();

		float rotationPointX = Display.getWidth()/2;
		float rotationPointY = Display.getHeight()/2;
		rotation += 0.011;

		// Translate the view into the mid of the point to rotate about:
		translate.set( rotationPointX,  rotationPointY, 0);
		batch.getViewMatrix().translate(translate);
		// Rotate the view:
		batch.getViewMatrix().rotate(rotation, axis);
		// Translate back to look directly at the rotation point, when rendering:
		translate.set(-rotationPointX, -rotationPointY, 0);
		batch.getViewMatrix().translate(translate);
		// Upload the view matrix to the GPU:
		batch.updateView();

		// Begin rendering:
		batch.begin();

		batch.draw(tex, 50, 50);
		batch.drawRegion(tex, 0, 0, 64, 64, 50, 350); //draw a single tile

		batch.setColor(1f, 0f, 0f, 1f); //tint red
		batch.draw(tex2, 350, 25);
		batch.setColor(1f, 1f, 1f, 1f); //reset color..

		batch.end();

		//simple debugging...
		//Display.setTitle("Render calls: "+batch.renderCalls);
		//batch.renderCalls = 0;
	}
}