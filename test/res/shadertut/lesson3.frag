//texture 0
uniform sampler2D u_texture;

uniform vec2 resolution;

// -- "in" varyings
varying vec4 vColor;

void main() {
	vec4 color = vec4(1.0);
	
	//final color
	gl_FragColor = vColor * color;
}
