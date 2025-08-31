#version 330 core

in vec4 fColor;
in vec2 fTexCoords;
in float fTexId;
in float fEntityId;

uniform sampler2D uTextures[8];

out vec3 color;

void main() {
    vec4 texColor = vec4(1, 1, 1, 1);
    if(fTexId > 0) {
        int id = int(fTexId);
        switch(id) {
            case 0:
                texColor = fColor * texture(uTextures[0], fTexCoords);
                break;
            case 1:
                texColor = fColor * texture(uTextures[1], fTexCoords);
                break;
            case 2:
                texColor = fColor * texture(uTextures[2], fTexCoords);
                break;
            case 3:
                texColor = fColor * texture(uTextures[3], fTexCoords);
                break;
            case 4:
                texColor = fColor * texture(uTextures[4], fTexCoords);
                break;
            case 5:
                texColor = fColor * texture(uTextures[5], fTexCoords);
                break;
            case 6:
                texColor = fColor * texture(uTextures[6], fTexCoords);
                break;
            case 7:
                texColor = fColor * texture(uTextures[7], fTexCoords);
                break;
        }
    }

    if(texColor.a < 0.5) {
        discard;
    }
    color = vec3(fEntityId, fEntityId, fEntityId);
}