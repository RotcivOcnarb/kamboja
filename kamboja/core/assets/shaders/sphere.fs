uniform float time; // time in seconds
varying vec2 v_texCoords;
uniform sampler2D u_texture; // scene buffer
void main(void)
{
  vec2 p = -1.0 + 2.0 * v_texCoords;
  float r = dot(p,p);
  if (r > 1.0) discard; 
  float f = (1.0-sqrt(1.0-r))/(r);
  vec2 uv;
  uv.x = p.x*f + time;
  uv.y = p.y*f + time;
  gl_FragColor = vec4(texture2D(u_texture,uv).xyz, 1.0);
}