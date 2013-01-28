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
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

/**
 * A bare-bones implementation of a LWJGL application.
 * @author davedes
 */
public abstract class Game {
	
	// Whether our game loop is running
	protected boolean running = false;
	
	
	/** time at last frame */
	private long lastFrame;
	private int fpsTick;
	/** frames per second */
	private int fps;
	/** last fps time */
	private long lastFPS;
	/** the delta time in milliseconds */
	private int delta;
	
	
	private boolean mouseWasDown = false;
	private int lastMouseX, lastMouseY;
	
	public void setDisplayMode(int width, int height) throws LWJGLException {
		Display.setDisplayMode(new DisplayMode(width, height));
	}
	
	public void setDisplayMode(int width, int height, boolean fullscreen) throws LWJGLException {
		setDisplayMode(width, height);
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
		
		delta = tick();
		lastFPS = getTime();
		
		// While we're still running and the user hasn't closed the window... 
		while (running && !Display.isCloseRequested()) {
			delta = tick();
			
			// If the game was resized, we need to update our projection
			if (Display.wasResized())
				resize();
			
			Keyboard.poll();
			while (Keyboard.next()) {
				char c = Keyboard.getEventCharacter();
				int k = Keyboard.getEventKey();
				boolean down = Keyboard.getEventKeyState();
				if (down)
					keyPressed(k, c);
				else
					keyReleased(k, c);
			}
			Mouse.poll();
			while (Mouse.next()) {
				int btn = Mouse.getEventButton();
				boolean btnDown = Mouse.getEventButtonState();
				int wheel = Mouse.getEventDWheel();
				int x = Mouse.getEventX();
				int y = Mouse.getEventY();
//				System.out.println(x+" "+y+" "+dx+" "+dy+" "+wheel+" "+btnDown);
				
				if (btnDown) {
					mouseWasDown = true;
					mousePressed(x, y, btn);
				} else if (mouseWasDown) {
					mouseWasDown = false;
//					mouseReleased(x, y, btn);	
				}
				if (lastMouseX!=x || lastMouseY!=y) {
//					mouseMoved(x, y, lastMouseX, lastMouseY);
					lastMouseX = x;
					lastMouseY = y;						
				}
				
				if (wheel!=0) {
					mouseWheelChanged(wheel/120);
				}
			}
			
			// Render the game
			render();
			
			//update FPS ticker
			updateFPS();
			
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
	
	protected void keyPressed(int key, char c) {
		
	}
	
	protected void keyReleased(int key, char c) {
		
	}
	
//	protected void mouseMoved(int x, int y, int ox, int oy) {
//		System.out.println("moved");
//	}
	
	protected void mousePressed(int x, int y, int button) {
	
	}
//	
//	protected void mouseReleased(int x, int y, int button) {
//		System.out.println("rel");
//	}
	
	protected void mouseWheelChanged(int delta) {
	}
	
	// Called to setup our game and context
	protected abstract void create() throws LWJGLException;
	
	// Called to render our game
	protected abstract void render() throws LWJGLException;
	
	// Called to resize our game
	protected abstract void resize() throws LWJGLException;
	
	// Called to destroy our game upon exiting
	protected abstract void dispose() throws LWJGLException;
	
	public int getDeltaTime() {
		return delta;
	}
	
	public int getFPS() {
		return fps;
	}
	
	private int tick() {
		long time = getTime();
		int delta = (int)(time - lastFrame);
		lastFrame = time;
		return delta;
	}
	
	public void updateFPS() {
		if (getTime() - lastFPS > 1000) {
			fps = fpsTick;
			fpsTick = 0;
			lastFPS += 1000;
		}
		fpsTick++;
	}

	/**
	 * Get the time in milliseconds
	 * 
	 * @return The system time in milliseconds
	 */
	public long getTime() {
	    return (Sys.getTime() * 1000) / Sys.getTimerResolution();
	}
}