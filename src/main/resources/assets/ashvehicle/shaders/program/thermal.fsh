#version 150

uniform sampler2D DiffuseSampler;
uniform vec2 InSize;
uniform vec2 OutSize;

out vec4 fragColor;

float lum(vec3 c) {
    return dot(c, vec3(0.299, 0.587, 0.114));
}

void main() {
    vec2 tex = gl_FragCoord.xy / InSize;
    
    // Sample original color
    vec4 color = texture(DiffuseSampler, tex);
    
    // Convert to grayscale for thermal background
    float gray = lum(color.rgb);
    
    // Darken the background slightly for contrast with white entities
    gray = gray * 0.6;
    
    fragColor = vec4(vec3(gray), 1.0);
}
