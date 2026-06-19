#version 150

uniform sampler2D DiffuseSampler;

uniform vec2 InSize;
uniform float Time;
uniform float Progress;

in vec2 texCoord;

out vec4 fragColor;

void main() {
    vec2 uv = texCoord;

    // Zoom in
    float zoom = 1.0 + Progress * 0.3;
    uv = (uv - 0.5) / zoom + 0.5;

    // Chromatic aberration increases with progress
    float aberration = Progress * 0.01;
    float r = texture(DiffuseSampler, uv + vec2(aberration, 0.0)).r;
    float g = texture(DiffuseSampler, uv).g;
    float b = texture(DiffuseSampler, uv - vec2(aberration, 0.0)).b;

    vec3 color = vec3(r, g, b);

    // Red edge glow
    vec2 center = vec2(0.5, 0.5);
    float dist = distance(uv, center);
    float edgeGlow = smoothstep(0.3, 0.8, dist) * Progress;
    color = mix(color, vec3(1.0, 0.0, 0.0), edgeGlow * 0.5);

    // Darken center
    float centerDark = 1.0 - smoothstep(0.0, 0.4, dist) * Progress * 0.5;
    color *= centerDark;

    // Flash at peak
    float flash = smoothstep(0.3, 0.5, Progress) * smoothstep(0.7, 0.5, Progress);
    color = mix(color, vec3(1.0), flash * 0.3);

    fragColor = vec4(color, 1.0);
}
