package br.unitins.arkanoid;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class TelaPrincipal extends AppCompatActivity {


    //Declara referencia para amarelo superficie de desenho
    private GLSurfaceView superficieDesenho = null;
    //declara referencia para o Render
    private Render render = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        //Instancia um objeto da superficie de desenho
        superficieDesenho = new GLSurfaceView(this);
        //instancia o objeto renderizador
        render = new Render(this);


        //Configura o objeto de desenho na superficie
        superficieDesenho.setRenderer(render);
        superficieDesenho.setOnTouchListener(render);


        //Publicar amarelo superficie de desenho na tela
        setContentView(superficieDesenho);

    }
}

//Classe que ira implementar amarelo logica do desenho
class Render implements GLSurfaceView.Renderer, View.OnTouchListener, SensorEventListener {

    ArrayList<Geometria> formas = null;

    GL10 gl;
    Geometria amarelo = null;
    Geometria preto = null;
    Geometria vermelho = null;

    int flag = 1;

    float largura = 0;
    float altura = 0;
    static float TAMANHO = 200;
    float toqueX, toqueY;
    float angulo =0;

    float sensorX, sensorY, sensorZ;
    long inicioToque;
    AppCompatActivity tela;

    private SensorManager sensor;
    private Sensor acelerometro;

    public Render(AppCompatActivity activity) {

        this.tela = activity;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        //define amarelo cor de limpeza no formato RGBA
        gl.glClearColor(1, 1, 1, 1);
        sensor = (SensorManager) tela.getSystemService(Context.SENSOR_SERVICE);

        acelerometro = sensor.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensor.registerListener(this, acelerometro, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        formas = new ArrayList<>();


        Log.i("tela", "X: " + width + " Y: " + height);
        //configurando amarelo area de coordenadas do plano cartesiano
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();

        altura = height;
        largura = width;
        TAMANHO = altura/4;
        //configurando o volume de renderização
        gl.glOrthof(0, largura,
                0, altura,
                1, -1);

        //configurando amarelo matriz de Transferencias geometricas
        //translação, rotação e escala
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();

        //configura amarelo area de visualização na tela do DISP
        gl.glViewport(0, 0, width, height);

        //Habilita o desenho por vertices
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);

        amarelo = new Quadrado(gl, TAMANHO);
        preto = new Quadrado(gl, TAMANHO);
        vermelho = new Quadrado(gl,TAMANHO).setEscala(0.5f,1).setRotacao(angulo);
        vermelho.setCor(Geometria.VERMELHO);
        this.gl = gl;
//        preencheQuadrados(4);

    }


    private boolean sobreposto(float x, float y) {

        for (Geometria forma : formas) {
            if (tocar(x, y, forma)) {
                return true;
            }
        }
        return false;
    }

    private void preencheQuadrados(int quant) {

        float vermelho;
        float verde;
        float azul;
        float x;
        float y;
        boolean pronto = false;
        Geometria tijolo;
        for (int i = 0; i < quant; i++) {

            vermelho = (float) Math.random();
            verde = (float) Math.random();
            azul = (float) Math.random();
            x = (float) Math.random();
            y = (float) Math.random();
            tijolo = new Quadrado(gl, 200)
                    .setCor(vermelho, verde, azul)
                    .setEscala(1, 0.5f)
                    .setXY(x, y)
                    .setRotacao(0);

            do {
                if (naTelaX(tijolo, 0)) {
                    formas.add(tijolo);
                    Log.i("tijolinho", "adicionado " + i);
                    pronto = true;

                } else {
                    x = (float) Math.random();
                    y = (float) Math.random();
                    tijolo.setXY(x, y);

                }
            } while (!pronto);
        }

    }

    @Override
    public void onDrawFrame(GL10 gl) {
        this.gl = gl;
        //Aplica amarelo cor de limpeza da tela amarelo todos os bits do buffer de cor
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

        vermelho.desenha();
        vermelho.setXY(largura/2,0);
        vermelho.setRotacao(angulo);
        //empilha uma transformação geometrica
        gl.glPushMatrix();

            amarelo.gl.glColor4f(1,1,0,1);
            amarelo.gl.glTranslatef(0, TAMANHO, 0);
            amarelo.gl.glRotatef(angulo,0,0,1);
            amarelo.gl.glScalef(1, 1, 1);
            amarelo.setXY(vermelho.getPosX(),vermelho.getPosY()+TAMANHO);

            amarelo.gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
            gl.glPushMatrix();
                preto.gl.glColor4f(0,0,0,1);
                preto.gl.glTranslatef(0, TAMANHO, 0);
                preto.gl.glRotatef(angulo * flag,0,0,1);
                preto.gl.glScalef(1, 1, 1);
                preto.setXY(vermelho.getPosX(),vermelho.getPosY()+TAMANHO);

                preto.gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
            gl.glPopMatrix();
        gl.glPopMatrix();
        if( flag == 1){
            angulo++;
        }
        else{
            angulo--;
        }
        if(angulo <=-30 || angulo >=30){
            flag*=-1;
        }

    }

    private boolean naTelaY(Geometria g, float mov) {
        return g.getPosY() <= altura - (g.tamanho / 2 * g.escalaY) && mov < 0
                || g.getPosY() >= (g.tamanho / 2 * g.escalaY) && mov > 0;
    }

    private boolean naTelaX(Geometria g, float mov) {
        return g.getPosX() <= largura - (g.tamanho / 2 * g.escalaX) && mov < 0
                || (g.getPosX() >= (g.tamanho / 2 * g.escalaX) && mov > 0)
                || (mov == 0 && g.getPosX() <= largura - (g.tamanho / 2 * g.escalaX) && g.getPosX() >= (g.tamanho / 2 * g.escalaX));
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        final float x = event.getX();
        final float y = event.getY();
        float yCalc = altura - y;
        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:


                if (tocar(x, yCalc, this.vermelho)
                        ) {
                    this.vermelho.selecionado = 1;
                }

                if (tocar(x, yCalc, this.amarelo)
                        ) {
                    this.amarelo.selecionado = 1;
                }
                if (tocar(x, yCalc, this.preto)
                        ) {
                    this.preto.selecionado = 1;
                }
                break;

            case MotionEvent.ACTION_MOVE:

                if (tocar(x, yCalc, this.vermelho)
                    && this.vermelho.selecionado == 1
                    ) {
                this.vermelho.setXY(x, yCalc);
            }
                if (tocar(x, yCalc, this.amarelo)
                        && this.amarelo.selecionado == 1
                        ) {
                    this.amarelo.setXY(x, yCalc);
                }
                if (tocar(x, yCalc, this.preto)
                    && this.preto.selecionado == 1
                    ) {
                this.preto.setXY(x, yCalc);
            }
                    break;

            case MotionEvent.ACTION_UP:
                this.vermelho.selecionado = 0;
                this.amarelo.selecionado = 0;
                this.preto.selecionado = 0;
                break;
        }

        return true;
    }



    private boolean tocar(float x, float yCalc, Geometria g) {
        return (x >= (g.getPosX() - (g.tamanho / 2) * g.escalaX)
                && x <= (g.getPosX() + (g.tamanho / 2) * g.escalaX)
        )
                && (yCalc >= (g.getPosY() - (g.tamanho / 2) * g.escalaY)
                && yCalc <= (g.getPosY() + (g.tamanho / 2) * g.escalaY)
        );

    }


    @Override
    public void onSensorChanged(SensorEvent event) {

        sensorX = event.values[0];
        sensorY = event.values[1];
        sensorZ = event.values[2];
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
