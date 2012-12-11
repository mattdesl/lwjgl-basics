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

import mdesl.graphics.Color;
import mdesl.graphics.SpriteBatch;
import mdesl.graphics.Texture;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;

public class SpriteBatchExample extends SimpleGame {

	public static void main(String[] args) throws LWJGLException {
		Game game = new SpriteBatchExample();
		game.setDisplayMode(640, 480, false);
		game.start();
	}

	Texture tex, tex2;
	SpriteBatch batch;

	protected void create() throws LWJGLException {
		super.create();

		//Load some textures
		try {
			tex = new Texture(Util.getResource("res/tiles.png"), Texture.NEAREST);
			tex2 = new Texture(Util.getResource("res/font0.png"));
		} catch (IOException e) {
			throw new RuntimeException("couldn't decode textures");
		}
		
		//create our sprite batch
		batch = new SpriteBatch();
	}

	protected void render() throws LWJGLException {
		super.render();		
		
		//start the sprite batch
		batch.begin();

		//draw some tiles from our sprite sheet
		batch.drawRegion(tex, 64, 64, 64, 64, 	//source X,Y,WIDTH,HEIGHT
							  0, 0);			//destination X,Y (uses source size)
		batch.drawRegion(tex, 0, 0, 64, 64,		//source X,Y,WIDTH,HEIGHT
							  50, 70, 128, 128);//destination X,Y,WIDTH,HEIGHT

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