uniform float time; // time in seconds
varying vec2 v_texCoords;
uniform sampler2D u_texture; // scene buffer
uniform vec2 center;
void main(void)
{
  vec2 p = (2.0 * v_texCoords - 1.0) + center;
  float len = length(p);
  vec2 uv = v_texCoords + (p/len)*cos(len*12.0-time*4.0)*0.03;
  vec3 col = texture2D(u_texture,uv).xyz;
  gl_FragColor = vec4(col,1.0);  
}