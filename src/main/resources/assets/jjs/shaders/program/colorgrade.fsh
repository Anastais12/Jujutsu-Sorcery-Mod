#version 150

uniform sampler2D DiffuseSampler;

uniform float Contrast;
uniform float Saturation;
uniform float Brightness;

in vec2 texCoord;

out vec4 fragColor;

void main() {
    vec4 color = texture(DiffuseSampler, texCoord);

    // Brightness
    color.rgb += Brightness;

    // Contrast
    color.rgb = (color.rgb - 0.5) * Contrast + 0.5;

    // Saturation
    float luminance = dot(color.rgb, vec3(0.2126, 0.7152, 0.0722));
    color.rgb = mix(vec3(luminance), color.rgb, Saturation);

    fragColor = color;
}
