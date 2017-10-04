			uniform float time;
            uniform sampler2D u_texture;
			varying vec2 v_texCoords;
			
            uniform float flicker;
            uniform float oversaturation;
            uniform float lightvariance;
            uniform float blackandwhite;
            uniform float scratches;
            uniform vec2 scratchsize;
            uniform float vignette;
            uniform float splotches;

            void main()
            {
                float noise = lightvariance * sin(30.3 * time) * sin(0.3 * time) + flicker * sin(82831.3 * time);

                vec2 fc = v_texCoords;

                vec2 tc = v_texCoords;
                vec4 col = texture2D(u_texture, tc);
                float bw = dot(col.xyz, col.xyz);
                col = mix(col, vec4(bw, bw, bw, col.a), blackandwhite);
                col = col * (1.0 + oversaturation + noise);

                vec2 vc = tc - vec2(0.5);
                float vign = vignette * dot(vc, vc);
                col = mix(col, vec4(0.3), vign);

                vec2 scratchOffset = (fc - time * vec2(11.3, 21.14)) / scratchsize;
                vec2 scratchIndex = ceil(scratchOffset);
                vec2 scratchError = fract(scratchOffset) - vec2(0.5);
                vec2 scratch = scratchsize * scratchIndex;
                float sn = sin(dot(scratch, vec2(time, 0.01)));
                float se = 1.0 - 5.0 * scratchError.x*scratchError.x;
                sn = se * smoothstep(1.0 - 1e-5 * scratches, 1.000001, sn);
                col = mix(col, vec4(1), sn);

                const float splotchSpeed = 26.112831;
                float splotchTime = floor(time * splotchSpeed);
                vec2 splotch = 2000.0 * sin(vec2(0.5, 0.8) * splotchTime);
                float splotchDist = length(fc - splotch);
                float splotchSize = splotches * abs(sin(splotchTime));
                float splotchInt = 1.0 - smoothstep(0.6*splotchSize, splotchSize, splotchDist);
                col = mix(col, vec4(0.9, 0.5 + 0.4*splotchInt, splotchInt, 1), 0.8*splotchInt);


                gl_FragColor = col;
}