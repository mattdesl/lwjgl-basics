//combined projection and view matrix
uniform mat4 u_projView;

// -- "in" attributes

attribute vec2 Position;
attribute vec2 TexCoord;
attribute vec4 Color;

// -- "out" varyings

varying vec4 vColor;
varying vec2 vTexCoord;
 
void main() {
	vColor = Color;
	vTexCoord = TexCoord;
	gl_Position = u_projView * vec4(Position.xy, 0.0, 1.0);
}