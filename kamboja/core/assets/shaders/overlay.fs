varying vec2 v_texCoords;
uniform sampler2D u_texture;
uniform sampler2D beforeBlood;
uniform sampler2D afterBlood;

float lerp(float v0, float v1, float t) {
  return (1.0 - t) * v0 + t * v1;
}

void main()
{

	vec4 before = texture2D(beforeBlood, v_texCoords);
	vec4 after = texture2D(afterBlood, v_texCoords);
	
	float r = after.r < 0.5 ? (2.0 * before.r * after.r) : (1.0 - 2.0*(1.0 - before.r) * (1.0 - after.r));
	float g = after.g < 0.5 ? (2.0 * before.g * after.g) : (1.0 - 2.0*(1.0 - before.g) * (1.0 - after.g));
	float b = after.b < 0.5 ? (2.0 * before.b * after.b) : (1.0 - 2.0*(1.0 - before.b) * (1.0 - after.b));
	
	float alpha = 0.6;

	/*
	gl_FragColor = vec4(
		lerp(before.r, r, alpha),
		lerp(before.g, g, alpha),
		lerp(before.b, b, alpha),
		1.0);
		*/
		
	//gl_FragColor = vec4(vec3(r, g, b) + before.rgb, 1.0);
	
	gl_FragColor = vec4(1.0 - (1.0 - before.rgb) * (1.0 - after.rgb), 1.0);
	

	
   
}