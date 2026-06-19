#version 150

in vec4 Position;

uniform vec2 InSize;
uniform vec2 OutSize;

out vec2 texCoord;

void main() {
    vec4 outPos = Position * vec4(2.0 / OutSize.x, -2.0 / OutSize.y, 1.0, 1.0)
                  + vec4(-1.0, 1.0, 0.0, 0.0);
    gl_Position = vec4(outPos.xy, 0.2, 1.0);
    texCoord = Position.xy / InSize;
}
