package mdesl.test.shadertut;

import java.io.IOException;

import mdesl.graphics.SpriteBatch;
import mdesl.graphics.Texture;
import mdesl.graphics.glutils.ShaderProgram;
import mdesl.test.Game;
import mdesl.test.SimpleGame;
import mdesl.util.Util;

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
			tex = new Texture(Util.getResource("res/grass.png"), Texture.LINEAR);
		} catch (IOException e) {
			throw new RuntimeException("couldn't decode texture");
		}

		// As explained in SpriteBatch docs, we will use the following
		// attributes:
		
		// create a new shader program
		// ShaderProgram program = new ShaderProgram(VERTEX, FRAGMENT,
		// SpriteBatch.ATTRIBUTES);
		batch = new SpriteBatch(1000);
	}

	protected void render() throws LWJGLException {
		super.render();

		// Begin rendering:
		batch.begin();

		// batch.draw(tex, 10, 10);

		batch.end();
	}

	// called to resize the display
	protected void resize() throws LWJGLException {
		super.resize();

		// resize our batch with the new screen size
		batch.resize(Display.getWidth(), Display.getHeight());
	}
}