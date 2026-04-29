#version 150

uniform sampler2D DiffuseSampler;

in vec2 texCoord;
out vec4 fragColor;

float luminance(vec3 c) {
    return dot(c, vec3(0.299, 0.587, 0.114));
}

void main() {
    vec3 col = texture(DiffuseSampler, texCoord).rgb;

    float l = luminance(col);

    // ===== 背景は白黒 =====
    vec3 gray = vec3(l);

    // ===== ★ここが重要（白化条件）=====
    // 明るい＝熱源（エンティティ・パーティクル）
    if (l > 0.6) {
        fragColor = vec4(1.0); // 完全白
        return;
    }

    // 背景
    fragColor = vec4(gray, 1.0);
}