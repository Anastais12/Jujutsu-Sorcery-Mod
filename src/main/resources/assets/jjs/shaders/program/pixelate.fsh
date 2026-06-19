#version 150

uniform sampler2D DiffuseSampler;

uniform vec2 InSize;
uniform float PixelSize;

in vec2 texCoord;

out vec4 fragColor;

void main() {
    vec2 pixelatedUV = floor(texCoord * InSize / PixelSize) * PixelSize / InSize;
    fragColor = texture(DiffuseSampler, pixelatedUV + (PixelSize * 0.5 / InSize));
}
