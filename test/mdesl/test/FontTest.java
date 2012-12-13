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
import mdesl.graphics.text.BitmapFont;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.opengl.Display;

public class FontTest extends SimpleGame {

	public static void main(String[] args) throws LWJGLException {
		Game game = new FontTest();
		game.setDisplayMode(640, 480, false);
		game.start();
	}

	BitmapFont font;
	SpriteBatch batch;

	protected void create() throws LWJGLException {
		super.create();

		//Load some textures
		try {
			font = new BitmapFont(Util.getResource("res/ptsans.fnt"), Util.getResource("res/ptsans_00.png"));
		} catch (IOException e) {
			// ... do something here ...
			Sys.alert("Error", "Could not decode font!");
			e.printStackTrace();
			System.exit(0);
		}
		//create our sprite batch
		batch = new SpriteBatch();
	}

	protected void render() throws LWJGLException {
		super.render();		
		
		//start the sprite batch
		batch.begin();
		
		batch.setColor(Color.WHITE);
		font.drawText(batch, "The quick brown fox jumps over the lazy dog!", 10, 10);
		
		//testing some unicode values
		batch.setColor(Color.GRAY);
		font.drawText(batch, "\u2122\u00e2\u00C9\u0110\u2082\u2264", 10, font.getLineHeight() + 10);
		
		batch.end();
	}
	

	protected void resize() throws LWJGLException {
		super.resize();
		batch.resize(Display.getWidth(), Display.getHeight());
	}
}