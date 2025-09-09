#version 330 core

in vec3 iColor;

out vec4 oColor;

void main() {
    oColor = vec4(iColor, 1);
}