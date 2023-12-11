attribute vec4 aVertex, aColour;
varying vec4 vColour;
uniform mat4 uPerspMtx, uMvMtx;
void main(void) {
    gl_Position = uPerspMtx * uMvMtx * aVertex;
    vColour = aColour;
}