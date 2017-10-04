uniform sampler2D u_texture; // 0
varying vec2 v_texCoords;
uniform float angle;

vec2 direction(float angle, float magnitude){
	return vec2(magnitude * cos(angle), magnitude * sin(angle));
}

void main ()
{
  vec2 uv = v_texCoords;
  vec4 c = texture2D(u_texture, uv);
  
  c += texture2D(u_texture, uv+direction(angle, 0.001));
  c += texture2D(u_texture, uv+direction(angle, 0.003));
  c += texture2D(u_texture, uv+direction(angle, 0.005));
  c += texture2D(u_texture, uv+direction(angle, 0.007));
  c += texture2D(u_texture, uv+direction(angle, 0.009));
  c += texture2D(u_texture, uv+direction(angle, 0.011));

  c += texture2D(u_texture, uv-direction(angle, 0.001));
  c += texture2D(u_texture, uv-direction(angle, 0.003));
  c += texture2D(u_texture, uv-direction(angle, 0.005));
  c += texture2D(u_texture, uv-direction(angle, 0.007));
  c += texture2D(u_texture, uv-direction(angle, 0.009));
  c += texture2D(u_texture, uv-direction(angle, 0.011));

  c.rgb = vec3((c.r+c.g+c.b)/3.0);
  c = c / 9.5;
  gl_FragColor = c;
}	

