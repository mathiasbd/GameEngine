#type fragment
#version 330 core


in vec4 fColor;
in vec2 fTexCoords;
in float fTexId;

uniform sampler2D uTexture[8];

out vec4 color;

void main()
{
    if (fTexId>0){
     int id =int (fTexId);
     color = fColor*texture(uTexture[id],fTexCoords);
     //color =vec4(fTexCoords,0,1);
    } else {
        color = fColor;
    }


}
