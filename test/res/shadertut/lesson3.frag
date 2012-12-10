//texture 0
uniform sampler2D u_texture;

//our screen resolution, set from Java whenever the display is resized
uniform vec2 resolution;

//"in" attributes from our vertex shader
varying vec4 vColor;
varying vec2 vTexCoord;

//size of our vignette, where 1.0 results in a circle fitting the screen
const float SIZE = 0.75;

//softness of our vignette, between 0.0 and 1.0
const float SOFTNESS = 0.45;

//sepia colour, adjust to taste
const vec3 SEPIA = vec3(1.2, 1.0, 0.8); 

void main() {
	//sample our texture
	vec4 texColor = texture2D(u_texture, vTexCoord);
	
	//determine center position
	vec2 position = (gl_FragCoord.xy / resolution.xy) - vec2(0.5);
	
	//correct for aspect ratio
	position.x *= resolution.x / resolution.y;
	
	//determine the vector length (magnitude) of the center position
	float len = length(position);
	
	//use smoothstep to create a smooth vignette
	float vignette = smoothstep(SIZE, SIZE-SOFTNESS, len);
	
	//apply the vignette with 50% opacity
	texColor.rgb = mix(texColor.rgb, texColor.rgb * vignette, 0.5);
	
	//convert to grayscale using NTSC conversion weights
	float gray = dot(texColor.rgb, vec3(0.299, 0.587, 0.114));
	
	//create our sepia tone from some constant value
	vec3 sepiaColor = vec3(gray) * SEPIA;
		
	//again we'll use mix so that the sepia effect is at 75%
	texColor.rgb = mix(texColor.rgb, sepiaColor, 0.75);
	
	//final colour, multiplied by vertex colour
	gl_FragColor = texture2D(u_texture, vTexCoord) * vColor;
}
