#type vertex
#version 330 core
layout (location=0) in vec3 aPos;
layout (location=1) in vec4 aColor;
layout (location=2) in vec2 aTexCoords;

uniform mat4 uProjection;
uniform mat4 uView;

out vec2 fTexCoords;
out vec4 fColor;
void main()
{
    fColor = aColor;
    fTexCoords = aTexCoords;
    gl_Position = uProjection * uView * vec4(aPos, 1.0);
}