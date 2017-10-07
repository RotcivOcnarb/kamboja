varying vec2 v_texCoords;
uniform sampler2D u_texture;
uniform sampler2D displacement_map;
uniform float intensity;
uniform float time;
uniform float x_t;
uniform float y_t;


void main()
{
	vec2 dis_pos = v_texCoords + vec2(time * x_t, time * y_t);
	
	float gray = length(texture2D(displacement_map, dis_pos));
	
	float angle = gray * 2.0 * 3.141529;
	
	vec2 displacement = vec2(cos(angle) * intensity, sin(angle) * intensity);

    gl_FragColor = texture2D(u_texture, v_texCoords + displacement);
	//gl_FragColor = texture2D(displacement_map, v_texCoords);
}