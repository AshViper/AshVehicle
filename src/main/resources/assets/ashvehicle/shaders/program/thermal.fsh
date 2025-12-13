#version 150

uniform sampler2D DiffuseSampler;
uniform vec2 InSize;   // 入力テクスチャの実際のピクセルサイズ
uniform vec2 OutSize;

out vec4 fragColor;

float lum(vec3 c) {
    return dot(c, vec3(0.299, 0.587, 0.114));
}

void main() {
    // ピクセル基準の正しい UV
    vec2 tex = gl_FragCoord.xy / InSize;
    vec2 px = 1.0 / InSize;

    // 3x3 Luminance sampling
    float tl = lum(texture(DiffuseSampler, tex + vec2(-px.x, -px.y)).rgb);
    float  t = lum(texture(DiffuseSampler, tex + vec2( 0.0, -px.y)).rgb);
    float tr = lum(texture(DiffuseSampler, tex + vec2( px.x, -px.y)).rgb);

    float l  = lum(texture(DiffuseSampler, tex + vec2(-px.x,  0.0)).rgb);
    float c  = lum(texture(DiffuseSampler, tex).rgb);
    float r  = lum(texture(DiffuseSampler, tex + vec2( px.x,  0.0)).rgb);

    float bl = lum(texture(DiffuseSampler, tex + vec2(-px.x,  px.y)).rgb);
    float  b = lum(texture(DiffuseSampler, tex + vec2( 0.0,  px.y)).rgb);
    float br = lum(texture(DiffuseSampler, tex + vec2( px.x,  px.y)).rgb);

    // 正しい Sobel カーネル
    float gx = (tr + 2.0*r + br) - (tl + 2.0*l + bl);
    float gy = (bl + 2.0*b + br) - (tl + 2.0*t + tr);

    // 勾配の大きさ
    float edge = sqrt(gx * gx + gy * gy);

    // 膨張しない 1px エッジ → threshold を高めに
    float glow = step(0.20, edge);

    // 映像本体をグレースケール化
    float gray = c;

    // 白輪郭を上描き（1px）
    vec3 finalColor = mix(vec3(gray), vec3(1.0), glow);

    fragColor = vec4(finalColor, 1.0);
}
