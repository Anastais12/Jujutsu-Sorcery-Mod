#version 150

uniform sampler2D DiffuseSampler;

uniform vec2 InSize;
uniform float Intensity;
uniform float LineCount;

in vec2 texCoord;

out vec4 fragColor;

void main() {
    vec4 color = texture(DiffuseSampler, texCoord);

    // Scanline pattern
    float scanline = sin(texCoord.y * LineCount * 3.14159) * 0.5 + 0.5;
    scanline = mix(1.0, scanline, Intensity);

    // Subtle horizontal line
    float hLine = sin(texCoord.x * InSize.x * 0.7) * 0.5 + 0.5;
    hLine = mix(1.0, hLine, Intensity * 0.3);

    color.rgb *= scanline * hLine;

    fragColor = color;
}
