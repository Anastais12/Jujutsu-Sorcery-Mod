#version 150

uniform sampler2D DiffuseSampler;

uniform vec2 InSize;
uniform float Time;

in vec2 texCoord;

out vec4 fragColor;

void main() {
    vec2 uv = texCoord;

    // Wavy distortion
    float wave1 = sin(uv.y * 8.0 + Time * 2.0) * 0.01;
    float wave2 = sin(uv.x * 6.0 + Time * 1.5) * 0.005;
    uv.x += wave1 + wave2;
    uv.y += wave2 * 0.5;

    // Vignette with red tint
    vec2 center = vec2(0.5, 0.5);
    float dist = distance(uv, center);
    float vignette = 1.0 - smoothstep(0.2, 0.8, dist);

    vec4 color = texture(DiffuseSampler, uv);

    // Darken edges with red
    vec3 horrorColor = mix(vec3(0.05, 0.0, 0.0), color.rgb, vignette);

    // Slight desaturation
    float luma = dot(horrorColor, vec3(0.299, 0.587, 0.114));
    horrorColor = mix(vec3(luma), horrorColor, 0.6);

    // Occasional flicker
    float flicker = step(0.98, sin(Time * 20.0)) * 0.1;
    horrorColor -= flicker;

    fragColor = vec4(horrorColor, color.a);
}
