package com.ramirocejas.galaga;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;


public abstract class Explosion {

    private Animation<TextureRegion> explosionAnimacion;
    private float explosionTimer;

    private Rectangle box;

    Explosion (Texture texture, Rectangle box, float tiempoTotalAnimacion){
        this.box = box;

        TextureRegion[][] region2D = TextureRegion.split(texture, 128, 128);

        TextureRegion[] texturaID = new TextureRegion[4];
        int indice = 0;

        for(int i = 0 ; i<2; i++){
            for (int j = 1; j<2; j++){
                texturaID[indice] = region2D[i][j];
                indice++;
            }
        }

        explosionAnimacion = new Animation<TextureRegion>(tiempoTotalAnimacion/4, texturaID);
        explosionTimer = 0;
    }

    public void update (float delta){
        explosionTimer += delta;
    }

    public void draw (SpriteBatch batch){
        if (explosionAnimacion.getKeyFrame(explosionTimer) != null){
            batch.draw(explosionAnimacion.getKeyFrame(explosionTimer), box.x,box.y,box.width,box.height);
        }
    }

    public boolean termino(){
        return (explosionAnimacion.isAnimationFinished(explosionTimer));
    }

}
