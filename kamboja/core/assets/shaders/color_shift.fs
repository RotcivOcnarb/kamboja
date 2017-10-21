varying vec2 v_texCoords;
uniform sampler2D u_texture;
uniform float intensity;

void main()
{

	float dist = length(v_texCoords - vec2(0.5, 0.5)) + 1.0;
	
	float r = texture2D(u_texture, v_texCoords + vec2(0.01 * intensity * dist, 0.0)).x;
	float g = texture2D(u_texture, v_texCoords + vec2(-0.01 * intensity * dist, 0.0)).y;
	float b = texture2D(u_texture, v_texCoords + vec2(0.0 * intensity * dist, 0.0)).z;

    gl_FragColor = vec4(r, g, b, 1.0);
}