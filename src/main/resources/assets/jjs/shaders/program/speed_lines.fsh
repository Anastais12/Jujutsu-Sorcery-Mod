#version 150

uniform sampler2D DiffuseSampler;

uniform vec2 InSize;
uniform float Time;
uniform float Intensity;

in vec2 texCoord;

out vec4 fragColor;

void main() {
    vec2 center = vec2(0.5, 0.5);
    vec2 delta = texCoord - center;
    float dist = length(delta);
    float angle = atan(delta.y, delta.x);

    // Speed lines radiating from center
    float lines = sin(angle * 40.0 + Time * 5.0) * 0.5 + 0.5;
    lines = pow(lines, 8.0) * Intensity;

    // Fade at center and edges
    float fade = smoothstep(0.0, 0.2, dist) * (1.0 - smoothstep(0.6, 1.0, dist));
    lines *= fade;

    vec4 color = texture(DiffuseSampler, texCoord);
    color.rgb = mix(color.rgb, vec3(1.0), lines);

    // Motion blur radial
    vec2 blurDir = normalize(delta);
    vec4 blurred = vec4(0.0);
    for (float i = 0.0; i < 5.0; i++) {
        vec2 offset = blurDir * i * 0.01 * Intensity;
        blurred += texture(DiffuseSampler, texCoord + offset);
    }
    blurred /= 5.0;

    color = mix(color, blurred, Intensity * 0.3);

    fragColor = color;
}
