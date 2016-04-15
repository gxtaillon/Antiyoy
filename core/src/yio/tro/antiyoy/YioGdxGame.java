package yio.tro.antiyoy;

import com.badlogic.gdx.*;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import yio.tro.antiyoy.factor_yio.FactorYio;

import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Random;
import java.util.StringTokenizer;

public class YioGdxGame extends ApplicationAdapter implements InputProcessor {
    SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    int w, h;
    MenuControllerLighty menuControllerLighty;
    private MenuViewLighty menuViewLighty;
    public static BitmapFont buttonFont, gameFont, listFont, cityFont;
    public static final String FONT_CHARACTERS = "йцукенгшщзхъёфывапролджэячсмитьбюЙЦУКЕНГШЩЗХЪФЫВАПРОЛДЖЭЯЧСМИТЬБЮіІїЇєЄabcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789][_!$%#@|\\/?-+=()*&.;:,{}\"´`'<^>";
    public static int FONT_SIZE;
    public static final int INDEX_OF_LAST_LEVEL = 70; // with tutorial
    public static final boolean CHECKING_BALANCE_MODE = false; // to measure balance
    public static boolean SOUND = true;
    public static int interface_type = 0;
    public static boolean ask_to_end_turn = false;
    public static final int INTERFACE_SIMPLE = 0;
    public static final int INTERFACE_COMPLICATED = 1;
    public static boolean autosave;
    TextureRegion mainBackground, infoBackground, settingsBackground, pauseBackground;
    TextureRegion currentBackground, lastBackground, splatTexture;
    public static float screenRatio;
    private GameSettings gameSettings;
    public GameController gameController;
    public GameView gameView;
    boolean gamePaused, readyToUnPause;
    private long timeToUnPause;
    private int frameSkipCount;
    private FrameBuffer frameBuffer, screenshotBuffer;
    private FactorYio transitionFactor, splatTransparencyFactor;
    private ArrayList<Splat> splats;
    private long timeToSpawnNextSplat;
    private float splatSize;
    private int currentSplatIndex;
    public static final Random random = new Random();
    private long lastTimeButtonPressed;
    private boolean alreadyShownErrorMessageOnce, showFpsInfo;
    private int fps, currentFrameCount;
    long timeToUpdateFpsInfo;
    private int currentBackgroundIndex;
    long timeWhenPauseStarted, timeForFireworkExplosion, timeToHideSplats;
    int currentBubbleIndex, selectedLevelIndex, splashCount;
    float defaultBubbleRadius, pressX, pressY, animX, animY, animRadius;
    double bubbleGravity;
    boolean ignoreNextTimeCorrection;
    boolean loadedResources;
    boolean ignoreDrag;
    boolean needToHideSplats;
    boolean simpleTransitionAnimation, useMenuMasks;
    TextureRegion splash;
    ArrayList<Float> debugValues;
    ArrayList<Integer> backButtonIds;
    static boolean screenVerySmall;
    boolean debugFactorModel;
    int balanceIndicator[];


    @Override
    public void create() {
        loadedResources = false;
        splashCount = 0;
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        splash = GameView.loadTextureRegionByName("splash.png", true);
        w = Gdx.graphics.getWidth();
        h = Gdx.graphics.getHeight();
        pressX = 0.5f * w;
        pressY = 0.5f * h;
        screenRatio = (float) w / (float) h;
        frameBuffer = new FrameBuffer(Pixmap.Format.RGB565, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
        screenshotBuffer = new FrameBuffer(Pixmap.Format.RGB565, w, h, true);
        balanceIndicator = new int[GameController.colorNumber];
        initDebugValues();
        backButtonIds = new ArrayList<Integer>();
        useMenuMasks = true;
    }


    private void initDebugValues() {
        debugFactorModel = false;

        debugValues = new ArrayList<Float>();
        if (debugFactorModel) {
            FactorYio factorYio = new FactorYio();
            factorYio.setValues(0, 0);
            factorYio.beginSpawning(4, 1);
            int c = 100;
            while (factorYio.needsToMove() && c > 0) {
                debugValues.add(Float.valueOf(factorYio.get()));
                factorYio.move();
                c--;
            }
        }
    }


    private void loadResourcesAndInitEverything() {
        loadedResources = true;
        gameSettings = new GameSettings(this);
        screenVerySmall = Gdx.graphics.getDensity() < 1.2;
        mainBackground = GameView.loadTextureRegionByName("main_menu_background.png", true);
        infoBackground = GameView.loadTextureRegionByName("info_background.png", true);
        settingsBackground = GameView.loadTextureRegionByName("settings_background.png", true);
        pauseBackground = GameView.loadTextureRegionByName("pause_background.png", true);
        splatTexture = GameView.loadTextureRegionByName("splat.png", true);
        SoundControllerYio.loadSounds();
        transitionFactor = new FactorYio();
        splatTransparencyFactor = new FactorYio();
        initSplats();

        initFonts();
        gamePaused = true;
        alreadyShownErrorMessageOnce = false;
        showFpsInfo = false;
        fps = 0;
        timeToUpdateFpsInfo = System.currentTimeMillis() + 1000;
//        decorations = new ArrayList<BackgroundMenuDecoration>();
//        initDecorations();

        Preferences preferences = Gdx.app.getPreferences("main");
        selectedLevelIndex = preferences.getInteger("progress", 1); // 1 - default value;
        if (selectedLevelIndex > INDEX_OF_LAST_LEVEL) { // completed campaign
            selectedLevelIndex = INDEX_OF_LAST_LEVEL;
        }
        menuControllerLighty = new MenuControllerLighty(this);
        menuViewLighty = new MenuViewLighty(this);
        gameController = new GameController(this); // must be called after menu controller is created. because of languages manager and other stuff
        gameView = new GameView(this);
        gameView.factorModel.beginDestroying(1, 1);
        currentBackgroundIndex = -1;
        currentBackground = gameView.blackPixel; // call this after game view is created
        beginBackgroundChange(0, true, false);
        defaultBubbleRadius = 0.02f * w;
        bubbleGravity = 0.00025 * w;
        revealSplats();
        Gdx.input.setInputProcessor(this);
        Gdx.gl.glClearColor(0, 0, 0, 1);

        loadSettings();
    }


    private void initFonts() {
        long time1 = System.currentTimeMillis();
        FileHandle fontFile = Gdx.files.internal("font.otf");
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(fontFile);
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        FONT_SIZE = (int) (0.041 * Gdx.graphics.getHeight());

        parameter.size = FONT_SIZE;
        parameter.characters = FONT_CHARACTERS;
        parameter.flip = true;
        buttonFont = generator.generateFont(parameter);

        parameter.size = (int) (1.5f * FONT_SIZE);
        parameter.flip = true;
        listFont = generator.generateFont(parameter);
        listFont.setColor(Color.BLACK);

        parameter.size = FONT_SIZE;
        parameter.flip = false;
        gameFont = generator.generateFont(parameter);
        gameFont.setColor(Color.BLACK);

        parameter.size = (int)(0.5 * FONT_SIZE);
        parameter.flip = false;
        cityFont = generator.generateFont(parameter);
        cityFont.setColor(Color.WHITE);

        generator.dispose();

        YioGdxGame.say("time to generate fonts: " + (System.currentTimeMillis() - time1));
    }


    private void initSplats() {
        splats = new ArrayList<Splat>();
        splatSize = 0.15f * Gdx.graphics.getWidth();
        ListIterator iterator = splats.listIterator();
        for (int i = 0; i < 100; i++) {
            float sx, sy, sr;
            sx = random.nextFloat() * w;
            sr = 0.03f * random.nextFloat() * h + 0.02f * h;
            sy = random.nextFloat() * h;
            float dx, dy;
            dx = 0.02f * splatSize * random.nextFloat() - 0.01f * splatSize;
            dy = 0.01f * splatSize;
            Splat splat = new Splat(null, sx, sy);
            if (random.nextDouble() < 0.6 || distance(w / 2, h / 2, sx, sy) > 0.6f * w) splat.y = 2 * h; // hide splat
            splat.setSpeed(dx, dy);
            splat.setRadius(sr);
            iterator.add(splat);
        }
    }


    public void saveSettings() {
        Preferences prefs = Gdx.app.getPreferences("settings");
        prefs.putInteger("sound", menuControllerLighty.sliders.get(4).getCurrentRunnerIndex());
        prefs.putInteger("skin", menuControllerLighty.sliders.get(5).getCurrentRunnerIndex());
        prefs.putInteger("interface", menuControllerLighty.sliders.get(6).getCurrentRunnerIndex()); // slot number
        prefs.putInteger("autosave", menuControllerLighty.sliders.get(7).getCurrentRunnerIndex());
        prefs.putInteger("ask_to_end_turn", menuControllerLighty.sliders.get(8).getCurrentRunnerIndex());
        prefs.putInteger("anim_style", menuControllerLighty.sliders.get(9).getCurrentRunnerIndex());
        prefs.putInteger("city_names", menuControllerLighty.sliders.get(10).getCurrentRunnerIndex());
        prefs.flush();
    }


    public void loadSettings() {
        Preferences prefs = Gdx.app.getPreferences("settings");

        // sound
        int soundIndex = prefs.getInteger("sound");
        if (soundIndex == 0) SOUND = false;
        else SOUND = true;
        menuControllerLighty.sliders.get(4).setRunnerValue(soundIndex);

        // skin
        int skin = prefs.getInteger("skin");
        gameView.loadSkin(skin);
        float slSkinValue = (float) skin / 2f;
        menuControllerLighty.sliders.get(5).setRunnerValue(slSkinValue);

        // interface
        interface_type = prefs.getInteger("interface");
        menuControllerLighty.sliders.get(6).setRunnerValue(interface_type);

        // autosave
        int AS = prefs.getInteger("autosave");
        autosave = false;
        if (AS == 1) autosave = true;
        menuControllerLighty.sliders.get(7).setRunnerValue(AS);

        // animations style
        menuControllerLighty.anim_style = prefs.getInteger("anim_style", 2);
        menuControllerLighty.sliders.get(9).setRunnerValue(MenuControllerLighty.anim_style / 3f);
        menuControllerLighty.applyAnimStyle();

        // ask to end turn
        int ATET = prefs.getInteger("ask_to_end_turn");
        ask_to_end_turn = (ATET == 1);
        menuControllerLighty.sliders.get(8).setRunnerValue(ATET);

        // show city names
        int cityNames = prefs.getInteger("city_names", 0);
        gameController.setShowCityNames(cityNames);
        menuControllerLighty.sliders.get(10).setRunnerValue(cityNames);

        for (int i = 4; i <= 10; i++) {
            menuControllerLighty.sliders.get(i).updateValueString();
        }
    }


    public void setGamePaused(boolean gamePaused) {
        if (gamePaused && !this.gamePaused) { // actions when paused
            this.gamePaused = true;
            timeWhenPauseStarted = System.currentTimeMillis();
            gameController.deselectAll();
            revealSplats();
            gameFont.setColor(Color.BLACK);
        } else if (!gamePaused && this.gamePaused) { // actions when unpaused
            unPauseAfterSomeTime();
            beginBackgroundChange(4, true, true);
            hideSplats();
            gameFont.setColor(Color.WHITE);
        }
    }


    public void beginBackgroundChange(int index, boolean updateAnimPos, boolean simpleTransition) {
        if (currentBackgroundIndex == index && index == 4) return;
        this.simpleTransitionAnimation = simpleTransition;
        currentBackgroundIndex = index;
        lastBackground = currentBackground;
        if (updateAnimPos) {
            animX = pressX;
            animY = pressY;
            float r1, r2, r3, r4;
            r1 = (float) distance(animX, animY, 0, 0);
            r2 = (float) distance(animX, animY, w, 0);
            r3 = (float) distance(animX, animY, 0, h);
            r4 = (float) distance(animX, animY, w, h);
            animRadius = r1;
            if (r2 > animRadius) animRadius = r2;
            if (r3 > animRadius) animRadius = r3;
            if (r4 > animRadius) animRadius = r4;
        }
        switch (index) {
            case 0:
                currentBackground = mainBackground;
                break;
            case 1:
                currentBackground = infoBackground;
                break;
            case 2:
                currentBackground = settingsBackground;
                break;
            case 3:
                currentBackground = pauseBackground;
                break;
            case 4:
                currentBackground = gameView.blackPixel;
                break;
        }
        transitionFactor.setValues(0.02, 0.01);
        transitionFactor.beginSpawning(0, 0.8);
    }


    private void timeCorrection(long correction) {
        if (ignoreNextTimeCorrection) {
            ignoreNextTimeCorrection = false;
            return;
        }
        gameController.timeCorrection(correction);
    }


    private void letsIgnoreNextTimeCorrection() {
        ignoreNextTimeCorrection = true;
    }


    private void checkToUseMenuMasks() {
        if (!useMenuMasks) { // check to switch on masks
            if (gameView.factorModel.get() < 1) {
                useMenuMasks = true;
                return;
            }
            ButtonLighty buttonLighty = menuControllerLighty.getButtonById(30);
            if (buttonLighty != null && buttonLighty.isCurrentlyTouched()) {
                useMenuMasks = true;
                return;
            }
        } else { // check to switch off masks
            if (gameView.factorModel.get() == 1 && gameView.factorModel.getDy() == 0) {
                useMenuMasks = false;
                return;
            }
        }
    }


    private void move() {
        if (!loadedResources) return;
//        if (random.nextInt(100) == 0) {
//            say("memory: " + (Gdx.app.getJavaHeap() + Gdx.app.getNativeHeap()));
//        }
        transitionFactor.move();
        splatTransparencyFactor.move();
        gameController.selMoneyFactor.move();
        if (readyToUnPause && System.currentTimeMillis() > timeToUnPause && gameView.coversAllScreen()) {
            gamePaused = false;
            readyToUnPause = false;
            gameController.currentTouchCount = 0;
            timeCorrection(System.currentTimeMillis() - timeWhenPauseStarted);
        }
        if (needToHideSplats && System.currentTimeMillis() > timeToHideSplats) {
            needToHideSplats = false;
        }
        gameView.moveFactors();
        menuControllerLighty.move();
        if (!loadedResources) return; // if exit button was pressed
        checkToUseMenuMasks();
        if (!gamePaused) {
            gameView.moveInsideStuff();
            gameController.move();
            if (gameView.factorModel.get() < 0.95) say("game not paused but game view is not visible");
        }
        if (!gameView.coversAllScreen()) {
            if (System.currentTimeMillis() > timeToSpawnNextSplat) {
                timeToSpawnNextSplat = System.currentTimeMillis() + 300 + random.nextInt(100);
                float sx, sy, sr;
                sx = random.nextFloat() * w;
                sr = 0.03f * random.nextFloat() * h + 0.02f * h;
                sy = -sr;
                int c = 0, size = splats.size();
                Splat splat = null;
                while (c < size) {
                    c++;
                    splat = splats.get(currentSplatIndex);
                    currentSplatIndex++;
                    if (currentSplatIndex >= size) currentSplatIndex = 0;
                    if (!splat.isVisible()) {
                        float dx, dy;
                        dx = 0.02f * splatSize * random.nextFloat() - 0.01f * splatSize;
                        dy = 0.01f * splatSize;
                        splat.set(sx, sy);
                        splat.setSpeed(dx, dy);
                        splat.setRadius(sr);
                        break;
                    }
                }
            }
            for (Splat splat : splats) {
                splat.move();
            }
        }
    }


    private void renderDebugValues() {
//        batch.begin();
//        batch.draw(gameView.grayPixel, 0, 0, w, h);
//        int graphWidth = w;
//        int graphHeight = (int)(0.45 * h);
//        int graphPos = h / 2;
//        float max = maxElement(debugValues);
//        float x, y, s;
//        s = 0.01f * w;
//        batch.draw(gameView.blackPixel, 0, graphPos - s, w, 2 * s);
//        batch.draw(gameView.blackPixel, 0, graphPos + graphHeight - s, w, 2 * s);
//        batch.draw(gameView.blackPixel, 0, graphPos - graphHeight - s, w, 2 * s);
//        for (int i=0; i<debugValues.size(); i++) {
//            x = ((float)i / (float)debugValues.size()) * graphWidth;
//            y = graphPos + ((float)graphHeight / max) * debugValues.get(i);
//            batch.draw(gameView.redUnit, x - s, y - s, 2 * s, 2 * s);
//        }
//        batch.end();
    }


    private void drawBackground(TextureRegion textureRegion) {
        batch.begin();
        batch.draw(textureRegion, 0, 0, w, h);
        batch.end();
    }


    private void renderMenuLayersWhenNothingIsMoving() { // when transitionFactor.get() == 1
        Color c = batch.getColor();
        batch.setColor(c.r, c.g, c.b, 1);
        batch.begin();
        batch.draw(currentBackground, 0, 0, w, h);
        renderSplats(c);
        batch.end();
    }

//    private void renderMenuLayersWhenBackAnimation() { // when backAnimation == true
//        Color c = batch.getColor();
//        batch.setColor(c.r, c.g, c.b, 1);
//        drawBackground(currentBackground);
//
//        menuViewLighty.render(true, false);
//
//        if (simpleTransitionAnimation) {
//            float f = (1 - transitionFactor.get());
//            batch.setColor(c.r, c.g, c.b, f);
//            drawBackground(lastBackground);
//        } else {
//            float f = (1 - transitionFactor.get());
//            maskingBegin();
//            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
//            shapeRenderer.circle(animX, animY, f * animRadius, 32);
//            shapeRenderer.end();
//            maskingContinue();
//            drawBackground(lastBackground);
//            maskingEnd();
//        }
//
//        batch.begin();
//        renderSplats(c);
//        batch.end();
//    }


    private void renderMenuLayersWhenUsualAnimation() {
        Color c = batch.getColor();
        batch.setColor(c.r, c.g, c.b, 1);
        drawBackground(lastBackground);

        if (simpleTransitionAnimation) {
            float f = (0 + transitionFactor.get());
            batch.setColor(c.r, c.g, c.b, f);
            drawBackground(currentBackground);
        } else {
            float f = (0 + transitionFactor.get());
            maskingBegin();
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.circle(animX, animY, f * animRadius, 32);
            shapeRenderer.end();
            maskingContinue();
            drawBackground(currentBackground);
            maskingEnd();
        }

        batch.begin();
        renderSplats(c);
        batch.end();

        menuViewLighty.render(false, true);
    }


    private void renderMenuWhenGameViewNotVisible() {
        if (transitionFactor.get() == 1 && !menuControllerLighty.notificationIsDestroying()) {
            renderMenuLayersWhenNothingIsMoving();
            return;
        }

        renderMenuLayersWhenUsualAnimation();
    }


    private void renderInternals() {
        currentFrameCount++;
        if (showFpsInfo && System.currentTimeMillis() > timeToUpdateFpsInfo) {
            timeToUpdateFpsInfo = System.currentTimeMillis() + 1000;
            fps = currentFrameCount;
            currentFrameCount = 0;
        }
        if (debugFactorModel) {
            renderDebugValues();
            return;
        }
        if (!gameView.coversAllScreen()) {
            renderMenuWhenGameViewNotVisible();
        }
        menuViewLighty.renderScroller();
        gameView.render();
        if (gamePaused) {
            menuViewLighty.render(true, false);
        } else {
            menuViewLighty.render(true, true);
        }
        if (showFpsInfo) {
            batch.begin();
            gameFont.draw(batch, "" + fps, 0.2f * w, Gdx.graphics.getHeight() - 10);
            batch.end();
        }
    }


    private void renderSplats(Color c) {
        if (splatTransparencyFactor.get() == 1) {
            batch.setColor(c.r, c.g, c.b, splatTransparencyFactor.get());
            for (Splat splat : splats) {
                batch.draw(splatTexture, splat.x - splat.r / 2, splat.y - splat.r / 2, splat.r, splat.r);
            }
        } else if (splatTransparencyFactor.get() > 0) {
            batch.setColor(c.r, c.g, c.b, splatTransparencyFactor.get());
            float a, d;
            for (Splat splat : splats) {
                a = (float) angle(w / 2, h / 2, splat.x, splat.y);
                d = (float) distance(w / 2, h / 2, splat.x, splat.y);
                d = 0.5f * h - d;
                d *= 1 - splatTransparencyFactor.get();
                batch.draw(splatTexture, splat.x - splat.r / 2 + d * (float) Math.cos(a), splat.y - splat.r / 2 + d * (float) Math.sin(a), splat.r, splat.r);
            }
        }
    }


    public static void maskingBegin() {
        Gdx.gl.glClearDepthf(1f);
        Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT);
        Gdx.gl.glDepthFunc(GL20.GL_LESS);
        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
        Gdx.gl.glDepthMask(true);
        Gdx.gl.glColorMask(false, false, false, false);
    }


    public static void maskingContinue() {
        Gdx.gl.glColorMask(true, true, true, true);
        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
        Gdx.gl.glDepthFunc(GL20.GL_EQUAL);
    }


    public static void maskingEnd() {
        Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
    }


    @Override
    public void render() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (!loadedResources) {
            batch.begin();
            batch.draw(splash, 0, 0, w, h);
            batch.end();
            if (splashCount == 2) loadResourcesAndInitEverything();
            splashCount++;
            return;
        }

        try {
            move();
        } catch (Exception exception) {
            if (!alreadyShownErrorMessageOnce) {
                exception.printStackTrace();
                alreadyShownErrorMessageOnce = true;
                menuControllerLighty.createExceptionReport(exception);
            }
        }

        if (gamePaused) {
            renderInternals();
        } else {
            if (Gdx.graphics.getDeltaTime() < 0.025 || frameSkipCount >= 2) {
                frameSkipCount = 0;
                frameBuffer.begin();
                renderInternals();
                frameBuffer.end();
            } else {
                frameSkipCount++;
            }
            batch.begin();
            batch.draw(frameBuffer.getColorBufferTexture(), 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false, true);
            batch.end();
        }
    }


    TextureRegion takeScreenshot() {
        screenshotBuffer.begin();
        renderInternals();
        Texture texture = screenshotBuffer.getColorBufferTexture();
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        TextureRegion screenshot = new TextureRegion(texture);
        screenshotBuffer.end();
        screenshot.flip(false, true);
        return screenshot;
    }


    private void unPauseAfterSomeTime() {
        readyToUnPause = true;
        timeToUnPause = System.currentTimeMillis() + 450; // время анимации - около 420мс
    }


    public void setAnimToPlayButtonSpecial() {
        ButtonLighty buttonLighty = menuControllerLighty.getButtonById(3);
        animX = buttonLighty.cx;
        animY = buttonLighty.cy;
        transitionFactor.setValues(0.15, 0);
    }


    public void setAnimToResumeButtonSpecial() {
        animX = w;
        animY = h;
        animRadius = (float) distance(0, 0, w, h);
    }


    public void setAnimToStartButtonSpecial() {
        animX = 0.5f * w;
        animY = 0.65f * h;
        animRadius = animY;
    }


    public static boolean isScreenVerySmall() {
        return screenVerySmall;
    }


    public static boolean isScreenVeryWide() {
        float ratio = (float) Gdx.graphics.getHeight() / Gdx.graphics.getWidth();
        return ratio < 1.51;
    }


    public void forceBackgroundChange() {
        transitionFactor.setValues(1, 0);
        simpleTransitionAnimation = true;
    }


    public static void say(String text) {
        System.out.println(text);
    }


    public void restartGame() {
        if (gameController.campaignMode) {
            gameController.loadCampaignLevel(gameController.currentLevelIndex);
            return;
        }
        gameController.restartGame();
    }


    public void startInEditorMode() {
        gameController.editorMode = true;
        if (GameController.colorNumber == 0) { // default
            gameController.setLevelSize(GameController.SIZE_BIG);
            gameController.setPlayersNumber(1);
            GameController.setColorNumber(5);
            startGame(false, false);
            gameController.createFieldMatrix();
            gameController.clearField();
        } else {
            startGame(false, false);
        }
    }


    public void startGame(boolean generateMap, boolean readParametersFromSliders) {
        startGame(random.nextInt(), generateMap, readParametersFromSliders);
    }


    public void startGame(int index, boolean generateMap, boolean readParametersFromSliders) {
//        if (selectedLevelIndex > gameController.progress) return;
        if (selectedLevelIndex < 0 || selectedLevelIndex > INDEX_OF_LAST_LEVEL) return;
        gameController.prepareForNewGame(index, generateMap, readParametersFromSliders);
        gameView.beginSpawnProcess();
        menuControllerLighty.createGameOverlay();
//        menuControllerLighty.scrollerYio.factorModel.setValues(0, 0);
        setGamePaused(false);
        letsIgnoreNextTimeCorrection();
    }


    void increaseLevelSelection() {
        menuControllerLighty.scrollerYio.increaseSelection();
        setSelectedLevelIndex(selectedLevelIndex + 1);
    }


    static double angle(double x1, double y1, double x2, double y2) {
        if (x1 == x2) {
            if (y2 > y1) return 0.5 * Math.PI;
            if (y2 < y1) return 1.5 * Math.PI;
            return 0;
        }
        if (x2 >= x1) return Math.atan((y2 - y1) / (x2 - x1));
        else return Math.PI + Math.atan((y2 - y1) / (x2 - x1));
    }


    static double distance(double x1, double y1, double x2, double y2) {
        return Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
    }


    public static ArrayList<String> decodeStringToArrayList(String string, String delimiters) {
        ArrayList<String> res = new ArrayList<String>();
        StringTokenizer tokenizer = new StringTokenizer(string, delimiters);
        while (tokenizer.hasMoreTokens()) {
            res.add(tokenizer.nextToken());
        }
        return res;
    }


    public int getSelectedLevelIndex() {
        return selectedLevelIndex;
    }


    public void setSelectedLevelIndex(int selectedLevelIndex) {
        if (selectedLevelIndex >= 0 && selectedLevelIndex <= INDEX_OF_LAST_LEVEL)
            this.selectedLevelIndex = selectedLevelIndex;
    }


    private void pressButtonIfVisible(int id) {
        ButtonLighty button = menuControllerLighty.getButtonById(id);
        if (button != null && button.isVisible() && button.factorModel.get() == 1) button.press();
    }


    void registerBackButtonId(int id) {
        for (Integer integer : backButtonIds) {
            if (integer.intValue() == id) return;
        }
        backButtonIds.add(Integer.valueOf(id));
    }


    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.BACK || keycode == Input.Keys.ESCAPE) {
            if (!gamePaused) {
                ButtonLighty pauseButton = menuControllerLighty.getButtonById(30);
                if (pauseButton != null && pauseButton.isVisible()) pauseButton.press();
                else menuControllerLighty.getButtonById(140).press();
            } else {
                // back buttons
                for (Integer integer : backButtonIds) {
                    pressButtonIfVisible(integer.intValue());
                }
            }
        }
        if (keycode == Input.Keys.Q) {
            if (!gamePaused) {
                menuControllerLighty.getButtonById(32).press(); // debug
            }
        }
        if (keycode == Input.Keys.SPACE) {
            if (!gamePaused) {
                menuControllerLighty.getButtonById(31).press();
            }
        }
        return false;
    }


    @Override
    public boolean keyUp(int keycode) {
        return false;
    }


    @Override
    public boolean keyTyped(char character) {
        return false;
    }


    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        ignoreDrag = true;
        pressX = screenX;
        pressY = h - screenY;
        try {
            if (!gameView.isInMotion() && transitionFactor.get() > 0.99 && menuControllerLighty.touchDown(screenX, Gdx.graphics.getHeight() - screenY, pointer, button)) {
                lastTimeButtonPressed = System.currentTimeMillis();
                return false;
            } else {
                ignoreDrag = false;
            }
            if (!gamePaused) gameController.touchDown(screenX, Gdx.graphics.getHeight() - screenY, pointer, button);
        } catch (Exception exception) {
            if (!alreadyShownErrorMessageOnce) {
                exception.printStackTrace();
                alreadyShownErrorMessageOnce = true;
                menuControllerLighty.createExceptionReport(exception);
            }
        }
        return false;
    }


    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        try {
            menuControllerLighty.touchUp(screenX, Gdx.graphics.getHeight() - screenY, pointer, button);
            if (!gamePaused && gameView.coversAllScreen() && System.currentTimeMillis() > lastTimeButtonPressed + 300)
                gameController.touchUp(screenX, Gdx.graphics.getHeight() - screenY, pointer, button);
        } catch (Exception exception) {
            if (!alreadyShownErrorMessageOnce) {
                exception.printStackTrace();
                alreadyShownErrorMessageOnce = true;
                menuControllerLighty.createExceptionReport(exception);
            }
        }
        return false;
    }


    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        menuControllerLighty.touchDragged(screenX, Gdx.graphics.getHeight() - screenY, pointer);
        if (!ignoreDrag && !gamePaused && gameView.coversAllScreen())
            gameController.touchDragged(screenX, Gdx.graphics.getHeight() - screenY, pointer);
        return false;
    }


    public int gamesPlayed() {
        int s = 0;
        for (int i = 0; i < balanceIndicator.length; i++) {
            s += balanceIndicator[i];
        }
        return s;
    }


    private String getBalanceIndicatorAsString(int array[]) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("[");
        for (int i = 0; i < GameController.colorNumber; i++) {
            stringBuffer.append(" " + array[i]);
        }
        stringBuffer.append(" ]");
        return stringBuffer.toString();
    }


    public String getBalanceIndicatorString() {
        double D = 0;
        int max = balanceIndicator[0], min = balanceIndicator[0];
        for (int i = 0; i < GameController.colorNumber; i++) {
            if (balanceIndicator[i] > max) max = balanceIndicator[i];
            if (balanceIndicator[i] < min) min = balanceIndicator[i];
        }
        if (max > 0) {
            D = 1d - (double) min / (double) max;
        }
        String dStr = Double.toString(D);
        if (dStr.length() > 4) dStr = dStr.substring(0, 4);

        return getBalanceIndicatorAsString(balanceIndicator) + " = " + dStr;
    }


    private void hideSplats() {
        needToHideSplats = true;
        timeToHideSplats = System.currentTimeMillis() + 350;
        splatTransparencyFactor.setDy(0);
        splatTransparencyFactor.beginDestroying(0, 1);
    }


    private void revealSplats() {
        needToHideSplats = false;
        splatTransparencyFactor.setDy(0);
        splatTransparencyFactor.beginSpawning(0, 0.5);
    }


    static float maxElement(ArrayList<Float> list) {
        if (list.size() == 0) return 0;
        float max = list.get(0);
        for (int i = 1; i < list.size(); i++) {
            if (list.get(i) > max) max = list.get(i);
        }
        return max;
    }


    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }


    @Override
    public boolean scrolled(int amount) {
        if (gameView.factorModel.get() > 0.1) gameController.scrolled(amount);
        return true;
    }


    public void close() {
        if (true) return;
        loadedResources = false;
        gameController.close();
        menuControllerLighty.close();

        gameController = null;
        menuControllerLighty = null;
        menuViewLighty = null;
        gameView = null;
    }
}
