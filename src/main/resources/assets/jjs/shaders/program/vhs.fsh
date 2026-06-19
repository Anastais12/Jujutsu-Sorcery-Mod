#version 150

uniform sampler2D DiffuseSampler;

uniform vec2 InSize;
uniform float Time;

in vec2 texCoord;

out vec4 fragColor;

float rand(vec2 co) {
    return fract(sin(dot(co.xy, vec2(12.9898, 78.233))) * 43758.5453);
}

void main() {
    vec2 uv = texCoord;

    // Tracking jitter
    float jitter = rand(vec2(uv.y * 100.0, Time * 10.0)) * 0.004;
    uv.x += jitter;

    // Chromatic aberration
    float r = texture(DiffuseSampler, uv + vec2(0.003, 0.0)).r;
    float g = texture(DiffuseSampler, uv).g;
    float b = texture(DiffuseSampler, uv - vec2(0.003, 0.0)).b;

    // VHS noise
    float noise = rand(uv * Time) * 0.08;

    // Scanlines
    float scanline = sin(uv.y * InSize.y * 2.0) * 0.04;

    // Tracking lines
    float tracking = step(0.97, rand(vec2(0.0, floor(uv.y * 100.0) + Time)));

    vec3 color = vec3(r, g, b);
    color += noise - scanline;
    color = mix(color, vec3(1.0), tracking * 0.3);

    // Slight desaturation
    float luma = dot(color, vec3(0.299, 0.587, 0.114));
    color = mix(vec3(luma), color, 0.85);

    fragColor = vec4(color, 1.0);
}
