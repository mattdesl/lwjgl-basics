package mdesl.test;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.util.jinput.LWJGLEnvironmentPlugin;

public class SimpleTest {
	public static void main(String[] args) throws LWJGLException {
		Display.setDisplayMode(new DisplayMode(800, 600));
		Display.create();
		

		while (!Display.isCloseRequested()) {
			// render here
			
			Display.update();
			Display.sync(60);
		}

		Display.destroy();
	}
}
