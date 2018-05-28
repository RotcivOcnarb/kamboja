varying vec2 v_texCoords;
uniform sampler2D u_texture;
uniform sampler2D beforeBlood;
uniform sampler2D afterBlood;

float lerp(float v0, float v1, float t) {
  return (1.0 - t) * v0 + t * v1;
}

vec4 lerp(vec4 v0, vec4 v1, float t) {
  return (1.0 - t) * v0 + t * v1;
}

float len2(vec3 vec){
	return vec.x * vec.x + vec.y * vec.y + vec.z * vec.z;
}

void main()
{

	vec4 before = texture2D(beforeBlood, v_texCoords);
	vec4 after = texture2D(afterBlood, v_texCoords);
	
	vec4 output = texture2D(afterBlood, v_texCoords) * texture2D(beforeBlood, v_texCoords);
	
	if( len2(after.rgb) < 2.8){
	
		output = lerp(output, after, 0.2);
	
	}

	gl_FragColor = output;

	
   
}