#version 150

uniform sampler2D DiffuseSampler; // The game's screen texture
uniform float Time;               // Passed from your post-chain config

in vec2 texCoord;
out vec4 fragColor;

void main() {
    vec2 uv = texCoord;

    // Mathematical distortion (adjust 0.005 for strength, 10.0 for speed/frequency)
    float distortionX = sin(uv.y * 20.0 + Time * 10.0) * 0.005;
    float distortionY = cos(uv.x * 20.0 + Time * 10.0) * 0.005;

    // Apply the offset coordinates to the screen texture
    vec2 distortedUV = uv + vec2(distortionX, distortionY);

    // Output the final pixel color
    fragColor = texture(DiffuseSampler, distortedUV);
}
