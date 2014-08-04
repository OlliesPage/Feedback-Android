attribute vec2 position;

varying vec4 f_color;

uniform float offset_x;
uniform float scale_x;
uniform mat4 scale_m;

void main(void) {
    gl_Position = scale_m * vec4((position.x + offset_x) * scale_x, position.y, 0, 1);
    f_color = vec4(position.xy / 2.0 + 0.5, 1, 1);
}