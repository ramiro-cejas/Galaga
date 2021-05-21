package com.ramirocejas.galaga;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;

public class ExplosionJugador extends Explosion{
    ExplosionJugador(Rectangle box) {
        super(new Texture("explosionJugador.png"), box, 0.7f);
    }
}
