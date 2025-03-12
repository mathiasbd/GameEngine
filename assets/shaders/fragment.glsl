#type fragment
#version 330 core

in vec4 fColor;
in vec2 fTexCoords;

uniform sampler2D TEX_SAMPLER;

out vec4 color;

void main()
{
    color = texture(TEX_SAMPLER, fTexCoords);
}