package com.ramirocejas.galaga;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

abstract class Nave {

    //Caracteristicas
    protected float velocidadMovimiento;
    protected int puntosVida;

    //Posicion y dimensiones
    protected Rectangle thisBox;

    //Disparo
    protected float disparoWidth, disparoHeight;
    protected float tiempoEntreDisparos;
    protected float disparoVelocidad;
    protected float tiempoDesdeUltimoDisparo = 0;
    protected Sound sonidoDisparo;

    //Graficos
    protected TextureRegion naveTextura, disparoTextura;

    protected Vector2 direccion;


    public Nave(float velocidadMovimiento, float xCentro,
                float yCentro, float width, float heigh,
                TextureRegion naveTextura, int puntosVida,
                TextureRegion disparoTextura, float disparoWidth,
                float disparoHeight, float disparoVelocidad,
                float tiempoEntreDisparos) {

        this.velocidadMovimiento = velocidadMovimiento;
        this.naveTextura = naveTextura;
        this.puntosVida = puntosVida;
        this.disparoTextura = disparoTextura;
        this.disparoWidth = disparoWidth;
        this.disparoHeight = disparoHeight;
        this.disparoVelocidad = disparoVelocidad;
        this.tiempoEntreDisparos = tiempoEntreDisparos;
        this.thisBox = new Rectangle(xCentro - width / 2, yCentro - heigh / 2, width, heigh);

        direccion = new Vector2(0, -1);
    }

    public void update(float delta){
        tiempoDesdeUltimoDisparo = tiempoDesdeUltimoDisparo + delta;
    }

    public boolean puedeDisparar(){
        return (tiempoDesdeUltimoDisparo - tiempoEntreDisparos)>0;
    }

    public abstract Disparo[] disparar();

    public void draw(Batch batch){
        batch.draw(naveTextura,thisBox.x,thisBox.y,thisBox.width,thisBox.height);
    }

    public boolean intersecta(Rectangle objeto){
        return thisBox.overlaps(objeto);
    }

    public boolean acierto(Disparo disparo){
        if (puntosVida > 0){
            puntosVida--;
        }
        return (puntosVida <= 0);
    }

    public Vector2 getDireccion() {
        return direccion;
    }

    private void randomDireccion(){
        double llevar = GalagaJuego.random.nextDouble()*6.283185; //De 0 a 2PI
        direccion.x = (float) Math.sin(llevar);
        direccion.y = (float) Math.cos(llevar);
    }
    public void mover(float xCambio, float yCambio){
        thisBox.setPosition(thisBox.x+xCambio, thisBox.y+yCambio);
    }
}
