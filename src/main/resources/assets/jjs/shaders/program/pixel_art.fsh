#version 150

uniform sampler2D DiffuseSampler;

uniform vec2 InSize;
uniform float PixelSize;

in vec2 texCoord;

out vec4 fragColor;

void main() {
    // Snap to pixel grid
    vec2 pixelatedUV = floor(texCoord * InSize / PixelSize) * PixelSize / InSize;

    // Sample at center of pixel for sharp edges
    vec2 centerUV = pixelatedUV + (PixelSize * 0.5 / InSize);

    fragColor = texture(DiffuseSampler, centerUV);
}
