uniform sampler2D u_texture;
varying vec4 vColor;
varying vec2 vTexCoord;
void main(void) {
	vec4 texColor = texture2D(u_texture, vTexCoord);
	gl_FragColor = vColor * texColor;
}