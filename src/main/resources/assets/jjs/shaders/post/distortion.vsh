#version 150

in vec4 Position;

uniform mat4 ProjMat;
uniform vec2 OutSize;

out vec2 texCoord;

void main() {
    vec4 outPos = ProjMat * Position;
    gl_Position = outPos;

    // Map screen position to standard 0.0 - 1.0 texture coordinates
    texCoord = Position.xy / OutSize;
    texCoord.y = 1.0 - texCoord.y; // Flip Y axis for screen space
}
