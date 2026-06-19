#version 150

uniform sampler2D DiffuseSampler;

uniform vec2 InSize;
uniform float Time;

in vec2 texCoord;

out vec4 fragColor;

const float SCANLINE_INTENSITY = 0.15;
const float CURVATURE = 4.0;
const float VIGNETTE_INTENSITY = 0.4;

vec2 curveRemap(vec2 uv) {
    uv = uv * 2.0 - 1.0;
    vec2 offset = abs(uv.yx) / vec2(CURVATURE, CURVATURE);
    uv = uv + uv * offset * offset;
    uv = uv * 0.5 + 0.5;
    return uv;
}

void main() {
    vec2 uv = curveRemap(texCoord);

    if (uv.x < 0.0 || uv.x > 1.0 || uv.y < 0.0 || uv.y > 1.0) {
        fragColor = vec4(0.0, 0.0, 0.0, 1.0);
        return;
    }

    // Chromatic aberration
    float aberration = 0.003;
    float r = texture(DiffuseSampler, uv + vec2(aberration, 0.0)).r;
    float g = texture(DiffuseSampler, uv).g;
    float b = texture(DiffuseSampler, uv - vec2(aberration, 0.0)).b;

    vec3 color = vec3(r, g, b);

    // Scanlines
    float scanline = sin(uv.y * InSize.y * 3.14159) * 0.5 + 0.5;
    color *= 1.0 - (scanline * SCANLINE_INTENSITY);

    // Vignette
    vec2 vignetteUV = uv * (1.0 - uv.yx);
    float vignette = vignetteUV.x * vignetteUV.y * 15.0;
    vignette = pow(vignette, VIGNETTE_INTENSITY);
    color *= vignette;

    // Slight green tint for retro feel
    color.g *= 1.05;

    // Subtle flicker
    color *= 0.98 + 0.02 * sin(Time * 60.0);

    fragColor = vec4(color, 1.0);
}
