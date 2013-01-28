package mdesl.test;

import static org.lwjgl.opengl.GL11.GL_BACK;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_COLOR_LOGIC_OP;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_FRONT;
import static org.lwjgl.opengl.GL11.GL_FRONT_FACE;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_TRIANGLE_STRIP;
import static org.lwjgl.opengl.GL11.GL_XOR;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glCullFace;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glLogicOp;

import java.awt.Canvas;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.List;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import mdesl.graphics.Color;
import mdesl.graphics.SpriteBatch;
import mdesl.graphics.Texture;
import mdesl.graphics.TextureRegion;
import mdesl.graphics.glutils.ShaderProgram;
import mdesl.graphics.glutils.VertexArray;
import mdesl.graphics.glutils.VertexAttrib;
import mdesl.graphics.glutils.VertexData;
import mdesl.graphics.text.BitmapFont;
import mdesl.util.MathUtil;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

public class TextureRepeat extends SimpleGame implements FileDrop.Listener {

	static Preferences prefs = Preferences.userNodeForPackage(TextureRepeat.class);
	
	public static void main(String[] args) throws LWJGLException {
		final TextureRepeat game = new TextureRepeat();
		
		final JFrame f = new JFrame();
		f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		f.addWindowListener(new WindowAdapter() {
			
			@Override
			public void windowClosing(WindowEvent arg0) {
				
			}
			
			@Override
			public void windowClosed(WindowEvent arg0) {
				game.exit();
			}
		});
		f.setTitle("Test");
		
		final Canvas c = new Canvas();
		
		f.add(c);
		f.setSize(prefs.getInt("window.width", 800), 
			      prefs.getInt("window.height", 600));
		f.setBackground(java.awt.Color.gray);
		Display.setParent(c);
		
		f.setLocationRelativeTo(null);
		f.setVisible(true);
		
		
		new FileDrop(f, game);
		
		game.setDisplayMode(f.getWidth(), f.getHeight(), false);
		
		game.start();
	}
	
	final int FBO_SIZE = 512;
	
	//a simple font to play with
	BitmapFont font;
	
	//our sprite batch
	SpriteBatch batch;

	URL url;
	File file;
	Texture tex;
	
	final int DEFAULT_SCALE_INDEX = 5;
	int scale = DEFAULT_SCALE_INDEX;
	float[] scales = { 0.05f, 0.15f, 0.25f, 0.5f, 0.75f, 1f, 2f, 3f, 4f, 5f, 10f, 12.5f, 15f, 20f };
	boolean grid = false;
	
	Color gridColor = new Color(1f,1f,1f, 1f); 
	Color checkColor = new Color(0.85f, 0.85f, 0.85f, 1f);
	Color uiColor = new Color(0f, 0f, 0f, 0.5f);
	TextureRegion rect;
	
	int time = 0;
	int pollDelay = 500;
	int curFilter = Texture.LINEAR;
	
	String errStr = "";
	
	boolean reload = false;
	int fpsWidth;
	private boolean help;
	private long lastModified;
	private boolean polling = true;
	boolean inFocus;
	private boolean checkBG = true;

	private TerrainMesh terrain;

	private boolean showTerrain = false;
	
	public void dispose() throws LWJGLException {
		if (tex!=null)
			tex.dispose();
		font.dispose();
		batch.getShader().dispose();
		prefs.putBoolean("linear", curFilter==Texture.LINEAR);		
		prefs.putBoolean("grid", grid);
		prefs.putBoolean("polling", polling);
		prefs.putBoolean("checkBG", checkBG);
		prefs.put("url", url!=null ? url.getPath() : "");
		prefs.putInt("window.width", Display.getWidth()); 
	    prefs.putInt("window.height", Display.getHeight());
		try {
			prefs.flush();
		} catch (BackingStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void create() throws LWJGLException {
		super.create();
		
		curFilter = prefs.getBoolean("linear", true) ? Texture.LINEAR : Texture.NEAREST;
		grid = prefs.getBoolean("grid", true);
		checkBG = prefs.getBoolean("checkBG", true);
		polling = prefs.getBoolean("polling", true);
		try {
			Texture fontTex = new Texture(Util.getResource("res/ptsans_00.png"));
			font = new BitmapFont(Util.getResource("res/ptsans.fnt"), fontTex);
			
			String path = prefs.get("url", null);
			if (path!=null&&path.length()!=0) {
				try {
					File file = new File(URLDecoder.decode(path, "UTF-8"));
					if (file.exists()) {
						url = file.toURI().toURL();
						this.file = file;
						lastModified = file.lastModified();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (url!=null)
				reload();
			
			terrain = new TerrainMesh(Util.getResource("res/height.png"));
			terrain.create(1, 55f, 25f);
			
			batch = new SpriteBatch();
			
			//in Photoshop, we included a small white box at the bottom right of our font sheet
			//we will use this to draw lines and rectangles within the same batch as our text
			rect = new TextureRegion(fontTex, fontTex.getWidth()-2, fontTex.getHeight()-2, 1, 1);
			
			fpsWidth = font.getWidth("FPS: 0000");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		glDisable(GL_CULL_FACE);
//		glCullFace(GL_BACK);
		glClearColor(0f, 1f, 1f, 1f);
	}
	
	public void reload() {
		if (url==null)
			return;
		if (!file.canRead()) {
			errStr = "Could not read file";
			return;
		}
		if (!file.exists()) {
			errStr = "File no longer exists...";
			url = null;
			file = null;
			return;
		}				
		try {
			if (tex!=null)
				tex.dispose();
			tex = new Texture(url, curFilter, Texture.REPEAT);
			errStr = "";
		} catch (IOException e) {
			e.printStackTrace();
			errStr = "Error decoding texture: "+e.getMessage();
		}
	}
	
	public void resize() throws LWJGLException {
		batch.resize(Display.getWidth(), Display.getHeight());
		setDisplayMode(Display.getWidth(), Display.getHeight());
	}
	
	public void render() throws LWJGLException {
		//super.render();
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		
		inFocus = Display.isActive();
		
		time += getDeltaTime();
		if (time > pollDelay) {
			if (polling && url!=null) {
				if (file.lastModified()!=lastModified) {
					reload();
				}
			}
			time -= pollDelay;
		}
		
		if (reload) {
			reload();
			reload = false;
		}
		
		batch.begin();
		
		if (checkBG) {
			int size = Math.max((int)(16 * scales[scale]), 4);
			int count = Math.max(Display.getWidth(), Display.getHeight())/size + 1;
			count = count%2==0 ? count+1 : count;
			batch.setColor(checkColor);			
			for (int x=0, pos=0; x<count; x++) {
				for (int y=0; y<count; y++) {
					if (pos%2==0)
						batch.draw(rect, x*size, y*size, size, size);
					pos++;
				}
			}
		}
		
		if (tex!=null) {
			int texWidth = Math.max(1, (int)(scales[scale] * tex.getWidth()));
			int texHeight = Math.max(1, (int)(scales[scale] * tex.getHeight()));
			int tilesX = Math.max(1, Display.getWidth()/texWidth+1);
			int tilesY = Math.max(1, Display.getHeight()/texHeight+1);
			
			//draw textures in one batch
			
			if (showTerrain) {
				batch.end();
				glEnable(GL_DEPTH_TEST);
				terrain.render();
				glDisable(GL_DEPTH_TEST);
				batch.begin();
			} else {
				batch.setColor(Color.WHITE);
				for (int x=0; x<tilesX; x++) {
					for (int y=0; y<tilesY; y++) {
						batch.draw(tex, texWidth*x, texHeight*y, texWidth, texHeight);
					}
				}
				
				batch.flush();
			}
			
			glEnable(GL_COLOR_LOGIC_OP);
			glLogicOp(GL_XOR);
			
			if (grid) {
				batch.setColor(gridColor);
				//draw lines in another batch
				for (int x=0; x<tilesX; x++) 
					batch.draw(rect, texWidth*x, 0, 1, Display.getHeight());
				for (int y=0; y<tilesY; y++) 
					batch.draw(rect, 0, texHeight*y, Display.getWidth(), 1);
			}
			
			batch.flush();
			glDisable(GL_COLOR_LOGIC_OP);
		}
		
		
		batch.setColor(uiColor);
		batch.draw(rect, 0, 0, Display.getWidth(), font.getLineHeight()*2+10);
		
		batch.setColor(Color.WHITE);
		font.drawText(batch, "Scale: "+(int)(100f*scales[scale])+"%", 5, 5);
		
		String str = "FPS: "+getFPS();
		font.drawText(batch, str, Display.getWidth()-font.getWidth(str)-5, 5);
		
		str = file!=null ? file.getName() : "(Drag and drop a PNG file to view)";
		
		font.drawText(batch, str, Display.getWidth()/2 - font.getWidth(str)/2, 5);
		font.drawText(batch, inFocus ? "Click anywhere for help" : "(click to gain focus)", 5, font.getLineHeight()+5);
		
		if (errStr.length()!=0) {
			int w = font.getWidth(errStr)+10;
			int h = font.getLineHeight()+10;
			int x = Display.getWidth()/2 - w/2;
			int y = Display.getHeight()/2 - h/2;

			batch.setColor(uiColor);
			batch.draw(rect, x, y, w, h);
			x += 5;
			y += 5;
			batch.setColor(Color.RED);
			font.drawText(batch, errStr, x, y);
		} else if (help) {
			int w = 300;
			int h = font.getLineHeight()*7+10;
			int x = Display.getWidth()/2 - w/2;
			int y = Display.getHeight()/2 - h/2;

			batch.setColor(uiColor);
			batch.draw(rect, x, y, w, h);
			x += 5;
			y += 5;
			batch.setColor(Color.WHITE);
			font.drawText(batch, "Up/Down Arrows - Change zoom level", x, y);
			font.drawText(batch, "Space - Reset zoom level", x, y+=font.getLineHeight());
			font.drawText(batch, "F - Set Filter: "+(curFilter==Texture.NEAREST ? "NEAREST" : "LINEAR"), x, y+=font.getLineHeight());
			font.drawText(batch, "G - Toggle grid: "+(grid?"on":"off"), x, y+=font.getLineHeight());
			font.drawText(batch, "C - Toggle checkered background: "+(checkBG?"on":"off"), x, y+=font.getLineHeight());
			font.drawText(batch, "P - Toggle automatic reload (file watch): "+(polling?"on":"off"), x, y+=font.getLineHeight());
			font.drawText(batch, "R - Reload current image", x, y+=font.getLineHeight());
		}
		
		batch.end();
	}
	
	public void keyPressed(int k, char c) {
		if (k==Keyboard.KEY_SPACE) {
			scale = DEFAULT_SCALE_INDEX;
		} else if (k==Keyboard.KEY_UP) {
			if (scale<scales.length-1)
				scale++;
		} else if (k==Keyboard.KEY_DOWN) {
			if (scale>0)
				scale--;
		} else if (k==Keyboard.KEY_G) {
			grid = !grid;
		} else if (k==Keyboard.KEY_F) {
			curFilter = curFilter==Texture.LINEAR ? Texture.NEAREST : Texture.LINEAR;
			if (tex!=null)
				tex.setFilter(curFilter);
		} else if (k==Keyboard.KEY_R) {
			reload();
		} else if (k==Keyboard.KEY_C) {
			checkBG = !checkBG;
		} else if (k==Keyboard.KEY_ESCAPE) {
			url = null;
			file = null;			
			tex.dispose();
			tex = null;
		} else if (k==Keyboard.KEY_P){
			polling = !polling;
		} else if (k==Keyboard.KEY_T) {
			showTerrain = !showTerrain;
		}
	}
	
	public void mouseWheelChanged(int delta) {
		if (delta > 0 && scale<scales.length-1) {
			scale++;
		} else if (delta < 0 && scale>0)
			scale--;
	}
	
	public void mousePressed(int x, int y, int button) {
		if (errStr.length()!=0) {
			errStr = "";
			help = false;
		} else if (inFocus)
			help = !help;
	}
	
	@Override
	public void filesDropped(File[] files) {
		if (files!=null && files.length>0) {
			String uri = files[0].getPath();
			if (uri.toLowerCase().endsWith(".png")) {
				try {
					this.url = files[0].toURI().toURL();
					file = files[0];
					lastModified = file.lastModified();
					reload = true;
					scale = DEFAULT_SCALE_INDEX;
				} catch (MalformedURLException e) {
					e.printStackTrace();
					errStr = "Could not convert path to URL";
				}
			} else {
				errStr = "Filetype must be PNG";
			}
		}
	}
	
	
	class TerrainMesh {
		
		VertexData data;
		byte[] px;
		int w, h;
		
		Matrix4f proj = new Matrix4f();
		Matrix4f view = new Matrix4f();
		Matrix4f projView = new Matrix4f();
		Matrix4f transpositionPool = new Matrix4f();
		ShaderProgram program;
		
		public TerrainMesh(URL heightMap) throws IOException, LWJGLException {
			BufferedImage map = ImageIO.read(heightMap);
			
			BufferedImage buf = new BufferedImage(map.getWidth(), map.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
			buf.getGraphics().drawImage(map, 0, 0, null);
			w = buf.getWidth();
			h = buf.getHeight();
			px = ((DataBufferByte)buf.getRaster().getDataBuffer()).getData();
			List<VertexAttrib> attr = Arrays.asList(
					new VertexAttrib(0, "Position", 3),
					new VertexAttrib(1, "TexCoord", 2));
			data = new VertexArray(w*h*2, attr);
			
			final String VERT = "uniform mat4 u_projView;\n"
					+ "attribute vec2 TexCoord;\n"
					+ "attribute vec3 Position;\n"
					+ "varying vec2 vTexCoord; \n"
					+ "void main() {\n" 
					+ "	vTexCoord = TexCoord;\n"					
					+ "	gl_Position = u_projView * vec4(Position, 1.0);\n" + "}";

			final String FRAG = "uniform sampler2D u_texture;\n"
					+ "varying vec2 vTexCoord;\n" + "void main() {\n"
					+ "	vec4 texColor = texture2D(u_texture, vTexCoord);\n"
					+ "	gl_FragColor = texColor;\n" + "}";
			
			ShaderProgram.setStrictMode(false);
			program = new ShaderProgram(VERT, FRAG, attr);
			if (program.getLog().length()!=0)
				System.out.println("Shader Log: "+program.getLog());
			
			MathUtil.setToProjection(proj, 0.01f, 1000f, 45f, Display.getWidth()/(float)Display.getHeight());
			
			program.use();
			program.setUniformi("u_texture", 0);
		}
		
		void create(int unit, float texSize, float heightScale) {
			data.clear();
			
//			for (int i=0; i<px.length; i++) {
//				float y = i / w;
//				float x = i - h*y;
//				float z = (px[i] & 0xFF) / 255f * heightScale;
//				
//				//place 2 tris
//				vert(x, y, z, texSize);//tl
//				vert(x+unit, y, z, texSize);//tr
//				vert(x, y+unit, z, texSize);//bl
//				vert(x+unit, y, z, texSize);//tr
//				vert(x+unit, y+unit, z, texSize);//br
//				vert(x, y+unit, z, texSize);//bl
//				
//				
//			}
			
//			int  
			for (int y=0; y<h; y++) {
				for (int x=0; x<w; x++) {
					float z = (px[x + (y*w)] & 0xFF) / 255f * heightScale;
					vert(x, y, z, texSize);
					vert(x, y+unit, z, texSize);
					
//					vert(x+unit, y, z, texSize);
				
//					vert(x+unit, y+unit, z, texSize);//tr
//					vert(x+unit, y+unit, z, texSize);//br
//					vert(x, y+unit, z, texSize);//bl
				}
			}
			
			data.flip();
		}
		
		void vert(float x, float y, float z, float texSize) {
			data.put(x).put(y).put(z).put( x/(float)w * texSize ).put( y/(float)h * texSize );
		}
		float rot=0;
		
		public void render() {
			Texture t = tex!=null ? tex : font.getTexturePages()[0].getTexture();
			program.use();
			
			float s = (float)Math.sin(getTime());
			view.setIdentity();
			view.translate(new Vector3f(-10f, 10f, -50f));
			view.rotate((float)Math.toRadians(rot+=0.10f), new Vector3f(0f, 1f, 0f));
//			view.rotate((float)Math.toRadians(90f), new Vector3f(1f, 0f, 0f));
			//view.scale(new Vector3f(s,s,s));
//			view.rotate((float)Math.toRadians(rot+=0.5f), new Vector3f(0f,0f,1f));
//			proj = MathUtil.toOrtho2D(proj, 0, 0, Display.getWidth(), Display.getHeight());
			Matrix4f.mul(Matrix4f.transpose(proj, transpositionPool), view, projView);
			program.setUniformMatrix("u_projView", false, projView);
			
			t.bind();
			data.bind();
			data.draw(GL_TRIANGLE_STRIP, 0, w*h);
			data.unbind();
		}
	}
}
