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

vec3 lerp(vec3 v0, vec3 v1, float t) {
  return (1.0 - t) * v0 + t * v1;
}

float len2(vec3 vec){
	return vec.x * vec.x + vec.y * vec.y + vec.z * vec.z;
}

void main()
{



	vec4 before = texture2D(beforeBlood, v_texCoords);
	vec4 after = texture2D(afterBlood, v_texCoords)*2.0;
	
	vec3 mult = before.rgb * after.rgb;
	
	vec3 saida1 = lerp(before.rgb, mult, after.a);
	
	vec4 saida = vec4(saida1, 1.0);
	
	/*
	vec4 saida = texture2D(afterBlood, v_texCoords) * texture2D(beforeBlood, v_texCoords);
	
	if( len2(after.rgb) < 2.8){
	
		saida = lerp(saida, after, 0.2);
	
	}

	*/

	gl_FragColor = saida;
	
	
   
}