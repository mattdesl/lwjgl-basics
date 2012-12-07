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

import java.io.IOException;

import mdesl.graphics.SpriteBatch;
import mdesl.graphics.Texture;
import mdesl.util.Util;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;

public class SpriteBatchExample extends SimpleGame {

	public static void main(String[] args) throws LWJGLException {
		Game game = new SpriteBatchExample();
		game.setDisplayMode(800, 600, false);
		game.start();
	}

	Texture tex, tex2;
	SpriteBatch batch;

	protected void create() throws LWJGLException {
		super.create();

		//Load some textures
		try {
			tex = new Texture(Util.getResource("res/tiles.png"), Texture.LINEAR);
			tex2 = new Texture(Util.getResource("res/font0.png"));
		} catch (IOException e) {
			throw new RuntimeException("couldn't decode textures");
		}

		batch = new SpriteBatch(1000);
	}

	protected void render() throws LWJGLException {
		super.render();		
		
		// Begin rendering:
		batch.begin();

		batch.drawRegion(tex, 64, 64, 64, 64, 0, 0); //draw a single tile		
		batch.drawRegion(tex, 0, 0, 64, 64, 50, 350); //draw a single tile

		batch.setColor(1f, 0f, 0f, 1f); //tint red
		batch.draw(tex2, 350, 25);
		batch.setColor(1f, 1f, 1f, 1f); //reset color..

		batch.end();
	}
	

	protected void resize() throws LWJGLException {
		super.resize();
		batch.resize(Display.getWidth(), Display.getHeight());
	}
}