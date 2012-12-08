//texture 0
uniform sampler2D u_texture;

// -- "in" varyings
varying vec4 vColor;
varying vec2 vTexCoord;

void main(void) {
	//sample the texture
	vec4 texColor = texture2D(u_texture, vTexCoord);
	
	//invert the red, green and blue channels
	texColor.rgb = 1.0 - texColor.rgb;
	
	//final color
	gl_FragColor = vColor * texColor;
}