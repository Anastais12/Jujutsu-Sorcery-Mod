#version 150

uniform sampler2D DiffuseSampler;

uniform vec2 InSize;
uniform float Time;
uniform float Intensity;

in vec2 texCoord;

out vec4 fragColor;

float rand(vec2 co) {
    return fract(sin(dot(co.xy, vec2(12.9898, 78.233))) * 43758.5453);
}

void main() {
    vec2 uv = texCoord;

    // Block glitch
    float blockSize = 30.0;
    vec2 blockUV = floor(uv * InSize / blockSize) * blockSize / InSize;
    float glitch = rand(blockUV + Time) * Intensity;

    if (glitch > 0.7) {
        float shift = (rand(blockUV + Time * 0.1) - 0.5) * 0.1 * Intensity;
        uv.x += shift;
    }

    // Scanline glitch
    float scanline = step(0.95, rand(vec2(0.0, uv.y * 100.0 + Time)));
    uv.x += scanline * 0.05 * Intensity;

    // RGB split
    float r = texture(DiffuseSampler, uv + vec2(0.01 * Intensity, 0.0)).r;
    float g = texture(DiffuseSampler, uv).g;
    float b = texture(DiffuseSampler, uv - vec2(0.01 * Intensity, 0.0)).b;

    fragColor = vec4(r, g, b, 1.0);
}
