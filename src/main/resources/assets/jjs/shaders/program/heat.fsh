#version 150

uniform sampler2D DiffuseSampler;

uniform vec2 InSize;
uniform float Time;
uniform float Intensity;

in vec2 texCoord;

out vec4 fragColor;

void main() {
    // Heat wave distortion
    float distortion = sin(texCoord.y * 20.0 + Time * 3.0) * Intensity;
    distortion += sin(texCoord.x * 15.0 + Time * 2.0) * Intensity * 0.5;

    vec2 distortedUV = texCoord + vec2(distortion, distortion * 0.3);

    fragColor = texture(DiffuseSampler, distortedUV);
}
