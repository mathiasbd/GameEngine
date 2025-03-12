#type fragment
#version 330 core

uniform float uTime;

in vec4 fColor;
in vec2 fTexCoords;

uniform sampler2D TEX_SAMPLER;

out vec4 color;

void main()
{
    color = texture(TEX_SAMPLER, fTexCoords);
    // color = sin(uTime) * color; // this will make the texture flicker (just a test to see if time implementation works)
}