uniform sampler2D u_texture;
varying vec2 v_texCoords;
uniform sampler2D noiseTex; 
uniform sampler2D maskTex; 
uniform float elapsedTime; // seconds
uniform float luminanceThreshold; // 0.2
uniform float colorAmplification; // 4.0
uniform float effectCoverage; // 0.5
void main ()
{
  vec4 finalColor;
  // Set effectCoverage to 1.0 for normal use.  
  if (v_texCoords.x < effectCoverage) 
  {
    vec2 uv;           
    uv.x = 0.4*sin(elapsedTime*50.0);                                 
    uv.y = 0.4*cos(elapsedTime*50.0);                                 
    float m = texture2D(maskTex, v_texCoords).r;
    vec3 n = texture2D(noiseTex, 
                 (v_texCoords*3.5) + uv).rgb;
    vec3 c = texture2D(u_texture, v_texCoords 
                               + (n.xy*0.005)).rgb;
  
    float lum = dot(vec3(0.30, 0.59, 0.11), c);
    if (lum < luminanceThreshold)
      c *= colorAmplification; 
  
    vec3 visionColor = vec3(0.1, 0.95, 0.2);
    finalColor.rgb = (c + (n*0.2)) * visionColor * m;
   }
   else
   {
    finalColor = texture2D(u_texture, 
                   v_texCoords);
   }
  gl_FragColor.rgb = finalColor.rgb;
  gl_FragColor.a = 1.0;
}			