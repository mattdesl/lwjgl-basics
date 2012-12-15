package mdesl.test.shadertut;

import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.GL_TEXTURE1;
import static org.lwjgl.opengl.GL13.GL_TEXTURE2;
import static org.lwjgl.opengl.GL13.glActiveTexture;

import java.io.IOException;

import mdesl.graphics.SpriteBatch;
import mdesl.graphics.Texture;
import mdesl.graphics.glutils.ShaderProgram;
import mdesl.test.Game;
import mdesl.test.SimpleGame;
import mdesl.test.Util;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;

public class ShaderLesson4B extends SimpleGame {

	public static void main(String[] args) throws LWJGLException {
		Game game = new ShaderLesson4B();
		game.setDisplayMode(640, 480, false);
		game.start();
	}

	//our grass texture
	Texture tex0;
	
	//our dirt texture
	Texture tex1;
	
	//our mask texture
	Texture mask;
	
	//our sprite batch
	SpriteBatch batch;

	//our program
	ShaderProgram program;
	
	//our very simple timing mechanism which we'll send to the shader
	float tick = 0;
	
	protected void create() throws LWJGLException {
		super.create();
		
		try {
			tex0 = new Texture(Util.getResource("res/grass.png"), Texture.NEAREST, Texture.REPEAT);
			tex1 = new Texture(Util.getResource("res/dirt.png"), Texture.NEAREST, Texture.REPEAT);
			mask = new Texture(Util.getResource("res/mask.png"), Texture.NEAREST, Texture.REPEAT);
		} catch (IOException e) {
			throw new RuntimeException("couldn't decode textures");
		}
		
		//load our shader program and sprite batch
		try {
			final String VERTEX = Util.readFile(Util.getResourceAsStream("res/shadertut/lesson4.vert"));
			final String FRAGMENT = Util.readFile(Util.getResourceAsStream("res/shadertut/lesson4b.frag"));
			
			//create our shader program -- be sure to pass SpriteBatch's default attributes!
			program = new ShaderProgram(VERTEX, FRAGMENT, SpriteBatch.ATTRIBUTES);
			
			//Good idea to log any warnings if they exist
			if (program.getLog().length()!=0)
				System.out.println(program.getLog());
			
			//bind our program
			program.use();
			
			//set our sampler2D uniforms
			program.setUniformi("u_texture1", 1);
			program.setUniformi("u_mask", 2);
			program.setUniformf("time", tick);
			
			//create our sprite batch
			batch = new SpriteBatch(program);
		} catch (Exception e) { 
			//simple exception handling...
			e.printStackTrace();
			System.exit(0);
		}
		
		
		//make GL_TEXTURE2 the active texture unit, then bind our mask texture
		glActiveTexture(GL_TEXTURE2);
		mask.bind();
		
		//do the same for our other two texture units
		glActiveTexture(GL_TEXTURE1);
		tex1.bind();
		
		glActiveTexture(GL_TEXTURE0);
		tex0.bind();
		
		//gray clear colour
		glClearColor(0.5f, 0.5f, 0.5f, 1.0f);
	}
	
	protected void render() throws LWJGLException {
		super.render();
		
		batch.begin();
		
		//shader will be bound already
		program.setUniformf("time", tick+=0.01f);
		
		batch.draw(tex0, 10, 10);
		
		batch.end();
	}
	
	// called to resize the display
	protected void resize() throws LWJGLException {
		super.resize();

		// resize our batch with the new screen size
		batch.resize(Display.getWidth(), Display.getHeight());
	}
}