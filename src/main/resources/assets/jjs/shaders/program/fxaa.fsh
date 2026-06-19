#version 150

uniform sampler2D DiffuseSampler;

uniform vec2 InSize;
uniform float SpanMax;
uniform float ReduceMul;
uniform float ReduceMin;

in vec2 texCoord;

out vec4 fragColor;

void main() {
    vec2 inverseSize = 1.0 / InSize;

    vec3 rgbNW = texture(DiffuseSampler, texCoord + vec2(-1.0, -1.0) * inverseSize).rgb;
    vec3 rgbNE = texture(DiffuseSampler, texCoord + vec2( 1.0, -1.0) * inverseSize).rgb;
    vec3 rgbSW = texture(DiffuseSampler, texCoord + vec2(-1.0,  1.0) * inverseSize).rgb;
    vec3 rgbSE = texture(DiffuseSampler, texCoord + vec2( 1.0,  1.0) * inverseSize).rgb;
    vec3 rgbM  = texture(DiffuseSampler, texCoord).rgb;

    vec3 luma = vec3(0.299, 0.587, 0.114);
    float lumaNW = dot(rgbNW, luma);
    float lumaNE = dot(rgbNE, luma);
    float lumaSW = dot(rgbSW, luma);
    float lumaSE = dot(rgbSE, luma);
    float lumaM  = dot(rgbM,  luma);

    float lumaMin = min(lumaM, min(min(lumaNW, lumaNE), min(lumaSW, lumaSE)));
    float lumaMax = max(lumaM, max(max(lumaNW, lumaNE), max(lumaSW, lumaSE)));

    vec2 dir;
    dir.x = -((lumaNW + lumaNE) - (lumaSW + lumaSE));
    dir.y =  ((lumaNW + lumaSW) - (lumaNE + lumaSE));

    float dirReduce = max((lumaNW + lumaNE + lumaSW + lumaSE) * (0.25 * ReduceMul), ReduceMin);
    float rcpDirMin = 1.0 / (min(abs(dir.x), abs(dir.y)) + dirReduce);

    dir = min(vec2(SpanMax, SpanMax),
              max(vec2(-SpanMax, -SpanMax),
                  dir * rcpDirMin)) * inverseSize;

    vec3 rgbA = 0.5 * (
        texture(DiffuseSampler, texCoord + dir * (1.0 / 3.0 - 0.5)).rgb +
        texture(DiffuseSampler, texCoord + dir * (2.0 / 3.0 - 0.5)).rgb);
    vec3 rgbB = rgbA * 0.5 + 0.25 * (
        texture(DiffuseSampler, texCoord + dir * -0.5).rgb +
        texture(DiffuseSampler, texCoord + dir *  0.5).rgb);

    float lumaB = dot(rgbB, luma);

    if ((lumaB < lumaMin) || (lumaB > lumaMax)) {
        fragColor = vec4(rgbA, 1.0);
    } else {
        fragColor = vec4(rgbB, 1.0);
    }
}
