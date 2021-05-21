package com.ramirocejas.galaga;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;

public class ExplosionMalo extends Explosion{
    ExplosionMalo(Rectangle box) {
        super(new Texture("explosionMalo.png"), box, 0.7f);
    }
}
