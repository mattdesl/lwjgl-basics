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

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

/**
 * A bare-bones implementation of a LWJGL application.
 * @author davedes
 */
public abstract class Game {
	
	// Whether our game loop is running
	protected boolean running = false;
	
	public void setDisplayMode(int width, int height, boolean fullscreen) throws LWJGLException {
		Display.setDisplayMode(new DisplayMode(width, height));
		Display.setFullscreen(fullscreen);
	}
		
	// Start our game
	public void start() throws LWJGLException {
		// Set up our display 
		Display.setTitle("Game");
		Display.setResizable(true);
		Display.setVSyncEnabled(true);
		
		Display.create();
		
		// Create our OpenGL context and initialize any resources
		create();
		
		// Starting size...
		resize();
		
		running = true;
		
		// While we're still running and the user hasn't closed the window... 
		while (running && !Display.isCloseRequested()) {
			// If the game was resized, we need to update our projection
			if (Display.wasResized())
				resize();
			
			// Render the game
			render();
			
			// Flip the buffers and sync to 60 FPS
			Display.update();
			Display.sync(60);
		}
		
		// Dispose any resources and destroy our window
		dispose();
		Display.destroy();
	}
	
	// Exit our game loop and close the window
	public void exit() {
		running = false;
	}
	
	// Called to setup our game and context
	protected abstract void create() throws LWJGLException;
	
	// Called to render our game
	protected abstract void render() throws LWJGLException;
	
	// Called to resize our game
	protected abstract void resize() throws LWJGLException;
	
	// Called to destroy our game upon exiting
	protected abstract void dispose() throws LWJGLException;
}