package mdesl.test;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_ONE;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL14.glBlendFuncSeparate;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT0;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

import mdesl.graphics.Color;
import mdesl.graphics.SpriteBatch;
import mdesl.graphics.Texture;
import mdesl.graphics.TextureRegion;
import mdesl.graphics.glutils.FrameBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

public class FBOTest extends SimpleGame {

	public static void main(String[] args) throws LWJGLException {
		Game game = new FBOTest();
		game.setDisplayMode(640, 480, false);
		game.start();
	}

	final int FBO_SIZE = 512;
	
	//our sprite batch
	SpriteBatch batch;

	Texture atlas;
	TextureRegion track, thumb;
	
	FrameBuffer fbo;
	TextureRegion fboRegion;
	
	protected void create() throws LWJGLException {
		super.create();
		//create our font
		try {
			atlas = new Texture(Util.getResource("res/slider.png"), Texture.NEAREST);
			
			//ideally you would use a texture packer like in LibGDX
			track = new TextureRegion(atlas, 0, 0, 64, 256);
			thumb = new TextureRegion(atlas, 65, 0, 64, 128);
			
			int width = track.getWidth();
			int height = track.getHeight();
			
			//create a new FBO with the width and height of our track
			if (Texture.isNPOTSupported()) {
				fbo = new FrameBuffer(width, height);
				fboRegion = new TextureRegion(fbo.getTexture());
			} else {
				int texWidth = Texture.toPowerOfTwo(width);
				int texHeight = Texture.toPowerOfTwo(height);
				fbo = new FrameBuffer(texWidth, texHeight);
				fboRegion = new TextureRegion(fbo.getTexture(), 0, texHeight-height, width, height);
			}
			fboRegion.flip(false, true);
			//GL uses lower left coords... we use upper-left for textures, so we need to flip Y 
			
		} catch (IOException e) {
			throw new RuntimeException("couldn't decode textures");
		}
		batch = new SpriteBatch();

		renderSlider();
	}
	
	protected void renderSlider() {
		//make our offscreen FBO the current buffer
		fbo.begin();
		
		//we need to first clear our FBO with transparent black
		glClearColor(0f,0f,0f,0f);
		glClear(GL_COLOR_BUFFER_BIT);
		
		//since our FBO is a different size than our Display, 
		//we need to resize our SpriteBatch in order for it to render correctly
		System.out.println(fbo.getWidth()+" "+fbo.getTexture().getWidth());

		batch.resize(fbo.getWidth(), fbo.getHeight());
		
		//setup our alpha blending to avoid blending twice
		glBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ONE);
		
		//start our batch
		batch.begin();
		
		//render some sprites to our offscreen FBO
		float x = 0;
		float y = 0;
		int val = 40; //example value amount
		
		//draw sprites
		batch.draw(track, x, y);
		batch.draw(thumb, x, y + val);

		//end (flush) our batch
		batch.end();

		//unbind the FBO
		fbo.end();

		//now we are back to screen aka "back buffer" rendering
		//so we should resize our SpriteBatch to the Display size
		batch.resize(Display.getWidth(), Display.getHeight());
		
		//now let's re-set blending to the default...
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
	}
	
	protected void render() throws LWJGLException {
		//nice smooth background color
		float L = 233/255f; 
		glClearColor(L, L, L, 1f);
		glClear(GL_COLOR_BUFFER_BIT);
		
		//we don't need super.render() since it just clears the screen, and we have that above
		
		//whenever the slider moves we will need to call renderSlider() to update the offscreen texture
		
		
		//render the offscreen texture with "premultiplied alpha" blending
		glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);
		
		batch.begin();
		
		//due to our different blend funcs we need to use RGBA to specify opacity
		//tinting becomes unavailable with this solution
		float a = .5f;
		batch.setColor(a, a, a, a);
		
		batch.draw(fboRegion, 0, 0);

		batch.flush();
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		batch.draw(track,  200, 200);
		batch.draw(thumb, 200, 250);
		
		batch.end();
	}

	// called to resize the display
	protected void resize() throws LWJGLException {
		super.resize();
		// resize our batch with the new screen size
		batch.resize(Display.getWidth(), Display.getHeight());
	}
}