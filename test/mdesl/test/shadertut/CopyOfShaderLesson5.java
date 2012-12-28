package mdesl.test.shadertut;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;

import java.io.IOException;

import mdesl.graphics.FrameBuffer;
import mdesl.graphics.SpriteBatch;
import mdesl.graphics.Texture;
import mdesl.graphics.glutils.ShaderProgram;
import mdesl.test.Game;
import mdesl.test.SimpleGame;
import mdesl.test.Util;
import mdesl.util.MathUtil;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Matrix4f;

public class CopyOfShaderLesson5 extends SimpleGame {

	public static final int FBO_SIZE = 2048; 
	
	
	public static void main(String[] args) throws LWJGLException {
		Game game = new CopyOfShaderLesson5();
		game.setDisplayMode(480, 480, false);
		game.start();
	}

	//our texture to blur
	Texture tex, tex2;
	
	//our sprite batch
	SpriteBatch screenBatch;
	
	//another sprite batch with a smaller initial size
	//this will be used to render our effect
	SpriteBatch fboBatch;
	
	//We will blur in two passes, vertical and horizontal
	ShaderProgram hblur;
	ShaderProgram vblur;
	
	//we will ping pong between two FBOs
	FrameBuffer blurTarget;
	
	float radius = 3f;
	final static float MAX_BLUR = 10f;
	
	ShaderProgram loadShader(String vertSrc, String fragSrc, int resolution) throws IOException, LWJGLException {
		//create our shader program -- be sure to pass SpriteBatch's default attributes!
		ShaderProgram program = new ShaderProgram(vertSrc, fragSrc, SpriteBatch.ATTRIBUTES);
		
		//Good idea to log any warnings if they exist
		if (program.getLog().length()!=0)
			System.out.println(program.getLog());
		
		//now set up our uniforms
		program.use();
		
		//for horizontal this is the image width, for vertical this is height
		program.setUniformf("resolution", resolution);
		
		//sets the radius
		program.setUniformf("radius", radius);
		
		//u_texture will be set by SpriteBatch
		
		return program;
	}
	
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
			blurTarget = new FrameBuffer(FBO_SIZE, FBO_SIZE, Texture.NEAREST);
			
			//we'll use the same vertex source for both shaders
			final String VERT = Util.readFile(Util.getResourceAsStream("res/shadertut/lesson5.vert"));
			
			//load our frag source
			final String FRAG_HORIZONTAL = Util.readFile(Util.getResourceAsStream("res/shadertut/lesson5_horiz.frag"));
			final String FRAG_VERTICAL = Util.readFile(Util.getResourceAsStream("res/shadertut/lesson5_vert.frag"));
			
			//compile our shaders
			hblur = loadShader(VERT, FRAG_HORIZONTAL, FBO_SIZE);
			vblur = loadShader(VERT, FRAG_VERTICAL, FBO_SIZE);
			
			//Create a batch with the DEFAULT shader
			//We will use this for screen projection
			screenBatch = new SpriteBatch();
			
			//Create another batch for FBO projection
			//It only needs to hold 1 sprite per frame
			//we also pass "false" as a hint to not update uniforms
			fboBatch = new SpriteBatch(hblur, 1);
			
			//resizes the FBO batch to the proper size
			//note that this only sends uniform data to hblur shader
			fboBatch.resize(FBO_SIZE, FBO_SIZE);
		} catch (Exception e) { 
			//simple exception handling...
			e.printStackTrace();
			System.exit(0);
		}
	}

	
	protected void renderScene() {
		
	}
	
	protected void render() throws LWJGLException {
		glClearColor(0.5f, 0.5f, 0.5f, 1f);
		super.render();
		
		///// RENDER TO BLUR TARGET
//		blurTarget.begin();
//		
//		//clear target A
////		glClearColor(0f, 0f, 0f, 0f);
//		glClear(GL_COLOR_BUFFER_BIT);
//		
//		//first render our entire scene to FBO
//		fboBatch.setShader(SpriteBatch.getDefaultShader());
//		fboBatch.resize(FBO_SIZE, FBO_SIZE);
//		
//		fboBatch.begin();
//		
//		//draw the original texture to targetA, applying the hblur
//		fboBatch.draw(tex, 150, 150);
//		
//		//draw something else to be blurred
//		fboBatch.draw(tex2, 150 + tex.getWidth(), 40);
//		
//		//flush the hblur, rendering it to the FBO
//		fboBatch.flush();
//			
//		///// FINISH RENDERING TO SCENE TARGET
//		blurTarget.end();
//		
//		
//		//swap to our hblur shader without updating any uniforms
//		//(we update them ourself on resize)
//		fboBatch.setShader(hblur);
//		
//		//begin our hblur shader batch
//		fboBatch.begin();
//		
//		//update radius based on mouse x axis
//		float mouseXAmt = Mouse.getX() / (float)Display.getWidth();
//		hblur.setUniformf("radius", mouseXAmt * MAX_BLUR);
//		
//		fboBatch.end();
//
//		//now we'll render to screen using vertical blur
//		screenBatch.setShader(vblur);
//		
//		screenBatch.begin();
//
//		//update radius based on mouse x axis
//		vblur.setUniformf("radius", mouseXAmt * MAX_BLUR);
//		
//		//now render targetA to the screen, applying the vertical blur
//		screenBatch.draw(blurTarget, 0, 0); 
//		
//		//now we switch to the default shader, which will flush our batch
//		screenBatch.setShader(SpriteBatch.getDefaultShader());
//		
//		//finish rendering to screen
//		screenBatch.end();
	}

	// called to resize the display
	protected void resize() throws LWJGLException {
		super.resize();
		
		int width = Display.getWidth();
		int height = Display.getHeight();

		//update our regular batch, will send uniform data
		
		screenBatch.resize(width, height);
		
		
	}
}