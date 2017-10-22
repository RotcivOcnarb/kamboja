varying vec2 v_texCoords;
uniform sampler2D u_texture;
uniform float angle;
uniform float intensity;
uniform float transparency;

void main()	{

	int kernel_size = 15;

	vec4 color = vec4(0.0, 0.0, 0.0, 0.0);

	vec2 anglevec = vec2(cos(angle), sin(angle));
	
	for(int i = -kernel_size; i <= kernel_size; i ++){
	
	
	
		vec4 col = texture2D(u_texture, v_texCoords + (anglevec * intensity * i));
		
		color += col;
	
	}
	
	color /= float(kernel_size*2 + 1);

	color.a *= transparency;
	
	gl_FragColor = color;
}