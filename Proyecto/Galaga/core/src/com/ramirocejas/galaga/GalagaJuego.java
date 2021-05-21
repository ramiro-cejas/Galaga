package com.ramirocejas.galaga;

import com.badlogic.gdx.Game;

import java.util.Random;

public class GalagaJuego extends Game {
    GalagaScreen galagaScreen;

    public static Random random = new Random();

    @Override
    public void create(){
        galagaScreen = new GalagaScreen();
        setScreen(galagaScreen);
    }

    @Override
    public void dispose() {
        galagaScreen.dispose();
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void resize(int width, int height) {
        galagaScreen.resize(width, height);
    }
}
