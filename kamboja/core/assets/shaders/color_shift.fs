varying vec2 v_texCoords;
uniform sampler2D u_texture;

void main()
{
	
	float r = texture2D(u_texture, v_texCoords + vec2(0.01, 0.0)).x;
	float g = texture2D(u_texture, v_texCoords + vec2(-0.01, 0.0)).y;
	float b = texture2D(u_texture, v_texCoords + vec2(0.0, 0.0)).z;

    gl_FragColor = vec4(r, g, b, 1.0);
}