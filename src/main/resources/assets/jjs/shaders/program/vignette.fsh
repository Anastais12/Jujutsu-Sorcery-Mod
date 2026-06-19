#version 150

uniform sampler2D DiffuseSampler;

uniform vec2 InSize;
uniform float Intensity;
uniform vec3 Color;

in vec2 texCoord;

out vec4 fragColor;

void main() {
    vec4 color = texture(DiffuseSampler, texCoord);

    // Distance from center
    vec2 center = vec2(0.5, 0.5);
    float dist = distance(texCoord, center);

    // Vignette mask
    float vignette = 1.0 - smoothstep(0.3, 0.9, dist);
    vignette = pow(vignette, Intensity * 2.0);

    // Apply vignette color
    vec3 vignetteColor = mix(Color, color.rgb, vignette);

    fragColor = vec4(vignetteColor, color.a);
}
