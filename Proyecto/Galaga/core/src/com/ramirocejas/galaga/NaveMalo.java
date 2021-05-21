package com.ramirocejas.galaga;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class NaveMalo extends Nave{

    private static Sound sonidoDisparo;
    private float tiempoDesdeMovimiento = 0;
    private float tiempoEntreMovimiento = 0.70f;
    private int cantidadDisparos;

    public NaveMalo(float velocidadMovimiento,
                    float xCentro, float yCentro,
                    float width, float heigh,
                    TextureRegion naveTextura,
                    int puntosVida, TextureRegion disparoTextura,
                    float disparoWidth, float disparoHeight,
                    float disparoVelocidad, float tiempoEntreDisparos, int cantidadDisparos) {

        super(velocidadMovimiento, xCentro, yCentro, width, heigh, naveTextura, puntosVida, disparoTextura, disparoWidth, disparoHeight, disparoVelocidad, tiempoEntreDisparos);

        direccion = new Vector2(0, -1);
        sonidoDisparo = Gdx.audio.newSound(Gdx.files.internal("sonidos/Malos_3.mp3"));
        this.cantidadDisparos = cantidadDisparos;
    }

    public Vector2 getDireccion() {
        return direccion;
    }

    private void randomDireccion(){
        double llevar = GalagaJuego.random.nextDouble()*6.283185; //De 0 a 2PI
        direccion.x = (float) Math.sin(llevar);
        direccion.y = (float) Math.cos(llevar);
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        tiempoDesdeMovimiento += delta;
        if (tiempoDesdeMovimiento > tiempoEntreMovimiento){
            randomDireccion();
            tiempoDesdeMovimiento -= tiempoEntreMovimiento;
        }
    }

    @Override
    public Disparo[] disparar() {
        Disparo[] disparo = new Disparo[cantidadDisparos];
        if (cantidadDisparos == 2) {
            disparo[0] = new Disparo(disparoVelocidad, thisBox.x + thisBox.width * 0.26f, thisBox.y + thisBox.height * 0.80f - thisBox.height, disparoWidth, disparoHeight, disparoTextura);
            disparo[1] = new Disparo(disparoVelocidad, thisBox.x + thisBox.width * 0.71f, thisBox.y + thisBox.height * 0.80f - thisBox.height, disparoWidth, disparoHeight, disparoTextura);
        }
        if (cantidadDisparos == 1) {
            disparo[0] = new Disparo(disparoVelocidad, thisBox.x+thisBox.width*0.5f, thisBox.y+thisBox.height*0.80f-thisBox.height, disparoWidth, disparoHeight, disparoTextura);
        }
        sonidoDisparo.setVolume(sonidoDisparo.play(),0.2f);

        tiempoDesdeUltimoDisparo = 0;

        return disparo;
    }
}
