package br.unitins.arkanoid;


import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

public abstract class Geometria {
    public  abstract void desenha();

    //variaveis de controle de cor
    static final int AZUL = 0;
    static final int VERMELHO = 1;
    static final int VERDE = 2;
    static final int AMARELO = 3;
    static final int MAGENTA = 4;
    static final int CIANO = 5;
    static final int BRANCO = 6;
    static final int ALEATORIO = 7;
    static final int PRETO = 8;


    protected GL10 gl;
    protected float rotacao;
    protected float posX;
    protected float posY;
    protected float vermelho;
    protected float verde;
    protected float azul;
    protected float escalaX, escalaY;
    protected float[] coordenadas;
    protected float tamanho;
    protected int selecionado;
    FloatBuffer buffer1;
    protected Geometria(GL10 gl, float tamanho){

        this.gl = gl;
        geraCor();
        setRotacao(0);
        setEscala(1,1);
        setXY(tamanho,tamanho);
        selecionado = 0;
    }




    protected FloatBuffer generateBuffer(float[] vetor){
        //aloca memoria em bytes para os vertices
        ByteBuffer prBuffer = ByteBuffer.allocateDirect(vetor.length * 4);
        //ordena os endereços de memoria conforme amarelo arquitetura do processador
        prBuffer.order(ByteOrder.nativeOrder());

        //Gera o encapsulador, limpa, insere o vetor, retira eventuais sobras de memoria
        FloatBuffer prFloat = prBuffer.asFloatBuffer();
        prFloat.clear();
        prFloat.put(vetor);
        prFloat.flip();

        return prFloat;

    }
    public Geometria setXY(float x, float y ){

        posX = x;
        posY = y;
        return this;
    }
    public Geometria setEscala(float x, float y){
        escalaX = x;
        escalaY = y;
        return this;
    }
    public Geometria geraCor(){
        vermelho = (float)Math.random();
        verde = (float)Math.random();
        azul= (float)Math.random();

        return this;
    }
    public Geometria setCor(int i){
        switch (i){
            case Geometria.AZUL:
                setCor(0,0,1);
                break;
            case Geometria.VERMELHO:
                setCor(1,0,0);
                break;
            case Geometria.VERDE:
                setCor(0,1,0);
                break;
            case Geometria.AMARELO:
                setCor(1,1,0);
                break;
            case Geometria.MAGENTA:
                setCor(1,0,1);
                break;
            case Geometria.CIANO:
                setCor(0,1,1);
                break;
            case Geometria.BRANCO:
                setCor(1,1,1);
                break;
            case Geometria.ALEATORIO:
                geraCor();
                break;
            case Geometria.PRETO:
                setCor(0,0,0);
                break;
        }
        return this;

    }
    public Geometria setCor(float vermelho, float verde, float azul){
        this.vermelho = vermelho;
        this.verde = verde;
        this.azul = azul;
        return this;
    }
    public float getRotacao() {
        return rotacao;
    }

    public Geometria setRotacao(float rotacao) {
        this.rotacao = rotacao;
        return this;
    }

    public float getPosX() {
        return posX;
    }
    public float getPosY() {
        return posY;
    }

}
