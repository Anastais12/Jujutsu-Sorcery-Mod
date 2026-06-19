#version 150

uniform sampler2D DiffuseSampler;

uniform vec2 InSize;
uniform vec2 BlurDir;
uniform float Radius;

in vec2 texCoord;

out vec4 fragColor;

void main() {
    vec4 blurred = vec4(0.0);
    float totalWeight = 0.0;

    for (float r = -Radius; r <= Radius; r += 1.0) {
        vec2 offset = (BlurDir * r) / InSize;
        float weight = 1.0 - abs(r / Radius);
        weight = weight * weight; // Smoothstep-like falloff
        blurred += texture(DiffuseSampler, texCoord + offset) * weight;
        totalWeight += weight;
    }

    fragColor = blurred / totalWeight;
}
