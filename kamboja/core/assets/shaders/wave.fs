varying vec2 v_texCoords;
uniform sampler2D u_texture;
uniform float time;
uniform float squish; //20
uniform float intensity; //0.03
uniform float percent_x; //

void main()
{
	vec2 uv2 = vec2(v_texCoords.x + (sin(v_texCoords.y*squish + time)*intensity), v_texCoords.y);
	if(uv2.x < percent_x){
		gl_FragColor = texture2D(u_texture, uv2);
	}
	else{
		gl_FragColor = vec4(0, 0, 0, 0);
	}
    
}