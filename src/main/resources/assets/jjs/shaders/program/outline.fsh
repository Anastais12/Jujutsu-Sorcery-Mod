#version 150

uniform sampler2D DiffuseSampler;

uniform vec2 InSize;
uniform float Threshold;

in vec2 texCoord;

out vec4 fragColor;

void main() {
    float x = 1.0 / InSize.x;
    float y = 1.0 / InSize.y;

    vec4 horizEdge = vec4(0.0);
    horizEdge -= texture(DiffuseSampler, vec2(texCoord.x - x, texCoord.y - y)) * 1.0;
    horizEdge -= texture(DiffuseSampler, vec2(texCoord.x - x, texCoord.y    )) * 2.0;
    horizEdge -= texture(DiffuseSampler, vec2(texCoord.x - x, texCoord.y + y)) * 1.0;
    horizEdge += texture(DiffuseSampler, vec2(texCoord.x + x, texCoord.y - y)) * 1.0;
    horizEdge += texture(DiffuseSampler, vec2(texCoord.x + x, texCoord.y    )) * 2.0;
    horizEdge += texture(DiffuseSampler, vec2(texCoord.x + x, texCoord.y + y)) * 1.0;

    vec4 vertEdge = vec4(0.0);
    vertEdge -= texture(DiffuseSampler, vec2(texCoord.x - x, texCoord.y - y)) * 1.0;
    vertEdge -= texture(DiffuseSampler, vec2(texCoord.x    , texCoord.y - y)) * 2.0;
    vertEdge -= texture(DiffuseSampler, vec2(texCoord.x + x, texCoord.y - y)) * 1.0;
    vertEdge += texture(DiffuseSampler, vec2(texCoord.x - x, texCoord.y + y)) * 1.0;
    vertEdge += texture(DiffuseSampler, vec2(texCoord.x    , texCoord.y + y)) * 2.0;
    vertEdge += texture(DiffuseSampler, vec2(texCoord.x + x, texCoord.y + y)) * 1.0;

    vec3 edge = sqrt((horizEdge.rgb * horizEdge.rgb) + (vertEdge.rgb * vertEdge.rgb));
    float edgeStrength = length(edge);

    vec4 original = texture(DiffuseSampler, texCoord);

    if (edgeStrength > Threshold) {
        fragColor = vec4(0.0, 0.0, 0.0, 1.0); // Black outline
    } else {
        fragColor = original;
    }
}
