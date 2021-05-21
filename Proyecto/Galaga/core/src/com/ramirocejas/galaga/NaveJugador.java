package com.ramirocejas.galaga;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class NaveJugador extends Nave{

    protected Sound sonidoDaño;

    public NaveJugador(float velocidadMovimiento,
                       float xCentro, float yCentro,
                       float width, float heigh,
                       TextureRegion naveTextura,
                       int puntosVida, TextureRegion disparoTextura,
                       float disparoWidth, float disparoHeight,
                       float disparoVelocidad, float tiempoEntreDisparos) {

        super(velocidadMovimiento, xCentro, yCentro, width, heigh, naveTextura, puntosVida, disparoTextura, disparoWidth, disparoHeight, disparoVelocidad, tiempoEntreDisparos);
        sonidoDisparo = Gdx.audio.newSound(Gdx.files.internal("sonidos/Disparo.mp3"));
        sonidoDaño = Gdx.audio.newSound(Gdx.files.internal("sonidos/RecibirDaño.mp3"));
    }

    @Override
    public Disparo[] disparar() {
        Disparo[] disparo = new Disparo[2];
        disparo[0] = new Disparo(disparoVelocidad, thisBox.x+thisBox.width*0.2f, thisBox.y+thisBox.height, disparoWidth, disparoHeight, disparoTextura);
        disparo[1] = new Disparo(disparoVelocidad, thisBox.x+thisBox.width*0.8f, thisBox.y+thisBox.height, disparoWidth, disparoHeight, disparoTextura);

        sonidoDisparo.play();

        tiempoDesdeUltimoDisparo = 0;

        return disparo;
    }

    public boolean acierto(Disparo disparo){
        sonidoDaño.play();
        return super.acierto(disparo);
    }


    @Override
    public Vector2 getDireccion() {
        return null;
    }
}
