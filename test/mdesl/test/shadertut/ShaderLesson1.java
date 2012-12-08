package mdesl.test.shadertut;

import java.io.IOException;

import mdesl.graphics.SpriteBatch;
import mdesl.graphics.Texture;
import mdesl.graphics.glutils.ShaderProgram;
import mdesl.test.Game;
import mdesl.test.SimpleGame;
import mdesl.test.Util;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;

public class ShaderLesson1 extends SimpleGame {

	public static void main(String[] args) throws LWJGLException {
		Game game = new ShaderLesson1();
		game.setDisplayMode(800, 600, false);
		game.start();
	}

	//our texture
	Texture tex;
	
	//our sprite batch
	SpriteBatch batch;

	protected void create() throws LWJGLException {
		super.create();

		// Load our textures
		try {
			tex = new Texture(Util.getResource("res/grass.png"), Texture.NEAREST);
		} catch (IOException e) {
			throw new RuntimeException("couldn't decode texture");
		}
		
		//load our shader program and sprite batch
		try {
			final String VERTEX = Util.readFile(Util.getResourceAsStream("res/shadertut/lesson1.vert"));
			final String FRAGMENT = Util.readFile(Util.getResourceAsStream("res/shadertut/lesson1.frag"));
			
			//create our shader program -- be sure to pass SpriteBatch's default attributes!
			ShaderProgram program = new ShaderProgram(VERTEX, FRAGMENT, SpriteBatch.ATTRIBUTES);
			
			//create our sprite batch
			batch = new SpriteBatch(program);
		} catch (Exception e) { 
			//simple exception handling...
			e.printStackTrace();
			System.exit(0);
		}
	}

	protected void render() throws LWJGLException {
		super.render();

		// Begin rendering:
		batch.begin();

		//draw some sprites
		batch.draw(tex, 10, 10);		
		batch.drawRegion(tex, 0, 0, 32, 32, 100, 350, 32, 32); 

		batch.end();
	}

	// called to resize the display
	protected void resize() throws LWJGLException {
		super.resize();

		// resize our batch with the new screen size
		batch.resize(Display.getWidth(), Display.getHeight());
	}
}