package mdesl.test;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.GL_TEXTURE1;
import static org.lwjgl.opengl.GL13.GL_TEXTURE2;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL20.glUniform1i;
import static org.lwjgl.opengl.GL11.*;

import java.io.IOException;

import mdesl.graphics.SpriteBatch;
import mdesl.graphics.Texture;
import mdesl.graphics.glutils.ShaderProgram;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;

public class TextureBlendTest extends SimpleGame {

	public static final String TEX_ALT = "u_texture1";
	public static final String TEX_MASK = "u_mask";
	
	public static final String VERT_SHADER =
			"uniform mat4 "+SpriteBatch.U_PROJ_VIEW+";\n" +
			"attribute vec4 "+SpriteBatch.ATTR_COLOR+";\n" +
			"attribute vec2 "+SpriteBatch.ATTR_TEXCOORD+";\n" +
			"attribute vec2 "+SpriteBatch.ATTR_POSITION+";\n" +
			"varying vec4 vColor;\n" +
			"varying vec2 vTexCoord; \n" +
			"void main() {\n" +
			"	vColor = "+SpriteBatch.ATTR_COLOR+";\n" +
			"	vTexCoord = "+SpriteBatch.ATTR_TEXCOORD+";\n" +
			"	gl_Position = "+SpriteBatch.U_PROJ_VIEW+" * vec4("+SpriteBatch.ATTR_POSITION+".xy, 0, 1);\n" +
			"}";

	public static final String FRAG_SHADER =
			"uniform sampler2D "+SpriteBatch.U_TEXTURE+";\n" +
			"uniform sampler2D "+TEX_ALT+";\n" +
			"uniform sampler2D "+TEX_MASK+";\n" +
			"varying vec4 vColor;\n" +
			"varying vec2 vTexCoord;\n" +			
			"void main(void) {\n" +
			"	vec4 texColor0 = texture2D("+SpriteBatch.U_TEXTURE+", vTexCoord);\n" +
			"	vec4 texColor1 = texture2D("+TEX_ALT+", vTexCoord);\n" +
			"	float mask = texture2D("+TEX_MASK+", vTexCoord).a;\n" +
			"	gl_FragColor = vColor * mix(texColor0, texColor1, mask);\n" +
			"}";
	
	
	SpriteBatch batch;
	Texture tex0, tex1, mask;
	
	public void create() throws LWJGLException {
		super.create();
		
		ShaderProgram shader = new ShaderProgram(VERT_SHADER, FRAG_SHADER, SpriteBatch.ATTRIBUTES);
		//setup our custom uniforms
		shader.use();
		
		shader.setUniformi(TEX_ALT, 1);
		shader.setUniformi(TEX_MASK, 2);
				
		System.out.println(VERT_SHADER);
		System.out.println();
		System.out.println(FRAG_SHADER);
		
		batch = new SpriteBatch(shader, 1000);
		
		
		try {
			tex0 = new Texture(Util.getResource("res/dirt.png"));
			tex1 = new Texture(Util.getResource("res/grass.png"));
			mask = new Texture(Util.getResource("res/mask.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Sys.alert("Error", "Could not load images");
			System.exit(0);
		}
		
		System.out.println(VERT_SHADER);
		System.out.println(FRAG_SHADER);
		
		//in this example our texture1 won't change, so we can bind it once and forget about it
		glActiveTexture(GL_TEXTURE2);
		mask.bind();
		
		glActiveTexture(GL_TEXTURE1);
		tex1.bind();
		
		glActiveTexture(GL_TEXTURE0);
		tex0.bind();
		
		glClearColor(0.5f, 0.5f, 0.5f, 1.0f);
	}
	
	public void render() throws LWJGLException {
		super.render();
		
		batch.begin();
		
		batch.draw(tex0, 50, 50);
		
		batch.end();
	}
	
	public static void main(String[] args) throws LWJGLException {
		Game game = new TextureBlendTest();
		game.setDisplayMode(800, 600, false);
		game.start();
	}
}
