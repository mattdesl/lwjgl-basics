package mdesl.test.shadertut;

import java.io.IOException;

import mdesl.graphics.Color;
import mdesl.graphics.SpriteBatch;
import mdesl.graphics.Texture;
import mdesl.graphics.glutils.ShaderProgram;
import mdesl.test.Game;
import mdesl.test.SimpleGame;
import mdesl.test.Util;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;

public class ShaderLesson3 extends SimpleGame {

	public static void main(String[] args) throws LWJGLException {
		Game game = new ShaderLesson3();
		game.setDisplayMode(640, 480, false);
		game.start();
	}

	//our texture
	Texture tex;
	
	//our sprite batch
	SpriteBatch batch;

	protected void create() throws LWJGLException {
		super.create();

		//this will be ignored in this lesson...
		try {
			tex = new Texture(Util.getResource("res/grass.png"), Texture.NEAREST);
		} catch (IOException e) {
			throw new RuntimeException("couldn't decode texture");
		}
		
		//load our shader program and sprite batch
		try {
			final String VERTEX = Util.readFile(Util.getResourceAsStream("res/shadertut/lesson2.vert"));
			final String FRAGMENT = Util.readFile(Util.getResourceAsStream("res/shadertut/lesson2.frag"));
			
			//create our shader program -- be sure to pass SpriteBatch's default attributes!
			ShaderProgram program = new ShaderProgram(VERTEX, FRAGMENT, SpriteBatch.ATTRIBUTES);
			
			//Good idea to log any warnings if they exist
			if (program.getLog().length()!=0)
				System.out.println(program.getLog());
			
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
		batch.setColor(Color.BLUE);
		batch.draw(tex, 10, 10, 32, 32);
		
		batch.setColor(Color.RED);
		batch.draw(tex, 10, 50, 16, 16);
		
		batch.end();
	}

	// called to resize the display
	protected void resize() throws LWJGLException {
		super.resize();

		// resize our batch with the new screen size
		batch.resize(Display.getWidth(), Display.getHeight());
	}
}