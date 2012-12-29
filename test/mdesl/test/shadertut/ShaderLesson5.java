package mdesl.test.shadertut;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;

import java.io.IOException;

import mdesl.graphics.SpriteBatch;
import mdesl.graphics.Texture;
import mdesl.graphics.glutils.FrameBuffer;
import mdesl.graphics.glutils.ShaderProgram;
import mdesl.test.Game;
import mdesl.test.SimpleGame;
import mdesl.test.Util;
import mdesl.util.MathUtil;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Matrix4f;

public class ShaderLesson5 extends SimpleGame {
	
	//should be at least as large as our display
	public static final int FBO_SIZE = 1024;
	
	public static void main(String[] args) throws LWJGLException {
		Game game = new ShaderLesson5();
		game.setDisplayMode(800, 600, false);
		game.start();
	}

	//our texture to blur
	Texture tex, tex2;
	
	//we'll use a single batch for everything
	SpriteBatch batch;
	
	//our blur shader
	ShaderProgram blurShader;
	
	//our offscreen buffers
	FrameBuffer blurTargetA, blurTargetB;
	
	float radius = 3f;
	final static float MAX_BLUR = 3f;
	
	protected void create() throws LWJGLException {
		super.create();
		
		try {
			//load our texture with linear filter
			tex = new Texture(Util.getResource("res/slider.png"), Texture.LINEAR);
			tex2 = new Texture(Util.getResource("res/tiles.png"), Texture.LINEAR);
		} catch (IOException e) {
			throw new RuntimeException("couldn't decode texture");
		}
		
		//our simple demo won't support display resizing
		Display.setResizable(false);
		
		//load our shader program and sprite batch
		try {
			//create our FBOs
			blurTargetA = new FrameBuffer(FBO_SIZE, FBO_SIZE, Texture.LINEAR);
			blurTargetB = new FrameBuffer(FBO_SIZE, FBO_SIZE, Texture.LINEAR);
			
			//our basic pass-through vertex shader
			final String VERT = Util.readFile(Util.getResourceAsStream("res/shadertut/lesson5.vert"));

			//our fragment shader, which does the blur in one direction at a time
			final String FRAG = Util.readFile(Util.getResourceAsStream("res/shadertut/lesson5.frag"));

			//create our shader program
			blurShader = new ShaderProgram(VERT, FRAG, SpriteBatch.ATTRIBUTES);

			//Good idea to log any warnings if they exist
			if (blurShader.getLog().length()!=0)
				System.out.println(blurShader.getLog());

			//always a good idea to set up default uniforms...
			blurShader.use();
			blurShader.setUniformf("dir", 0f, 0f); //direction of blur; nil for now
			blurShader.setUniformf("resolution", FBO_SIZE); //size of FBO texture
			blurShader.setUniformf("radius", radius); //radius of blur
			
			batch = new SpriteBatch();
		} catch (Exception e) { 
			//simple exception handling...
			e.printStackTrace();
			System.exit(0);
		}
	}

	
	protected void drawEntities(SpriteBatch batch) {
		batch.draw(tex, 50, 50);
		batch.draw(tex2, tex.getWidth()+20, 100);
	}
	
	void renderScene() throws LWJGLException {
		//Bind FBO target A
		blurTargetA.begin();
		
		//Clear FBO A with an opaque colour to minimize blending issues
		glClearColor(0.5f, 0.5f, 0.5f, 1f);
		glClear(GL_COLOR_BUFFER_BIT);
		
		//Reset batch to default shader (without blur)
		batch.setShader(SpriteBatch.getDefaultShader());
		
		//send the new projection matrix (FBO size) to the default shader
		batch.resize(blurTargetA.getWidth(), blurTargetA.getHeight());
		
		//now we can start our batch
		batch.begin();
		
		//render our scene fully to FBO A
		drawEntities(batch);
		
		//flush the batch, i.e. render entities to GPU
		batch.flush();
		
		//After flushing, we can finish rendering to FBO target A
		blurTargetA.end();
	}
	
	void horizontalBlur() throws LWJGLException {
		//swap the shaders
		//this will send the batch's (FBO-sized) projection matrix to our blur shader
		batch.setShader(blurShader);
		
		//ensure the direction is along the X-axis only
		blurShader.setUniformf("dir", 1f, 0f);
		
		//determine radius of blur based on mouse position
		float mouseXAmt = Mouse.getX() / (float)Display.getWidth();
		blurShader.setUniformf("radius", mouseXAmt * MAX_BLUR);
		
		//start rendering to target B
		blurTargetB.begin();
		
		//no need to clear since targetA has an opaque background
		//render target A (the scene) using our horizontal blur shader
		//it will be placed into target B
		batch.draw(blurTargetA, 0, 0);
		
		//flush the batch before ending target B
		batch.flush();
		
		//finish rendering target B
		blurTargetB.end();
	}
	
	void verticalBlur() throws LWJGLException {
		//now we can render to the screen using the vertical blur shader

		//send the screen-size projection matrix to the blurShader
		batch.resize(Display.getWidth(), Display.getHeight());
		
		//apply the blur only along Y-axis
		blurShader.setUniformf("dir", 0f, 1f);
		
		//update Y-axis blur radius based on mouse
		float mouseYAmt = (Display.getHeight()-Mouse.getY()-1) / (float)Display.getHeight();
		blurShader.setUniformf("radius", mouseYAmt * MAX_BLUR);
		
		//draw the horizontally-blurred FBO B to the screen, applying the vertical blur as we go
		batch.draw(blurTargetB, 0, 0);
		
		batch.end();
	}
	
	protected void render() throws LWJGLException {
		//render scene to FBO A
		renderScene();
		
		//render FBO A to FBO B, using horizontal blur
		horizontalBlur();
		
		//render FBO B to scene, using vertical blur
		verticalBlur();
	}

	// called to resize the display
	protected void resize() throws LWJGLException {
		super.resize();
		//we will call batch.resize() in render
	}
}