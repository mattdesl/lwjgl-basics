uniform mat4 u_projView;
attribute vec4 Color;
attribute vec2 TexCoord;
attribute vec2 Position;
varying vec4 vColor;
varying vec2 vTexCoord; 
void main() {
	vColor = Color;
	vTexCoord = TexCoord;
	gl_Position = u_projView * vec4(Position.xy, 0, 1);
}