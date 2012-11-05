package mdesl.graphics.glutils;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.GL_COMPILE_STATUS;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_INFO_LOG_LENGTH;
import static org.lwjgl.opengl.GL20.GL_LINK_STATUS;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20.glAttachShader;
import static org.lwjgl.opengl.GL20.glBindAttribLocation;
import static org.lwjgl.opengl.GL20.glCompileShader;
import static org.lwjgl.opengl.GL20.glCreateProgram;
import static org.lwjgl.opengl.GL20.glCreateShader;
import static org.lwjgl.opengl.GL20.glDeleteProgram;
import static org.lwjgl.opengl.GL20.glDeleteShader;
import static org.lwjgl.opengl.GL20.glDetachShader;
import static org.lwjgl.opengl.GL20.glGetProgram;
import static org.lwjgl.opengl.GL20.glGetProgramInfoLog;
import static org.lwjgl.opengl.GL20.glGetShader;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glLinkProgram;
import static org.lwjgl.opengl.GL20.glShaderSource;
import static org.lwjgl.opengl.GL20.glUseProgram;

import java.util.List;

/**
 * A bare-bones ShaderProgram utility based on ra4king's ArcSynthesis Java ports. 
 * 
 * @author ra4king
 */
public class ShaderProgram {

	public final int program;
	public final int vertex;
	public final int fragment;
	protected String log;
	
	public ShaderProgram(String vertexSource, String fragmentSource) {
		this(vertexSource, fragmentSource, null);
	}

	public ShaderProgram(String vertexShader, String fragmentShader, List<VertexAttrib> attributes) {
		vertex = compileShader(vertexShader, GL_VERTEX_SHADER);
		fragment = compileShader(fragmentShader, GL_FRAGMENT_SHADER);
		program = glCreateProgram();
		glAttachShader(program, vertex);
		glAttachShader(program, fragment);
		
		if (attributes != null)
			for (VertexAttrib a : attributes)
				glBindAttribLocation(program, a.location, a.name);
		
		glLinkProgram(program);
		
		String infoLog = glGetProgramInfoLog(program, glGetProgram(program, GL_INFO_LOG_LENGTH));
		
		if (glGetProgram(program, GL_LINK_STATUS) == GL_FALSE)
			throw new RuntimeException(
					"Failure in linking program. Error log:\n" + infoLog);
		
		if (infoLog!=null && infoLog.trim().length()!=0)
			log += infoLog;
				
		glDetachShader(program, vertex);
		glDetachShader(program, fragment);
		glDeleteShader(vertex);
		glDeleteShader(fragment);
	}
		
	private int compileShader(String source, int type) {
		int shader = glCreateShader(type);
		glShaderSource(shader, source);
		glCompileShader(shader);
		
		String infoLog = glGetShaderInfoLog(shader,
				glGetShader(shader, GL_INFO_LOG_LENGTH));

		if (glGetShader(shader, GL_COMPILE_STATUS) == GL_FALSE)
			throw new RuntimeException("Failure in compiling " + getName(type)
					+ ". Error log:\n" + infoLog);
		
		if (infoLog!=null && infoLog.trim().length()!=0)
			log += getName(type) +": "+infoLog + "\n";
		
		return shader;
	}

	private String getName(int shaderType) {
		if (shaderType == GL_VERTEX_SHADER)
			return "GL_VERTEX_SHADER";
		if (shaderType == GL_FRAGMENT_SHADER)
			return "GL_FRAGMENT_SHADER";
		else 
			return "shader";
	}

	public void begin() {
		glUseProgram(program);
	}

	public void end() {
		glUseProgram(0);
	}
	
	public void destroy() {		
		glDeleteProgram(program);
	}
	
	public int getUniformLocation(String str) {
		return glGetUniformLocation(program, str);
	}
}