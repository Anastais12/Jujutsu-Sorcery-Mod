#version 150

uniform sampler2D DiffuseSampler;

uniform vec2 InSize;
uniform float Intensity;
uniform float Threshold;

in vec2 texCoord;

out vec4 fragColor;

void main() {
    vec4 color = texture(DiffuseSampler, texCoord);

    // Extract bright areas
    float brightness = dot(color.rgb, vec3(0.2126, 0.7152, 0.0722));
    vec4 bright = color * smoothstep(Threshold, Threshold + 0.1, brightness);

    // Simple box blur approximation
    vec4 bloom = vec4(0.0);
    float total = 0.0;
    for (float x = -2.0; x <= 2.0; x += 1.0) {
        for (float y = -2.0; y <= 2.0; y += 1.0) {
            vec2 offset = vec2(x, y) / InSize;
            float weight = 1.0 - length(vec2(x, y)) / 3.0;
            weight = max(0.0, weight);
            bloom += texture(DiffuseSampler, texCoord + offset) * weight;
            total += weight;
        }
    }
    bloom /= total;

    // Combine
    fragColor = color + bloom * Intensity;
}
