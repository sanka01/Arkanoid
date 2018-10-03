package br.unitins.arkanoid;

import javax.microedition.khronos.opengles.GL10;

public class Paralelogramo extends Geometria {

    public Paralelogramo(GL10 gl, float tamanho){
        super(gl,tamanho);
        float[] vetCoordenadas = {
               -tamanho, -tamanho/2,
                tamanho*1.5f,tamanho/2,
                0,tamanho/2,
                tamanho/2, -tamanho/2,
                -tamanho, -tamanho/2
        };

        this.coordenadas = vetCoordenadas;
        this.tamanho = tamanho;
        buffer1 = generateBuffer(coordenadas);

    }
    @Override
    public void desenha() {
        gl.glLoadIdentity();

        //registra o vetor de coordenadas na OpenGl
        gl.glVertexPointer(2,GL10.GL_FLOAT,
                0, buffer1);

        gl.glColor4f(vermelho,verde,azul,1);

        gl.glTranslatef(posX,posY,0);

        gl.glRotatef(getRotacao(), 0, 0, 1);

        gl.glScalef(escalaX,escalaY,1);

        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP,0,5);

    }
}
