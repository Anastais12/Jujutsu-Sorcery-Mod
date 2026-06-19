#version 150

uniform sampler2D DiffuseSampler;

uniform vec2 InSize;

in vec2 texCoord;

out vec4 fragColor;

void main() {
    vec4 color = texture(DiffuseSampler, texCoord);

    // Convert to grayscale
    float luma = dot(color.rgb, vec3(0.299, 0.587, 0.114));

    // Heat vision color ramp: black -> red -> yellow -> white
    vec3 heatColor;
    if (luma < 0.25) {
        heatColor = mix(vec3(0.0, 0.0, 0.0), vec3(0.5, 0.0, 0.0), luma * 4.0);
    } else if (luma < 0.5) {
        heatColor = mix(vec3(0.5, 0.0, 0.0), vec3(1.0, 0.0, 0.0), (luma - 0.25) * 4.0);
    } else if (luma < 0.75) {
        heatColor = mix(vec3(1.0, 0.0, 0.0), vec3(1.0, 1.0, 0.0), (luma - 0.5) * 4.0);
    } else {
        heatColor = mix(vec3(1.0, 1.0, 0.0), vec3(1.0, 1.0, 1.0), (luma - 0.75) * 4.0);
    }

    // Add noise for thermal sensor look
    float noise = fract(sin(dot(texCoord * InSize, vec2(12.9898, 78.233))) * 43758.5453);
    heatColor += (noise - 0.5) * 0.05;

    fragColor = vec4(heatColor, color.a);
}
