//texture 0
uniform sampler2D u_texture;

//our screen resolution
uniform vec2 resolution;

// -- "in" varyings
varying vec4 vColor;
varying vec2 vTexCoord;

void main() {
	vec4 texColor = texture2D(u_texture, vTexCoord);
	
	vec2 position = (gl_FragCoord.xy / resolution.xy) - vec2(0.5);
	
	//correct for aspect ratio
	position.x *= resolution.x /resolution.y;
	
	float len = length(position);
	
	//the most basic solution:
	//texColor.rgb *= vec3(1.0 - length(position));
	//gl_FragColor = texColor * vColor;
	
	vec2 pos = (gl_FragCoord.xy / resolution.xy);
	
	float radius = 0.75;
	float softness = .45;
	
	float vignette = smoothstep(radius, radius-softness, len);
	
	//make vignette 50% opacity
	texColor.rgb = mix(texColor.rgb, texColor.rgb * vignette, 0.5);
	
	//turn grayscale using NTSC conversion weights
	float gray = dot(texColor.rgb, vec3(0.299, 0.587, 0.114));
	
	//make sepia tone
	vec3 sepia = vec3(gray) * vec3(1.2, 1.0, 0.8);
		
	//again we'll use mix so that the sepia effect is at 75%
	texColor.rgb = mix(texColor.rgb, sepia, 0.75);
		
	gl_FragColor = texColor * vColor;
}
