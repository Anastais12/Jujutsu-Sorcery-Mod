#version 150

uniform sampler2D DiffuseSampler;

uniform vec2 InSize;
uniform float Time;
uniform float Progress;
uniform float Intensity;
uniform vec3 ImpactColor;

in vec2 texCoord;

out vec4 fragColor;

void main() {
    vec4 color = texture(DiffuseSampler, texCoord);

    // Radial distortion
    vec2 center = vec2(0.5, 0.5);
    vec2 delta = texCoord - center;
    float dist = length(delta);
    float angle = atan(delta.y, delta.x);

    // Zoom burst effect
    float zoom = 1.0 + (1.0 - Progress) * Intensity * 0.5;
    vec2 zoomedUV = center + delta / zoom;

    // RGB split based on distance from center
    float split = (1.0 - Progress) * Intensity * 0.02;
    float r = texture(DiffuseSampler, zoomedUV + vec2(cos(angle), sin(angle)) * split).r;
    float g = texture(DiffuseSampler, zoomedUV).g;
    float b = texture(DiffuseSampler, zoomedUV - vec2(cos(angle), sin(angle)) * split).b;

    color = vec4(r, g, b, 1.0);

    // Flash overlay
    float flash = (1.0 - Progress) * Intensity;
    color = mix(color, vec4(ImpactColor, 1.0), flash * 0.5);

    // Speed lines at edges
    float edgeDist = max(abs(delta.x), abs(delta.y)) * 2.0;
    if (edgeDist > 0.7) {
        float lines = sin(angle * 30.0 + Progress * 10.0) * 0.5 + 0.5;
        color = mix(color, vec4(1.0), lines * flash * (edgeDist - 0.7) * 3.0);
    }

    fragColor = color;
}
