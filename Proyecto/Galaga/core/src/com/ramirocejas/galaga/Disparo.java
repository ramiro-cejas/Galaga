package com.ramirocejas.galaga;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public class Disparo {
    //Fisicas
    float velocidad;

    //Posicion y Dimensión
    float xPosicion, yPosicion;
    float width, height;

    //Textura
    TextureRegion textureRegion;

    public Disparo(float velocidad, float xPosicion, float yPosicion, float width, float height, TextureRegion textureRegion) {
        this.velocidad = velocidad;
        this.xPosicion = xPosicion;
        this.yPosicion = yPosicion;
        this.width = width;
        this.height = height;
        this.textureRegion = textureRegion;
    }

    public void draw(Batch batch){
        batch.draw(textureRegion,xPosicion - width/2,yPosicion,width,height);
    }

    public Rectangle getBox(){
        return new Rectangle(xPosicion,yPosicion,width,height);
    }

}
