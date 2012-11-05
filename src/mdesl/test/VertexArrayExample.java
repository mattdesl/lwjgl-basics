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
			tex = new Texture(getResource("res/small.png"), Texture.LINEAR);
			tex2 = new Texture(getResource("res/font0.png"));
		} catch (IOException e) {
			throw new RuntimeException("couldn't decode textures");
		}
		
		batch = new SpriteBatch(1000);
	}
	
	protected void render() {
		glClear(GL_COLOR_BUFFER_BIT);
		batch.begin();
		
		batch.draw(tex, 50, 50);
		batch.draw(tex, 150, 150, tex.width*2, tex.height*2);
		
		batch.draw(tex2, 350, 25);
		
		batch.setColor(1f, 0f, 0f, 1f); //tint red
		batch.drawRegion(tex2, 25, 50, 100, 32, 
							350, 350);  
		batch.setColor(1f, 1f, 1f, 1f); //reset color..
		
		batch.end();
		
		//simple debugging...
		//Display.setTitle("Render calls: "+batch.renderCalls);
		//batch.renderCalls = 0;
	}
}