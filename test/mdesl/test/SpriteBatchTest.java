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

import static org.lwjgl.opengl.GL11.glClearColor;

import java.io.IOException;

import mdesl.graphics.Color;
import mdesl.graphics.SpriteBatch;
import mdesl.graphics.Texture;
import mdesl.graphics.TextureRegion;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.opengl.Display;

public class SpriteBatchTest extends SimpleGame {

	public static void main(String[] args) throws LWJGLException {
		Game game = new SpriteBatchTest();
		game.setDisplayMode(640, 480, false);
		game.start();
	}

	Texture tex, tex2;
	TextureRegion tile;
	SpriteBatch batch;

	protected void create() throws LWJGLException {
		super.create();

		//Load some textures
		try {
			tex = new Texture(Util.getResource("res/tiles.png"), Texture.NEAREST);
			tex2 = new Texture(Util.getResource("res/ptsans_00.png"));
			tile = new TextureRegion(tex, 128, 64, 64, 64);
		} catch (IOException e) {
			// ... do something here ...
			Sys.alert("Error", "Could not decode images!");
			e.printStackTrace();
			System.exit(0);
		}
		glClearColor(0.5f, .5f, .5f, 1f);
		//create our sprite batch
		batch = new SpriteBatch();
	}

	protected void render() throws LWJGLException {
		super.render();		
		
		//start the sprite batch
		batch.begin();

		//draw a tile from our sprite sheet
		batch.draw(tile, 10, 10);
		
		batch.draw(tile, 10, 100, 128, 128); //we can stretch it with a new width/height
		
		//we can also draw a region of a Texture on the fly like so:
		batch.drawRegion(tex, 0, 0, 32, 32, 	  //srcX, srcY, srcWidth, srcHeight
							   10, 250, 32, 32);  //dstX, dstY, dstWidth, dstHeight
		
		//tint batch red
		batch.setColor(Color.RED); 
		batch.draw(tex2, 200, 155);
		
		//reset color
		batch.setColor(Color.WHITE);

		//finish the sprite batch and push the tiles to the GPU
		batch.end();
	}
	

	protected void resize() throws LWJGLException {
		super.resize();
		batch.resize(Display.getWidth(), Display.getHeight());
	}
}