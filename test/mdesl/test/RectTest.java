package mdesl.test;

import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.glClearColor;

import java.io.IOException;
import java.nio.ByteBuffer;

import mdesl.graphics.Color;
import mdesl.graphics.SpriteBatch;
import mdesl.graphics.Texture;
import mdesl.graphics.TextureRegion;
import mdesl.graphics.glutils.FrameBuffer;
import mdesl.graphics.text.BitmapFont;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

public class RectTest extends SimpleGame {

	public static void main(String[] args) throws LWJGLException {
		Game game = new RectTest();
		game.setDisplayMode(640, 480, false);
		game.start();
	}

	final int FBO_SIZE = 512;
	
	//a simple font to play with
	BitmapFont font;
	
	//our sprite batch
	SpriteBatch batch;

	Texture fontTex;
	TextureRegion rect;

	Color dark = new Color(0x4e4e4e);
	Color blue = new Color(0x96a2a5);
	Color light = new Color(0xc9c9c9);
	
	protected void create() throws LWJGLException {
		super.create();
		//create our font
		try {
			fontTex = new Texture(Util.getResource("res/ptsans_00.png"), Texture.NEAREST);
			
			//in Photoshop, we included a small white box at the bottom right of our font sheet
			//we will use this to draw lines and rectangles within the same batch as our text
			rect = new TextureRegion(fontTex, fontTex.getWidth()-2, fontTex.getHeight()-2, 1, 1);
			
			font = new BitmapFont(Util.getResource("res/ptsans.fnt"), fontTex);
		} catch (IOException e) {
			throw new RuntimeException("couldn't decode texture");
		}
		
		glClearColor(0.5f, .5f, .5f, 1f);
		batch = new SpriteBatch();
	}

	void drawRect(int x, int y, int width, int height, int thickness) {
		batch.draw(rect, x, y, width, thickness);
		batch.draw(rect, x, y, thickness, height);
		batch.draw(rect, x, y+height-thickness, width, thickness);
		batch.draw(rect, x+width-thickness, y, thickness, height);
	}
	
	void drawLine(int x1, int y1, int x2, int y2, int thickness) {
		int dx = x2-x1;
		int dy = y2-y1;
		float dist = (float)Math.sqrt(dx*dx + dy*dy);
		float rad = (float)Math.atan2(dy, dx);
		batch.draw(rect, x1, y1, dist, thickness, 0, 0, rad); 
	}
	
	
	protected void render() throws LWJGLException {
		super.render();
		
		batch.begin();
		
		int x = 25;
		int y = 50;
		int width = 250;
		int height = font.getLineHeight() * 2;
		batch.setColor(dark);
		batch.draw(rect, x, y, width, height);
		batch.setColor(blue);
		batch.draw(rect, x+=2, y+=2, width-=4, height-=4);
		batch.setColor(light);
		batch.draw(rect, x+=5, y+=5, width-=10, height-=10);
		batch.setColor(dark);
		
		String str = "Hello world, muchos jalepe\u00f1o";
		
		int fw = font.getWidth(str);
		int fx = x + width/2 - fw/2;
		int fy = y + height/2 - font.getLineHeight()/2;
		int base = fy+font.getBaseline() + 1;
		font.drawText(batch, str, fx, fy);
		
		//underline only a portion of the string
		fx += font.getWidth(str, 0, 6);
		fw = font.getWidth(str, 6, 11);
		
		//draw underline
		drawLine(fx, base, fx + fw, base, 1);
		
		//draw some other stuff
		batch.setColor(Color.WHITE);
		
		//test drawing a rectangle
		drawRect(180, y + 50, 50, 25, 3);
		
		//test drawing a rotated line
		batch.draw(rect, x + width + 30, 25, 100, 1, 0, 0, (float)Math.toRadians(45));
		
		//test drawing line from point A to point B
		batch.setColor(Color.PINK);
		drawLine(400, 300, Mouse.getX(), Display.getHeight()-Mouse.getY()-1, 3);
				
		batch.end();
	}

	// called to resize the display
	protected void resize() throws LWJGLException {
		super.resize();
		// resize our batch with the new screen size
		batch.resize(Display.getWidth(), Display.getHeight());
	}
}