#version 150

uniform sampler2D DiffuseSampler;

uniform vec2 InSize;
uniform float Intensity;

in vec2 texCoord;

out vec4 fragColor;

void main() {
    vec2 center = vec2(0.5, 0.5);
    vec2 delta = texCoord - center;
    float dist = length(delta);
    float aberration = Intensity * dist;

    float r = texture(DiffuseSampler, texCoord + delta * aberration).r;
    float g = texture(DiffuseSampler, texCoord).g;
    float b = texture(DiffuseSampler, texCoord - delta * aberration).b;

    fragColor = vec4(r, g, b, 1.0);
}
