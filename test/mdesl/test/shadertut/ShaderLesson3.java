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

public class ShaderLesson3 extends SimpleGame {

	public static void main(String[] args) throws LWJGLException {
		Game game = new ShaderLesson3();
		game.setDisplayMode(320, 240, false);
		game.start();
	}

	//our texture
	Texture tex;
	
	//our sprite batch
	SpriteBatch batch;

	//our program
	ShaderProgram program;
	
	protected void create() throws LWJGLException {
		super.create();
		
		//our small demo will use a fixed-size display
		Display.setResizable(false);

		//In later lessons, we'll learn about "post-processing" an entire scene using an FBO. 
		//For now we will apply the concepts to individual textures.
		try {
			tex = new Texture(Util.getResource("res/scene.png"), Texture.NEAREST);
		} catch (IOException e) {
			throw new RuntimeException("couldn't decode texture");
		}
		
		//load our shader program and sprite batch
		try {
			final String VERTEX = Util.readFile(Util.getResourceAsStream("res/shadertut/lesson3.vert"));
			final String FRAGMENT = Util.readFile(Util.getResourceAsStream("res/shadertut/lesson3.frag"));
			
			//create our shader program -- be sure to pass SpriteBatch's default attributes!
			program = new ShaderProgram(VERTEX, FRAGMENT, SpriteBatch.ATTRIBUTES);
						
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
		
		batch.begin();
		
		//draw the texture at top left
		batch.draw(tex, 0, 0);
		
		batch.end();
	}

	// called to resize the display
	protected void resize() throws LWJGLException {
		super.resize();

		// resize our batch with the new screen size
		batch.resize(Display.getWidth(), Display.getHeight());
		
		// whenever our screen resizes, we need to update our uniform
		program.use();
		program.setUniformf("resolution", Display.getWidth(), Display.getHeight());
	}
}