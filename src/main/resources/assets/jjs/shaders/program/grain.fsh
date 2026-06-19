#version 150

uniform sampler2D DiffuseSampler;

uniform vec2 InSize;
uniform float Time;
uniform float Intensity;

in vec2 texCoord;

out vec4 fragColor;

// Simple pseudo-random function
float rand(vec2 co) {
    return fract(sin(dot(co.xy, vec2(12.9898, 78.233))) * 43758.5453);
}

void main() {
    vec4 color = texture(DiffuseSampler, texCoord);

    // Generate grain
    float grain = rand(texCoord * InSize + Time) * 2.0 - 1.0;

    // Apply grain
    color.rgb += grain * Intensity;

    fragColor = color;
}
