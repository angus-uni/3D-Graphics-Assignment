#version 330 core

out vec4 fragColor;

uniform vec3 lightColour;

void main() {
    fragColor = vec4(lightColour, 1.0);
}