package com.ramirocejas.galaga;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Locale;


public class GalagaScreen implements Screen {

    //Pantalla
    private Camera camara;
    private Viewport viewp;

    //Graficos
    private SpriteBatch batch;
    private TextureAtlas textureAtlas;
    private TextureRegion[] fondos;

    //Sonidos
    private Sound sonidoExplosion;
    private Music sonidoInicio, sonidoGanaste, sonidoPerdiste;

    //Timing
    private float[] offSets = {0,0,0};
    private float maximaVelocidadFondo;

    //Parametros
    private final int WIDHT = 72;
    private final int HEIGHT = 128;
    private final float UMBRAL_MOVIMIENTO_TOUCH = 0.5f;
    private int nivelActual = 0;
    private int enemigosVivos = -1;
    private int puntaje = 0;
    private boolean estaPresionado = false;
    private boolean habilitar = false;
    private boolean estaSonandoInicio = false;
    private boolean pausado = false;
    private boolean estaSonandoFin = false;
    private boolean dispararonPares = false;
    private float tiempoEntreDisparosEnemigos = 0;
    private float tiempoUltimoDisparoEnemigo = 0;
    private int vidaEnemigos = 0;

    //Objetos del juego
    private Nave naveJugador;
    private Nave[] navesMalos;
    private LinkedList<Disparo> disparosJugador;
    private LinkedList<Disparo>[] disparosMalos;
    private LinkedList<Explosion> explosiones;

    //Estados
    private Object estadoInicio = new Object();
    private Object estadoNivel1 = new Object();
    private Object estadoNivel2 = new Object();
    private Object estadoNivel3 = new Object();
    private Object estadoGanaste = new Object();
    private Object estadoPerdiste = new Object();
    private Object estadoPausado = new Object();

    private Object estadoActual = estadoInicio;

    //Fuentes
    BitmapFont fuente, fuente2, fuente3, fuente4;
    float hudMargenVertical, hudIzquierdoX, hudDerechoX, hudCentroX, hudLinea1, hudLinea2, hudWidth;

    GalagaScreen(){
        camara = new OrthographicCamera(); //Camara en 2d
        viewp = new StretchViewport(WIDHT,HEIGHT,camara);

        sonidoInicio = Gdx.audio.newMusic(Gdx.files.internal("sonidos/Intro.mp3"));
        sonidoExplosion = Gdx.audio.newSound(Gdx.files.internal("sonidos/Explosion.mp3"));
        sonidoPerdiste = Gdx.audio.newMusic(Gdx.files.internal("sonidos/Perdiste.mp3"));
        sonidoGanaste = Gdx.audio.newMusic(Gdx.files.internal("sonidos/Ganaste.mp3"));

        maximaVelocidadFondo = HEIGHT / 4;

        //Texturas Atlas
        textureAtlas = new TextureAtlas("imagenes.atlas");

        //Texturas


        //Objetos
        explosiones = new LinkedList<>();

        batch = new SpriteBatch();

        hud();
    }

    private void hud(){
        FreeTypeFontGenerator fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("emulogic.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter fontParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();

        fontParameter.size = 72;
        fontParameter.borderWidth = 3.6f;
        fontParameter.color = new Color(1, 1, 1, 0.3f);
        fontParameter.borderColor = new Color(0,0,0,0.3f);

        fuente = fontGenerator.generateFont(fontParameter);
        fuente2 = fontGenerator.generateFont(fontParameter);
        fuente3 = fontGenerator.generateFont(fontParameter);
        fuente4 = fontGenerator.generateFont(fontParameter);

        fuente.getData().setScale(0.04f);

        fuente2.getData().setScale(0.1f);

        fuente3.getData().setScale(0.03f);

        fuente4.getData().setScale(0.03f);

        hudMargenVertical = fuente.getCapHeight() / 2;
        hudIzquierdoX = hudMargenVertical;
        hudDerechoX = WIDHT * 2/3 - hudIzquierdoX;
        hudCentroX = WIDHT / 3;
        hudLinea1 = HEIGHT - hudMargenVertical;
        hudLinea2 = hudLinea1 - hudMargenVertical - fuente.getCapHeight();
        hudWidth = WIDHT / 3;

    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        batch.begin();
        if (estadoActual == estadoInicio){
            Inicio();
            estaPresionado = Gdx.input.isTouched();
            if(!estaPresionado){
                habilitar = true;
            }
            if (habilitar && Gdx.input.isTouched()) {
                enemigosVivos = 0;
                if (estaSonandoInicio){
                    sonidoInicio.stop();
                    estaSonandoInicio = false;
                }
                habilitar = false;
                nivel1();
            }
        }

        if (enemigosVivos<=0 && estadoActual == estadoNivel1){
            enemigosVivos = 0;
            nivel2();
        }

        if (enemigosVivos<=0 && estadoActual == estadoNivel2){
            enemigosVivos = 0;
            nivel3();
        }

        //Fondo animado
        fondoAnimado(delta);

        if (estadoActual!=estadoInicio && estadoActual!=estadoPausado) moverMalos(delta);

        if (estadoActual!=estadoInicio && estadoActual!=estadoPausado){
            for(int i = 0; i< navesMalos.length; i++){
                if (navesMalos[i] != null)
                    navesMalos[i].update(delta);
            }
        }
        //Malos
        if (estadoActual!=estadoInicio){
            for(int i = 0; i< navesMalos.length; i++){
                if (navesMalos[i]!=null)
                    navesMalos[i].draw(batch);
            }
        }

        //Disparos
        if (estaJugando()) disparos(delta);

        //Explosiones
        if (estadoActual!= estadoInicio) explosiones(delta);

        //Input
        if (estaJugando()) detectarInput(delta);

        //Nave jugador
        if (estaJugando()) naveJugador.update(delta);
        if (estaJugando() || estadoActual == estadoPausado) naveJugador.draw(batch);

        //Colisiones
        if (estaJugando()) detectarColisiones();

        //Perdiste
        if ((estaJugando()) && naveJugador.puntosVida<=0) estadoActual = estadoPerdiste;

        //Ganaste
        if (enemigosVivos == 0 && (estadoActual==estadoNivel3)) estadoActual = estadoGanaste;


        if(estadoActual == estadoPerdiste){
            hudPerdiste();
            estaPresionado = Gdx.input.isTouched();
            if(!estaPresionado){
                habilitar = true;
            }
            if (habilitar && Gdx.input.isTouched()){
                habilitar = false;
                setearInicio();
            }
        }

        if (estadoActual == estadoGanaste){
            hudGanaste();
            estaPresionado = Gdx.input.isTouched();
            if(!estaPresionado){
                habilitar = true;
            }
            if (habilitar && Gdx.input.isTouched()){
                setearInicio();
            }
        }

        if (estadoActual == estadoInicio){
            hudInicio();
        }

        if (estadoActual != estadoInicio) renderHUD();

        if (estaJugando()  && !Gdx.input.isTouched()) {
            estadoActual = estadoPausado;
        }

        if (estadoActual == estadoPausado) {

            hudPausa();
            if (Gdx.input.isTouched()){
                if (nivelActual==1) estadoActual = estadoNivel1;
                if (nivelActual==2) estadoActual = estadoNivel2;
                if (nivelActual==3) estadoActual = estadoNivel3;
            }
        }

        batch.end();
    }

    private boolean estaJugando(){
        return (estadoActual != estadoInicio && estadoActual != estadoGanaste && estadoActual != estadoPerdiste && estadoActual != estadoPausado);
    }

    private void setearInicio() {
        if (estaSonandoFin){
            sonidoPerdiste.stop();
            sonidoGanaste.stop();
            estaSonandoFin = false;
        }
        nivelActual=0;
        enemigosVivos = -1;
        puntaje = 0;
        habilitar = false;
        estadoActual = estadoInicio;
    }

    private void Inicio(){
        //Fondo
        fondos = new TextureRegion[3];
        fondos[0] = textureAtlas.findRegion("fondo0");
        fondos[1] = textureAtlas.findRegion("fondo1");
        fondos[2] = textureAtlas.findRegion("fondo2");

        dispararonPares = false;
        tiempoUltimoDisparoEnemigo = 0;
    }

    private void hudPausa(){
        fuente2.draw(batch, "PAUSA", hudCentroX, WIDHT/2+fuente2.getCapHeight(), hudWidth, Align.center, false);
        fuente3.draw(batch, "TOCA PARA CONTINUAR", hudCentroX, WIDHT/2, hudWidth, Align.center, false);
    }

    private void hudInicio(){
        if (!estaSonandoInicio){
            sonidoInicio.play();
            estaSonandoInicio = true;
        }
        fuente2.draw(batch, "GALAGA", hudCentroX, WIDHT/2+fuente2.getCapHeight(), hudWidth, Align.center, false);
        fuente3.draw(batch, "TOCA PARA INICIAR", hudCentroX, WIDHT/2, hudWidth, Align.center, false);
        fuente4.draw(batch, "Ramiro Cejas", hudCentroX, WIDHT/2+fuente2.getCapHeight()+1.5f*fuente3.getCapHeight(), hudWidth, Align.center, false);
    }

    private void hudPerdiste(){
        if (!estaSonandoFin){
            sonidoPerdiste.play();
            estaSonandoFin = true;
        }
        fuente2.draw(batch, "PERDISTE", hudCentroX, WIDHT/2+fuente2.getCapHeight(), hudWidth, Align.center, false);
        fuente3.draw(batch, "TOCA PARA REINTENTAR", hudCentroX, WIDHT/2, hudWidth, Align.center, false);
    }

    private void hudGanaste(){
        if (!estaSonandoFin){
            sonidoGanaste.play();
            estaSonandoFin = true;
        }
        fuente2.draw(batch, "GANASTE", hudCentroX, WIDHT/2+fuente2.getCapHeight(), hudWidth, Align.center, false);
        fuente3.draw(batch, "TOCA PARA VOLVER A JUGAR", hudCentroX, WIDHT/2, hudWidth, Align.center, false);

    }

    private void renderHUD(){
        int vidas=0;
        fuente.draw(batch, "PUNTAJE", hudIzquierdoX, hudLinea1, hudWidth, Align.left, false);
        fuente.draw(batch, "NIVEL", hudCentroX, hudLinea1, hudWidth, Align.center, false);
        fuente.draw(batch, "VIDAS", hudDerechoX, hudLinea1, hudWidth, Align.right, false);

        if(naveJugador != null)
            vidas = naveJugador.puntosVida;
        else
            vidas = 0;

        fuente.draw(batch, String.format(Locale.getDefault(), "%06d", puntaje), hudIzquierdoX, hudLinea2, hudWidth, Align.left, false);
        fuente.draw(batch, String.format(Locale.getDefault(), "%01d", nivelActual), hudCentroX, hudLinea2, hudWidth, Align.center, false);
        fuente.draw(batch, String.format(Locale.getDefault(), "%02d", vidas), hudDerechoX, hudLinea2, hudWidth, Align.right, false);

        fuente.draw(batch, "VIDAS MALOS "+vidaEnemigos, hudCentroX, hudLinea2 - hudMargenVertical - fuente.getCapHeight()*2, hudWidth, Align.center, false);

    }

    private void detectarInput(float delta){
        float limiteIzq, limiteDer, limiteArriba, limiteAbajo;
        limiteIzq = -naveJugador.thisBox.x;
        limiteAbajo = -naveJugador.thisBox.y;
        limiteDer = WIDHT - naveJugador.thisBox.x - naveJugador.thisBox.width;
        limiteArriba = HEIGHT*2/3 - naveJugador.thisBox.y - naveJugador.thisBox.height;

        //Debug con teclado
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) && limiteDer > 0){
            float xCambio = naveJugador.velocidadMovimiento*delta;
            xCambio = Math.min(xCambio, limiteDer);
            naveJugador.mover(xCambio, 0f);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP) && limiteArriba > 0){
            float yCambio = naveJugador.velocidadMovimiento*delta;
            yCambio = Math.min(yCambio, limiteArriba);
            naveJugador.mover(0f, yCambio);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) && limiteIzq < 0){
            float xCambio = naveJugador.velocidadMovimiento*delta;
            xCambio = Math.max(-xCambio, limiteIzq);
            naveJugador.mover(xCambio, 0f);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN) && limiteAbajo < 0){
            float yCambio = naveJugador.velocidadMovimiento*delta;
            yCambio = Math.max(-yCambio, limiteAbajo);
            naveJugador.mover(0f, yCambio);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.E)){  //Ganar
            nivel1();
        }
        if (Gdx.input.isKeyPressed(Input.Keys.R)){  //Perder
            nivel2();
        }
        if (Gdx.input.isKeyPressed(Input.Keys.T)){  //Perder
            nivel3();
        }

        //Touch
        if (Gdx.input.isTouched()){
            //Posicion del touch
            float xTouch = Gdx.input.getX();
            float yTouch = Gdx.input.getY();

            //Convertir posicion del mundo
            Vector2 touchPunto = new Vector2(xTouch,yTouch);
            touchPunto = viewp.unproject(touchPunto);

            //Calcular x e y
            Vector2 naveJugadorCentro = new Vector2(
                    naveJugador.thisBox.x + naveJugador.thisBox.width/2,
                    naveJugador.thisBox.y + naveJugador.thisBox.height/2);

            float distanciaTouch = touchPunto.dst(naveJugadorCentro);

            if(distanciaTouch>UMBRAL_MOVIMIENTO_TOUCH){
                float xDiferencia = touchPunto.x - naveJugadorCentro.x;
                float yDiferencia = touchPunto.y - naveJugadorCentro.y;

                //Escalar a la velocidad de la nave
                float xMovimiento = xDiferencia / distanciaTouch * naveJugador.velocidadMovimiento * delta;
                float yMovimiento = yDiferencia / distanciaTouch * naveJugador.velocidadMovimiento * delta;

                if (xMovimiento > 0) xMovimiento = Math.min(xMovimiento, limiteDer);
                else xMovimiento = Math.max(xMovimiento, limiteIzq);

                if (yMovimiento > 0) yMovimiento = Math.min(yMovimiento, limiteArriba);
                else yMovimiento = Math.max(yMovimiento, limiteAbajo);

                naveJugador.mover(xMovimiento,yMovimiento);
            }

        }
    }

    private void fondoAnimado(float delta){

        offSets[0] += delta * maximaVelocidadFondo / 5;
        offSets[1] += delta * maximaVelocidadFondo / 4;
        offSets[2] += delta * maximaVelocidadFondo / 8;

        for (int capa = 0; capa < offSets.length; capa++){
            if (offSets[capa] > HEIGHT){
                offSets[capa] = 0;
            }
            batch.draw(fondos[capa], 0,-offSets[capa], WIDHT, HEIGHT);
            batch.draw(fondos[capa], 0,-offSets[capa]+HEIGHT, WIDHT, HEIGHT);
        }
    }

    private void disparos(float delta){

        //Disparos
        //Crear nuevos
        //Jugador
        if (naveJugador.puedeDisparar()){
            Disparo[] disparos = naveJugador.disparar();
            for (Disparo disparo: disparos){
                disparosJugador.add(disparo);
            }
        }
        //Malo

        tiempoUltimoDisparoEnemigo += delta;

        for(int i = 0; i< navesMalos.length; i++){
            if (navesMalos[i] != null && navesMalos[i].puedeDisparar()){

                if ((tiempoUltimoDisparoEnemigo < tiempoEntreDisparosEnemigos) && !dispararonPares && i%2==0){
                    Disparo[] disparos = navesMalos[i].disparar();
                    for (Disparo disparo: disparos){
                        disparosMalos[i].add(disparo);
                    }
                }
                if ((tiempoUltimoDisparoEnemigo < tiempoEntreDisparosEnemigos) && dispararonPares && i%2!=0){
                    Disparo[] disparos = navesMalos[i].disparar();
                    for (Disparo disparo: disparos){
                        disparosMalos[i].add(disparo);
                    }
                }
            }
        }

        if (tiempoUltimoDisparoEnemigo > tiempoEntreDisparosEnemigos){
            dispararonPares = !dispararonPares;
            tiempoUltimoDisparoEnemigo -= tiempoEntreDisparosEnemigos;
        }

        //Dibujar disparos
        //Borrar disparos viejos
        ListIterator<Disparo> iterator = disparosJugador.listIterator();
        while(iterator.hasNext()){
            Disparo disparo = iterator.next();
            disparo.draw(batch);
            disparo.yPosicion += disparo.velocidad*delta;
            if (disparo.yPosicion > HEIGHT){
                iterator.remove();
            }
        }

        for(int i = 0; i< disparosMalos.length; i++){
            iterator = disparosMalos[i].listIterator();
            while(iterator.hasNext()){
                Disparo disparo = iterator.next();
                disparo.draw(batch);
                disparo.yPosicion -= disparo.velocidad*delta;
                if (disparo.yPosicion+disparo.height<0){
                    iterator.remove();
                }
            }
        }

    }

    private void moverMalos(float delta){
        float limiteIzq, limiteDer, limiteArriba, limiteAbajo;
        for(int i = 0; i< navesMalos.length; i++){
            if (navesMalos[i] != null){
                limiteIzq = -navesMalos[i].thisBox.x;
                limiteAbajo = HEIGHT/2- navesMalos[i].thisBox.y;
                limiteDer = WIDHT - navesMalos[i].thisBox.x - navesMalos[i].thisBox.width;
                limiteArriba = HEIGHT - navesMalos[i].thisBox.y - navesMalos[i].thisBox.height;

                float xMovimiento = navesMalos[i].getDireccion().x * navesMalos[i].velocidadMovimiento * delta;
                float yMovimiento = navesMalos[i].getDireccion().y * navesMalos[i].velocidadMovimiento * delta;

                if (xMovimiento > 0) xMovimiento = Math.min(xMovimiento, limiteDer);
                else xMovimiento = Math.max(xMovimiento, limiteIzq);

                if (yMovimiento > 0) yMovimiento = Math.min(yMovimiento, limiteArriba);
                else yMovimiento = Math.max(yMovimiento, limiteAbajo);

                navesMalos[i].mover(xMovimiento,yMovimiento);
            }
        }

    }

    private void detectarColisiones(){
        if (naveJugador != null) {
            //Para cada disparo del jugador, chequear si intersecta nave enemiga
            ListIterator<Disparo> iterator = disparosJugador.listIterator();
            boolean borre;
            while (iterator.hasNext()) {
                borre = false;
                Disparo disparo = iterator.next();
                for (int i = 0; i < navesMalos.length; i++) {
                    if (!borre && navesMalos[i] != null && navesMalos[i].intersecta(disparo.getBox())) {
                        vidaEnemigos--;
                        explosiones.add(new ExplosionMalo(new Rectangle(disparo.xPosicion ,disparo.yPosicion,8,8)));

                        //Acierto
                        if (navesMalos[i].acierto(disparo)) {
                            explosiones.add(new ExplosionMalo(new Rectangle(navesMalos[i].thisBox)));
                            navesMalos[i] = null;
                            enemigosVivos--;
                            puntaje += 100*nivelActual;
                        }
                        iterator.remove();
                        borre = true;
                        break;
                    }
                }

            }

            //Para cada disparo del malo2, chequear si intersecta nave jugador
            for (int i = 0; i < disparosMalos.length; i++) {
                iterator = disparosMalos[i].listIterator();
                while (iterator.hasNext()) {
                    Disparo disparo = iterator.next();
                    if (naveJugador != null && naveJugador.intersecta(disparo.getBox())) {
                        //Acierto
                        if (naveJugador.acierto(disparo)) {
                            explosiones.add(new ExplosionJugador(new Rectangle(naveJugador.thisBox)));
                            sonidoExplosion.play();
                        }
                        iterator.remove();
                    }
                }
            }

        }
    }

    private void explosiones(float delta){
        ListIterator<Explosion> explosionListIterator = explosiones.listIterator();

        while (explosionListIterator.hasNext()){
            Explosion explosion = explosionListIterator.next();
            explosion.update(delta);
            if (explosion.termino()){
                explosionListIterator.remove();
            }
            else{
                explosion.draw(batch);
            }
        }

    }

    private void nivel1(){
        TextureRegion texturaMalo = textureAtlas.findRegion("malo2");
        TextureRegion texturaDisparo = textureAtlas.findRegion("disparoMalo2");
        texturaDisparo.flip(false, true);

        //En esta sección se editan los parámetros del nivel
        int cantidadEnemigos = 5;
        float velocidadMovimiento = 40;
        float width = 8;
        float heigh = 8;
        int puntosVida = 3;
        float disparoVelocidad = 50;
        float tiempoEntreDisparos = 3f;
        int cantidadDisparos = 1;

        estadoActual = estadoNivel1;
        cargarJugador();
        nivelActual = 1;

        cargarMalos(cantidadEnemigos,velocidadMovimiento,width,heigh,puntosVida,disparoVelocidad,tiempoEntreDisparos,cantidadDisparos,texturaMalo,texturaDisparo);

    }

    private void nivel2(){
        TextureRegion texturaMalo = textureAtlas.findRegion("malo1");
        TextureRegion texturaDisparo = textureAtlas.findRegion("disparoMalo1");
        texturaDisparo.flip(false, true);

        //En esta sección se editan los parámetros del nivel
        int cantidadEnemigos = 8;
        float velocidadMovimiento = 40;
        float width = 7;
        float heigh = 7;
        int puntosVida = 5;
        float disparoVelocidad = 60;
        float tiempoEntreDisparos = 4f;
        int cantidadDisparos = 2;

        nivelActual = 2;
        fondos[0] = textureAtlas.findRegion("fondo0_lvl2");
        estadoActual = estadoNivel2;

        cargarMalos(cantidadEnemigos,velocidadMovimiento,width,heigh,puntosVida,disparoVelocidad,tiempoEntreDisparos,cantidadDisparos,texturaMalo,texturaDisparo);

    }

    private void nivel3(){
        TextureRegion texturaMalo = textureAtlas.findRegion("malo3");
        TextureRegion texturaDisparo = textureAtlas.findRegion("disparoMalo2");
        texturaDisparo.flip(false, true);

        //En esta sección se editan los parámetros del nivel
        int cantidadEnemigos = 1;
        float velocidadMovimiento = 20;
        float width = 20;
        float heigh = 20;
        int puntosVida = 16;
        float disparoVelocidad = 100;
        float tiempoEntreDisparos = 1.5f;
        int cantidadDisparos = 1;

        nivelActual = 3;
        fondos[0] = textureAtlas.findRegion("fondo0_lvl3");
        estadoActual = estadoNivel3;

        cargarMalos(cantidadEnemigos,velocidadMovimiento,width,heigh,puntosVida,disparoVelocidad,tiempoEntreDisparos,cantidadDisparos,texturaMalo,texturaDisparo);

    }

    private void cargarMalos(int cantidadEnemigos, float velocidadMovimiento, float width, float heigh, int puntosVida, float disparoVelocidad, float tiempoEntreDisparos, int cantidadDisparos, TextureRegion texturaMalo, TextureRegion texturaDisparo) {
        dispararonPares = false;
        tiempoUltimoDisparoEnemigo = 0;
        tiempoEntreDisparosEnemigos = tiempoEntreDisparos/2;

        enemigosVivos = 0;

        navesMalos = new NaveMalo[cantidadEnemigos];
        for(int i = 0; i< navesMalos.length; i++){
            navesMalos[i] = new NaveMalo(velocidadMovimiento, GalagaJuego.random.nextFloat()*4500/WIDHT,HEIGHT*5/6,width,heigh, texturaMalo, puntosVida, texturaDisparo, 1, 2, disparoVelocidad, tiempoEntreDisparos, cantidadDisparos);
            enemigosVivos++;
        }
        disparosMalos = new LinkedList[navesMalos.length];
        for(int i = 0; i< disparosMalos.length; i++){
            disparosMalos[i] = new LinkedList<>();
        }
        vidaEnemigos = puntosVida*cantidadEnemigos;
    }

    private void cargarJugador(){
        TextureRegion naveJugadorTextura = textureAtlas.findRegion("jugador");
        TextureRegion disparoJugadorTextura = textureAtlas.findRegion("disparoJugador");

        naveJugador = new NaveJugador(75, WIDHT/2,HEIGHT/6,10,10, naveJugadorTextura, 5, disparoJugadorTextura, 0.4f, 4, 80, 0.8f);

        disparosJugador = new LinkedList<>();
    }

    @Override
    public void resize(int width, int height) {
        viewp.update(width, height, true);
        batch.setProjectionMatrix(camara.combined);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
