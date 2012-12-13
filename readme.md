_lwjgl-basics_ is a minimal shader-based library for 2D LWJGL sprite games. It provides essential utilities for handling textures, shaders, and sprite rendering.

For a large game project, a platform like [LibGDX](http://libgdx.badlogicgames.com/) may be more suitable.

The [source code](https://github.com/mattdesl/lwjgl-basics) is hosted on GitHub.

### OpenGL & Shader Tutorials

The Wiki also hosts various OpenGL and GLSL tutorials:  
https://github.com/mattdesl/lwjgl-basics/wiki

### Installing the API

The best way to install the API is to use Eclipse and EGit (or another IDE with Git support) to pull the most recent source code. Included in the `lib` and `native` folder is a distribution of LWJGL 2.8.5, as well as an Eclipse project with class path set up for you. You can download newer versions of LWJGL from their [downloads page](http://lwjgl.org/download.php). 

Alternatively, you can download the full library as a ZIP:

![ZIP](http://i.imgur.com/Dkvp0.png)

Then, simply open the Eclipse project to start testing. Ensure your LWJGL JARs and natives have been set correctly in [Eclipse](http://www.lwjgl.org/wiki/index.php?title=Setting_Up_LWJGL_with_Eclipse), [NetBeans](http://www.lwjgl.org/wiki/index.php?title=Setting_Up_LWJGL_with_NetBeans) or [IntelliJ](http://www.lwjgl.org/wiki/index.php?title=Setting_Up_LWJGL_with_IntelliJ_IDEA), and include lwjgl-basics as a class library. lwjgl-basics also uses PNGDecoder.jar as a dependency, which can be downloaded [here](http://twl.l33tlabs.org/textureloader/).

See the [tests](https://github.com/mattdesl/lwjgl-basics/tree/master/test/mdesl/test) package to get started with some basic examples.


### Credits

- PNG decoder by Matthias Mann
- [PT Font](http://www.fontsquirrel.com/fonts/PT-Sans)
- [Grass & Water Tileset](http://opengameart.org/content/grass-and-water-tiles)
- [Tiling textures](http://opengameart.org/content/tilling-textures-pack-33)
- [2D Game Scene](http://opengameart.org/content/grassland-tileset)
- Code written by Matt DesLauriers (aka: davedes or mattdesl) unless otherwise stated.