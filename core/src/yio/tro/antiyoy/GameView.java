package yio.tro.antiyoy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import yio.tro.antiyoy.factor_yio.FactorYio;

import java.util.ArrayList;

/**
 * Created by ivan on 05.08.14.
 */
public class GameView {

    private final YioGdxGame yioGdxGame;
    private final GameController gameController;
    private TextureRegion backgroundRegion;
    public final FactorYio factorModel;
    private final FrameBuffer frameBuffer;
    SpriteBatch batchMovable, batchSolid, batchCache;
    private ShapeRenderer shapeRenderer;
    float cx, cy, dw, dh, borderLineThickness;
    TextureRegion blackCircleTexture, redRectTexture, exclamationMarkTexture, forefingerTexture;
    TextureRegion animationTextureRegion, blackBorderTexture;
    TextureRegion hexGreen, hexRed, hexBlue, hexYellow, hexCyan;
    float linkLineThickness, hexViewSize, cacheFrameX1, cacheFrameY1, cacheFrameX2, cacheFrameY2, hexShadowSize;
    TextureRegion blackPixel, grayPixel, selectionPixel, shadowHexTexture, gradientShadow, transCircle1, transCircle2, selUnitShadow, currentObjectTexture;
    Storage3xTexture manTextures[], palmTexture, houseTexture, towerTexture, graveTexture, pineTexture;
    int segments, w, h, currentZoomQuality;
    OrthographicCamera orthoCam, cacheCam;
    TextureRegion cacheLevelTextures[], sideShadow, moveZonePixel, responseAnimHexTexture, selectionBorder, defenseIcon;
    FrameBuffer frameBufferList[];
    SimpleRectangle screenRectangle;
    PointYio pos;
    double camBlurSpeed, zoomLevelOne, zoomLevelTwo;


    public GameView(YioGdxGame yioGdxGame) { //must be called after creation of GameController and MenuView
        this.yioGdxGame = yioGdxGame;
        gameController = yioGdxGame.gameController;
        w = Gdx.graphics.getWidth();
        h = Gdx.graphics.getHeight();
        factorModel = new FactorYio();
        frameBuffer = new FrameBuffer(Pixmap.Format.RGB565, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
        batchMovable = new SpriteBatch();
        batchSolid = yioGdxGame.batch;
        batchCache = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        createOrthoCam();
        cacheCam = new OrthographicCamera(yioGdxGame.w, yioGdxGame.h);
        cacheCam.position.set(orthoCam.viewportWidth / 2f, orthoCam.viewportHeight / 2f, 0);
        cx = yioGdxGame.w / 2;
        cy = yioGdxGame.h / 2;
        zoomLevelOne = 0.8;
        zoomLevelTwo = 1.3;
        borderLineThickness = 0.006f * w;
        linkLineThickness = 0.01f * Gdx.graphics.getWidth();
        segments = Gdx.graphics.getWidth() / 75;
        if (segments < 12) segments = 12;
        if (segments > 24) segments = 24;
        hexViewSize = 1.04f * gameController.hexSize;
        hexShadowSize = 1.00f * hexViewSize;
        frameBufferList = new FrameBuffer[4];
        for (int i = 0; i < frameBufferList.length; i++)
            frameBufferList[i] = new FrameBuffer(Pixmap.Format.RGB565, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
        loadTextures();
        screenRectangle = new SimpleRectangle(0, 0, w, h);
        camBlurSpeed = 0.001 * w;
    }


    void createOrthoCam() {
        orthoCam = new OrthographicCamera(yioGdxGame.w, yioGdxGame.h);
        orthoCam.position.set(orthoCam.viewportWidth / 2f, orthoCam.viewportHeight / 2f, 0);
        updateCam();
    }


    void createLevelCacheTextures() {
        cacheLevelTextures = new TextureRegion[gameController.levelSize];
    }


    private void loadTextures() {
        backgroundRegion = loadTextureRegionByName("game_background.png", true);
        blackCircleTexture = loadTextureRegionByName("black_circle.png", true);
        redRectTexture = loadTextureRegionByName("red_rect.png", false);
        shadowHexTexture = loadTextureRegionByName("shadow_hex.png", true);
        gradientShadow = loadTextureRegionByName("gradient_shadow.png", false);
        blackPixel = loadTextureRegionByName("black_pixel.png", false);
        transCircle1 = loadTextureRegionByName("transition_circle_1.png", false);
        transCircle2 = loadTextureRegionByName("transition_circle_2.png", false);
        loadFieldTextures();
        selUnitShadow = loadTextureRegionByName("sel_shadow.png", true);
        sideShadow = loadTextureRegionByName("money_shadow.png", true);
        moveZonePixel = loadTextureRegionByName("move_zone_pixel.png", false);
        responseAnimHexTexture = loadTextureRegionByName("response_anim_hex.png", false);
        selectionBorder = loadTextureRegionByName("selection_border.png", false);
        exclamationMarkTexture = loadTextureRegionByName("exclamation_mark.png", true);
        forefingerTexture = loadTextureRegionByName("forefinger.png", true);
        defenseIcon = loadTextureRegionByName("defense_icon.png", true);
        blackBorderTexture = loadTextureRegionByName("pixels/black_border.png", true);
        grayPixel = blackPixel;
    }


    void loadSkin(int skin) {
        switch (skin) {
            case 0: // original
                loadOriginalSkin();
                break;
            case 1: // points
                loadPointsSkin();
                break;
            case 2: // grid
                loadGridSkin();
                break;
        }
    }


    private void loadOriginalSkin() {
        hexGreen = loadTextureRegionByName("hex_green.png", false);
        hexRed = loadTextureRegionByName("hex_red.png", false);
        hexBlue = loadTextureRegionByName("hex_blue.png", false);
        hexCyan = loadTextureRegionByName("hex_cyan.png", false);
        hexYellow = loadTextureRegionByName("hex_yellow.png", false);
    }


    private void loadPointsSkin() {
        hexGreen = loadTextureRegionByName("skins/points_hex_green.png", false);
        hexRed = loadTextureRegionByName("skins/points_hex_red.png", false);
        hexBlue = loadTextureRegionByName("skins/points_hex_blue.png", false);
        hexCyan = loadTextureRegionByName("skins/points_hex_cyan.png", false);
        hexYellow = loadTextureRegionByName("skins/points_hex_yellow.png", false);
    }


    private void loadGridSkin() {
        hexGreen = loadTextureRegionByName("skins/hex_green_grid.png", false);
        hexRed = loadTextureRegionByName("skins/hex_red_grid.png", false);
        hexBlue = loadTextureRegionByName("skins/hex_blue_grid.png", false);
        hexCyan = loadTextureRegionByName("skins/hex_cyan_grid.png", false);
        hexYellow = loadTextureRegionByName("skins/hex_yellow_grid.png", false);
    }


    private void loadFieldTextures() {
        AtlasLoader atlasLoader = new AtlasLoader("field_elements/atlas_texture.png", "field_elements/atlas_structure.txt", true);
        selectionPixel = atlasLoader.getTexture("selection_pixel_lowest.png");
        manTextures = new Storage3xTexture[4];
        for (int i = 0; i < 4; i++) {
            manTextures[i] = new Storage3xTexture(atlasLoader, "man" + i + ".png");
        }
        graveTexture = new Storage3xTexture(atlasLoader, "grave.png");
        houseTexture = new Storage3xTexture(atlasLoader, "house.png");
        palmTexture = new Storage3xTexture(atlasLoader, "palm.png");
        pineTexture = new Storage3xTexture(atlasLoader, "pine.png");
        towerTexture = new Storage3xTexture(atlasLoader, "tower.png");
    }


    public static TextureRegion loadTextureRegionByName(String name, boolean antialias) {
        Texture texture = new Texture(Gdx.files.internal(name));
        if (antialias) texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        return new TextureRegion(texture);
    }


    public void updateCacheLevelTextures() {
        if (YioGdxGame.CHECKING_BALANCE_MODE) return;
        gameController.letsUpdateCacheByAnim = false;
        for (int i = 0; i < cacheLevelTextures.length; i++) {
            FrameBuffer cacheLevelFrameBuffer = frameBufferList[i];
            cacheLevelFrameBuffer.begin();
//            Matrix4 matrix4 = new Matrix4();
//            matrix4.setToOrtho2D(0, 0, w / 2, h / 2);
//            batchMovable.setProjectionMatrix(matrix4);
            Gdx.gl.glClearColor(0, 0, 0, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            switch (i) {
                case 0:
                    cacheCam.position.set(0.5f * w, 0.5f * h, 0);
                    setCacheFrame(0, 0, w, h);
                    break;
                case 1:
                    cacheCam.translate(w, 0);
                    setCacheFrame(w, 0, 2 * w, h);
                    break;
                case 2:
                    cacheCam.translate(-w, h);
                    setCacheFrame(0, h, w, 2 * h);
                    break;
                case 3:
                    cacheCam.translate(w, 0);
                    setCacheFrame(w, h, 2 * w, 2 * h);
                    break;
            }
            cacheCam.update();
            batchCache.setProjectionMatrix(cacheCam.combined);
            renderCache(batchCache, true);

            Texture texture = cacheLevelFrameBuffer.getColorBufferTexture();
            texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            cacheLevelTextures[i] = new TextureRegion(texture, w, h);
            cacheLevelTextures[i].flip(false, true);
            cacheLevelFrameBuffer.end();
        }
    }


    void updateCacheNearAnimHexes() {
        if (YioGdxGame.CHECKING_BALANCE_MODE) return;
        float up, right, down, left;
        ArrayList<Hex> ah = gameController.animHexes;
        if (ah.size() == 0) return;
        up = down = ah.get(0).getPos().y;
        left = right = ah.get(0).getPos().x;
        for (int i = 1; i < ah.size(); i++) {
            PointYio tempPos = ah.get(i).getPos();
            if (tempPos.x < left) left = tempPos.x;
            if (tempPos.x > right) right = tempPos.x;
            if (tempPos.y < down) down = tempPos.y;
            if (tempPos.y > up) up = tempPos.y;
        }
        right += hexViewSize;
        left -= hexViewSize;
        up += hexViewSize;
        down -= hexViewSize;
        for (int i = 0; i < cacheLevelTextures.length; i++) {
            switch (i) {
                case 0:
                    cacheCam.position.set(0.5f * w, 0.5f * h, 0);
                    setCacheFrame(0, 0, w, h);
                    if (left > w || down > h) continue;
                    break;
                case 1:
                    cacheCam.translate(w, 0);
                    setCacheFrame(w, 0, 2 * w, h);
                    if (right < w || down > h) continue;
                    break;
                case 2:
                    cacheCam.translate(-w, h);
                    setCacheFrame(0, h, w, 2 * h);
                    if (left > w || up < h) continue;
                    break;
                case 3:
                    cacheCam.translate(w, 0);
                    setCacheFrame(w, h, 2 * w, 2 * h);
                    if (right < w || up < h) continue;
                    break;
            }
            FrameBuffer frameBuffer = frameBufferList[i];
            frameBuffer.begin();
            Gdx.gl.glClearColor(0, 0, 0, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
            cacheCam.update();
            batchCache.setProjectionMatrix(cacheCam.combined);
            renderCache(batchCache, true);

            Texture texture = frameBuffer.getColorBufferTexture();
            texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            cacheLevelTextures[i] = new TextureRegion(texture, w, h);
            cacheLevelTextures[i].flip(false, true);
            frameBuffer.end();
        }
    }


    private void setCacheFrame(float x1, float y1, float x2, float y2) {
        cacheFrameX1 = x1;
        cacheFrameY1 = y1;
        cacheFrameX2 = x2;
        cacheFrameY2 = y2;
    }


    void updateCam() {
        orthoCam.update();
        batchMovable.setProjectionMatrix(orthoCam.combined);
        shapeRenderer.setProjectionMatrix(orthoCam.combined);
    }


    private void renderCache(SpriteBatch spriteBatch, boolean cutOffHexes) {
        spriteBatch.begin();
        spriteBatch.draw(backgroundRegion, 0, 0, 2 * yioGdxGame.w, 2 * yioGdxGame.h);
        int actualZoomQuality = currentZoomQuality;
        currentZoomQuality = 2;
        renderHexField(spriteBatch, cutOffHexes);
        currentZoomQuality = actualZoomQuality;
        spriteBatch.end();
        Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
    }


    private void renderHexField(SpriteBatch spriteBatch, boolean cutOffHexes) {
        TextureRegion currentHexTexture;

        // shadows
        for (Hex hex : gameController.activeHexes) {
            pos = hex.getPos();
            if (cutOffHexes) {
                if (!isPosInCacheFrame(pos, hexViewSize)) continue;
            }
            spriteBatch.draw(shadowHexTexture, pos.x - hexViewSize + 0.1f * hexViewSize, pos.y - hexViewSize - 0.15f * hexViewSize, 2 * hexViewSize, 2 * hexViewSize);
        }

        // hexes
        for (Hex hex : gameController.activeHexes) {
            pos = hex.getPos();
            if (cutOffHexes) {
                if (!isPosInCacheFrame(pos, hexViewSize)) continue;
            }
            currentHexTexture = getHexTextureByColor(hex.colorIndex);
            spriteBatch.draw(currentHexTexture, pos.x - 0.99f * hexViewSize, pos.y - 0.99f * hexViewSize, 2 * 0.99f * hexViewSize, 2 * 0.99f * hexViewSize);
        }

        // lines between hexes
        for (Hex hex : gameController.activeHexes) {
            pos = hex.getPos();
            if (cutOffHexes) {
                if (!isPosInCacheFrame(pos, hexViewSize)) continue;
            }
            for (int i = 0; i < 6; i++) {
                Hex adjacentHex = hex.adjacentHex(i);
                if (adjacentHex != null && ((adjacentHex.active && !adjacentHex.sameColor(hex) && i >= 2 && i <= 4) || !adjacentHex.active)) {
                    if (i >= 2 && i <= 4) renderGradientShadow(hex, adjacentHex, spriteBatch);
                    renderLineBetweenHexes(adjacentHex, hex, spriteBatch, borderLineThickness, i);
                }
            }
        }

        // solid objects
        for (Hex hex : gameController.solidObjects) {
            renderSolidObject(spriteBatch, hex.getPos(), hex.objectInside);
        }
    }


    private void renderCertainUnits(SpriteBatch spriteBatch) {
        for (Unit unit : gameController.unitList) {
            if (isPosInViewFrame(unit.currentPos, hexViewSize)) {
                renderUnit(spriteBatch, unit);
            }
        }
    }


    private TextureRegion getUnitTexture(Unit unit) {
        if (!gameController.isPlayerTurn() && unit.moveFactor.get() < 1 && unit.moveFactor.get() > 0.1) {
            return manTextures[unit.strength - 1].getLowest();
        }
        return manTextures[unit.strength - 1].getTexture(currentZoomQuality);
    }


    private void renderUnit(SpriteBatch spriteBatch, Unit unit) {
        PointYio pos = unit.currentPos;
        spriteBatch.draw(getUnitTexture(unit), pos.x - 0.7f * hexViewSize, pos.y - 0.5f * hexViewSize + unit.jumpPos * hexViewSize, 1.4f * hexViewSize, 1.6f * hexViewSize);
    }


    private TextureRegion getSolidObjectTexture(int objectType, int quality) {
        switch (objectType) {
            case Hex.OBJECT_GRAVE:
                return graveTexture.getTexture(quality);
            case Hex.OBJECT_HOUSE:
                return houseTexture.getTexture(quality);
            case Hex.OBJECT_PALM:
                return palmTexture.getTexture(quality);
            case Hex.OBJECT_PINE:
                return pineTexture.getTexture(quality);
            case Hex.OBJECT_TOWER:
                return towerTexture.getTexture(quality);
            default:
                return selectionPixel;
        }
    }


    private TextureRegion getSolidObjectTexture(int objectType) {
        return getSolidObjectTexture(objectType, currentZoomQuality);
    }


    private void renderSolidObject(SpriteBatch spriteBatch, PointYio pos, int objectType) {
        currentObjectTexture = getSolidObjectTexture(objectType);
        spriteBatch.draw(currentObjectTexture, pos.x - 0.7f * hexViewSize, pos.y - 0.5f * hexViewSize, 1.4f * hexViewSize, 1.6f * hexViewSize);
    }


    private void renderGradientShadow(Hex hex1, Hex hex2, SpriteBatch spriteBatch) {
        double a = YioGdxGame.angle(hex1.pos.x, hex1.pos.y, hex2.pos.x, hex2.pos.y);
        double cx = 0.5 * (hex1.pos.x + hex2.pos.x);
        double cy = 0.5 * (hex1.pos.y + hex2.pos.y);
        double s = 0.5 * gameController.hexSize;
        cx -= 0.2 * s * Math.cos(a);
        cy -= 0.2 * s * Math.sin(a);
        a += 0.5 * Math.PI;
        drawLine(cx + s * Math.cos(a), cy + s * Math.sin(a), cx - s * Math.cos(a), cy - s * Math.sin(a), 0.01 * w, spriteBatch, gradientShadow);
    }


    private void renderLineBetweenHexesWithOffset(Hex hex1, Hex hex2, SpriteBatch spriteBatch, double thickness, TextureRegion textureRegion, double offset, int rotation, double factor) {
        double a = YioGdxGame.angle(hex1.pos.x, hex1.pos.y, hex2.pos.x, hex2.pos.y);
        double a2 = a + 0.5 * Math.PI;
        double cx = 0.5 * (hex1.pos.x + hex2.pos.x);
        double cy = 0.5 * (hex1.pos.y + hex2.pos.y);
        double s = 0.5 * gameController.hexSize * (0.7 + 0.3 * factor);
        drawSpecialHexedLine(cx + offset * Math.cos(a) + s * Math.cos(a2), cy + offset * Math.sin(a) + s * Math.sin(a2), cx + offset * Math.cos(a) - s * Math.cos(a2), cy + offset * Math.sin(a) - s * Math.sin(a2), thickness, spriteBatch, textureRegion, rotation);
    }


    private void renderLineBetweenHexes(Hex hex1, Hex hex2, SpriteBatch spriteBatch, double thickness, int rotation) {
        double a = YioGdxGame.angle(hex1.pos.x, hex1.pos.y, hex2.pos.x, hex2.pos.y);
        a += 0.5 * Math.PI;
        double cx = 0.5 * (hex1.pos.x + hex2.pos.x);
        double cy = 0.5 * (hex1.pos.y + hex2.pos.y);
        double s = 0.5 * gameController.hexSize;
        drawSpecialHexedLine(cx + s * Math.cos(a), cy + s * Math.sin(a), cx - s * Math.cos(a), cy - s * Math.sin(a), thickness, spriteBatch, blackBorderTexture, rotation + 3);
    }


    private void drawSpecialHexedLine(double x1, double y1, double x2, double y2, double thickness, SpriteBatch spriteBatch, TextureRegion blackPixel, int rotation) {
        spriteBatch.draw(blackPixel, (float) x1, (float) (y1 - thickness * 0.5), 0f, (float) thickness * 0.5f, (float) YioGdxGame.distance(x1, y1, x2, y2), (float) thickness, 1f, 1f, (float) (180 * (-rotation / 3d)));
    }


    public void beginSpawnProcess() {
        factorModel.setValues(0.02, 0);
        factorModel.beginSpawning(3, 0.8); // 3, 1
        updateAnimationTexture();
    }


    public void beginDestroyProcess() {
        if (yioGdxGame.gamePaused) return;
        if (factorModel.get() >= 1) {
            factorModel.setValues(1, 0);
            factorModel.beginDestroying(1, 5);
        }
        updateAnimationTexture();
    }


    void updateAnimationTexture() {
        frameBuffer.begin();
        batchSolid.begin();
        batchSolid.draw(blackPixel, 0, 0, w, h);
        batchSolid.end();
        renderInternals();
        frameBuffer.end();
        Texture texture = frameBuffer.getColorBufferTexture();
        animationTextureRegion = new TextureRegion(texture);
        animationTextureRegion.flip(false, true);
    }


    private boolean isPosInCacheFrame(PointYio pos, float offset) {
        if (pos.x < cacheFrameX1 - offset) return false;
        if (pos.x > cacheFrameX2 + offset) return false;
        if (pos.y < cacheFrameY1 - offset) return false;
        if (pos.y > cacheFrameY2 + offset) return false;
        return true;
    }


    private boolean isPosInViewFrame(PointYio pos, float offset) {
        if (pos.x < gameController.frameX1 - offset) return false;
        if (pos.x > gameController.frameX2 + offset) return false;
        if (pos.y < gameController.frameY1 - offset) return false;
        if (pos.y > gameController.frameY2 + offset) return false;
        return true;
    }


    private TextureRegion getHexTextureByColor(int colorIndex) {
        switch (colorIndex) {
            default:
            case 0:
                return hexGreen;
            case 1:
                return hexRed;
            case 2:
                return hexBlue;
            case 3:
                return hexCyan;
            case 4:
                return hexYellow;
        }
    }


    private void renderAllSolidObjects() {
        for (Hex activeHex : gameController.activeHexes) {
            if (activeHex.containsSolidObject())
                renderSolidObject(batchMovable, activeHex.getPos(), activeHex.objectInside);
        }
    }


    private void renderAnimHexes() {
        PointYio pos;
        TextureRegion currentHexLastTexture, currentHexTexture;
        Color c = batchMovable.getColor();

        for (Hex hex : gameController.animHexes) {
            pos = hex.getPos();
            if (!isPosInViewFrame(pos, hexViewSize)) continue;

            if (hex.animFactor.get() < 1) {
                currentHexLastTexture = getHexTextureByColor(hex.lastColorIndex);
                batchMovable.setColor(c.r, c.g, c.b, 1f - hex.animFactor.get());
                batchMovable.draw(currentHexLastTexture, pos.x - hexViewSize, pos.y - hexViewSize, 2 * hexViewSize, 2 * hexViewSize);
            }
            currentHexTexture = getHexTextureByColor(hex.colorIndex);
            batchMovable.setColor(c.r, c.g, c.b, hex.animFactor.get());
            batchMovable.draw(currentHexTexture, pos.x - hexViewSize, pos.y - hexViewSize, 2 * hexViewSize, 2 * hexViewSize);
        }

        batchMovable.setColor(c.r, c.g, c.b, 1);
        for (Hex hex : gameController.animHexes) {
            pos = hex.getPos();
            if (!isPosInViewFrame(pos, hexViewSize)) continue;
            for (int i = 0; i < 6; i++) {
                Hex adjacentHex = hex.adjacentHex(i);
                if (adjacentHex != null && ((adjacentHex.active && !adjacentHex.sameColor(hex)) || !adjacentHex.active)) {
                    if (i >= 2 && i <= 4) renderGradientShadow(hex, adjacentHex, batchMovable);
                    renderLineBetweenHexes(adjacentHex, hex, batchMovable, borderLineThickness, i);
                }
            }
            if (hex.containsSolidObject()) {
                batchMovable.setColor(c.r, c.g, c.b, 1);
                renderSolidObject(batchMovable, pos, hex.objectInside);
            }
        }
        batchMovable.setColor(c.r, c.g, c.b, 1);
    }


    public static void drawFromCenter(SpriteBatch batch, TextureRegion textureRegion, double cx, double cy, double r) {
        batch.draw(textureRegion, (float) (cx - r), (float) (cy - r), (float) (2d * r), (float) (2d * r));
    }


    private static void drawFromCenterRotated(Batch batch, TextureRegion textureRegion, double cx, double cy, double r, double rotationAngle) {
        batch.draw(textureRegion, (float) (cx - r), (float) (cy - r), (float) r, (float) r, (float) (2d * r), (float) (2d * r), 1, 1, 57.29f * (float) rotationAngle);
    }


    private void renderResponseAnimHex() {
        if (gameController.responseAnimHex != null) {
            pos = gameController.responseAnimHex.getPos();
            Color c = batchMovable.getColor();
            batchMovable.setColor(c.r, c.g, c.b, 0.5f * Math.min(gameController.responseAnimFactor.get(), 1));
            float s = Math.max(hexViewSize, hexViewSize * gameController.responseAnimFactor.get());
            batchMovable.draw(responseAnimHexTexture, pos.x - s, pos.y - s, 2 * s, 2 * s);
            batchMovable.setColor(c.r, c.g, c.b, c.a);
        }
    }


    private void renderExclamationMarks() {
        if (!gameController.isPlayerTurn()) return;
        for (Province province : gameController.provinces) {
            if (gameController.isCurrentTurn(province.getColor()) && province.money >= GameController.PRICE_UNIT) {
                Hex capitalHex = province.getCapital();
                PointYio pos = capitalHex.getPos();
                batchMovable.draw(exclamationMarkTexture, pos.x - 0.5f * hexViewSize, pos.y + 0.3f * hexViewSize + gameController.jumperUnit.jumpPos * hexViewSize, 0.35f * hexViewSize, 0.6f * hexViewSize);
            }
        }
    }


    private void renderSelectedHexes() {
        for (Hex hex : gameController.selectedHexes) {
            if (hex.selectionFactor.get() < 0.01) continue;
            for (int i = 0; i < 6; i++) {
                Hex h = hex.adjacentHex(i);
                if (h != null && !h.isEmptyHex() && (!h.active || !h.sameColor(hex)))
                    renderLineBetweenHexesWithOffset(hex, h, batchMovable, hex.selectionFactor.get() * 0.01 * w, selectionBorder, -(1d - hex.selectionFactor.get()) * 0.01 * w, i, hex.selectionFactor.get());
            }
        }

        for (Hex hex : gameController.selectedHexes) {
            if (hex.containsSolidObject()) {
                renderSolidObject(batchMovable, hex.getPos(), hex.objectInside);
            }
        }
    }


    private void renderTextOnHex(Hex hex, String text) {
        YioGdxGame.gameFont.draw(batchMovable, text, hex.pos.x - 0.02f * w, hex.pos.y + 0.02f * w);
    }


    private void renderForefinger() {
        if (gameController.forefinger.isPointingToHex()) {
            batchMovable.begin();
            pos = gameController.forefinger.animPos;
            Color c = batchMovable.getColor();
            batchMovable.setColor(c.r, c.g, c.b, gameController.forefinger.getAlpha());
            drawFromCenterRotated(batchMovable, forefingerTexture, pos.x, pos.y, hexViewSize * gameController.forefinger.getSize(), gameController.forefinger.getRotation());
            batchMovable.setColor(c.r, c.g, c.b, c.a);
            batchMovable.end();
        } else {
            batchSolid.begin();
            pos = gameController.forefinger.animPos;
            Color c = batchSolid.getColor();
            batchSolid.setColor(c.r, c.g, c.b, gameController.forefinger.getAlpha());
            drawFromCenterRotated(batchSolid, forefingerTexture, pos.x, pos.y, hexViewSize * gameController.forefinger.getSize(), gameController.forefinger.getRotation());
            batchSolid.setColor(c.r, c.g, c.b, c.a);
            batchSolid.end();
        }
    }


    private void renderMoveZoneAndSelectedUnit() {
        PointYio pos;
        TextureRegion currentHexTexture, currentHexLastTexture;
        Color c = batchMovable.getColor();

        // drawing backgrounds of hexes in move zone
        for (Hex hex : gameController.moveZone) {
            pos = hex.getPos();
            if (!isPosInViewFrame(pos, hexViewSize)) continue;
            if (gameController.isPlayerTurn(hex.colorIndex) && hex.animFactor.get() < 1 && hex.animFactor.getDy() > 0) {
                if (hex.animFactor.get() < 1) {
                    currentHexLastTexture = getHexTextureByColor(hex.lastColorIndex);
                    batchMovable.setColor(c.r, c.g, c.b, 1f - hex.animFactor.get());
                    batchMovable.draw(currentHexLastTexture, pos.x - hexViewSize, pos.y - hexViewSize, 2 * hexViewSize, 2 * hexViewSize);
                }
                currentHexTexture = getHexTextureByColor(hex.colorIndex);
                batchMovable.setColor(c.r, c.g, c.b, hex.animFactor.get());
                batchMovable.draw(currentHexTexture, pos.x - hexViewSize, pos.y - hexViewSize, 2 * hexViewSize, 2 * hexViewSize);
                continue;
            }
            batchMovable.setColor(c.r, c.g, c.b, 1);
            currentHexTexture = getHexTextureByColor(hex.colorIndex);
            batchMovable.draw(currentHexTexture, pos.x - hexViewSize, pos.y - hexViewSize, 2 * hexViewSize, 2 * hexViewSize);
        }

        // drawing buildings and black lines between hexes
        batchMovable.setColor(c.r, c.g, c.b, 1);
        for (Hex hex : gameController.moveZone) {
            pos = hex.getPos();
            if (!isPosInViewFrame(pos, hexViewSize)) continue;
            for (int i = 0; i < 6; i++) {
                Hex adjacentHex = hex.adjacentHex(i);
                if (adjacentHex != null && ((adjacentHex.active && !adjacentHex.sameColor(hex)) || !adjacentHex.active)) {
//                    if (i >= 2 && i <= 4) renderGradientShadow(hex, adjacentHex, batchMovable); // this causes serious lag on tablet z
                    renderLineBetweenHexes(adjacentHex, hex, batchMovable, borderLineThickness, i);
                }
            }
            if (hex.containsBuilding() || hex.objectInside == Hex.OBJECT_GRAVE)
                renderSolidObject(batchMovable, pos, hex.objectInside);
        }

        renderResponseAnimHex();

        // drawing move zone border line and units
        Hex hex;
        if (gameController.selectedUnit != null || gameController.tipFactor.get() > 0 || gameController.moveZoneFactor.get() > 0) {
            for (int k = gameController.moveZone.size() - 1; k >= 0; k--) {
                hex = gameController.moveZone.get(k);
                for (int i = 0; i < 6; i++) {
                    Hex h = hex.adjacentHex(i);
                    if (h != null && !h.isEmptyHex() && (!h.active || h.inMoveZone != hex.inMoveZone))
                        renderLineBetweenHexesWithOffset(hex, h, batchMovable, gameController.moveZoneFactor.get() * 0.02 * w, moveZonePixel, -(1d - gameController.moveZoneFactor.get()) * 0.01 * w, i, gameController.moveZoneFactor.get());
                }
                if (hex.containsUnit()) renderUnit(batchMovable, hex.unit);
                if (hex.containsTree()) renderSolidObject(batchMovable, hex.pos, hex.objectInside);
            }
        }

        // drawing selection border for smooth fade in/out animation
        if (gameController.selectedHexes.size() != 0) {
            batchMovable.setColor(c.r, c.g, c.b, 1f - gameController.moveZoneFactor.get());
            for (int k = gameController.moveZone.size() - 1; k >= 0; k--) {
                hex = gameController.moveZone.get(k);
                for (int i = 0; i < 6; i++) {
                    Hex h = hex.adjacentHex(i);
                    if (h != null && !h.isEmptyHex() && (!h.active || !h.sameColor(hex)))
                        renderLineBetweenHexesWithOffset(hex, h, batchMovable, hex.selectionFactor.get() * 0.01 * w, selectionBorder, -(1d - hex.selectionFactor.get()) * 0.01 * w, i, hex.selectionFactor.get());
                }
//                renderTextOnHex(hex, "" + (int)(10 * hex.animFactor.get()));
            }
        }

        // drawing selected unit
        batchMovable.setColor(c.r, c.g, c.b, c.a);
        if (gameController.selectedUnit != null) {
            pos = gameController.selectedUnit.currentPos;
            float ar = 0.35f * hexViewSize * gameController.selUnitFactor.get();
            batchMovable.draw(selUnitShadow, pos.x - 0.7f * hexViewSize - 2 * ar, pos.y - 0.6f * hexViewSize - 2 * ar, 1.4f * hexViewSize + 4 * ar, 1.6f * hexViewSize + 4 * ar);
            batchMovable.draw(manTextures[gameController.selectedUnit.strength - 1].getNormal(), pos.x - 0.7f * hexViewSize - ar, pos.y - 0.6f * hexViewSize - ar, 1.4f * hexViewSize + 2 * ar, 1.6f * hexViewSize + 2 * ar);
        }
    }


    private void renderDebug() {
//        for (Hex hex : gameController.activeHexes) {
//            if (hex.genFlag) {
//                renderTextOnHex(hex, "" + hex.defensity);
//            }
//            if (hex.colorIndex == 0 && isPosInViewFrame(hex.pos, 0)) {
//                renderTextOnHex(hex, "" + (int)(9 * hex.selectionFactor.get()));
//            }
//        }

//        for (Province province : gameController.provinces) {
//            int c = 0;
//            for (Hex hex : province.hexList) {
//                renderTextOnHex(hex, c + "");
//                c++;
//            }
//            renderTextOnHex(province.getCapital(), province.hexList.size() + "");
//        }

//        for (Hex hex : gameController.animHexes) {
//            renderTextOnHex(hex, "O");
//        }

//        for (Unit unit : gameController.unitList) {
//            if (unit.currHex.unit != unit) renderTextOnHex(unit.currHex, "" + unit.strength);
//        }

//        for (Hex activeHex : gameController.activeHexes) {
//            if (activeHex.ignoreTouch) {
//                PointYio pos = activeHex.getPos();
//                drawFromCenter(batchMovable, blackCircleTexture, pos.x, pos.y, 0.005 * w);
//            }
//        }
    }


    private void renderBlackout() {
        Color c = batchMovable.getColor();
        batchMovable.setColor(c.r, c.g, c.b, 0.5f * gameController.blackoutFactor.get());
        batchMovable.draw(blackPixel, 0, 0, gameController.boundWidth, gameController.boundHeight);
        batchMovable.setColor(c.r, c.g, c.b, c.a);
    }


    private void renderCacheLevelTextures() {
        batchMovable.draw(cacheLevelTextures[0], 0, 0);
        if (gameController.levelSize >= GameController.SIZE_MEDIUM) {
            batchMovable.draw(cacheLevelTextures[1], w, 0);
            if (gameController.levelSize >= GameController.SIZE_BIG) {
                batchMovable.draw(cacheLevelTextures[2], 0, h);
                batchMovable.draw(cacheLevelTextures[3], w, h);
            }
        }
    }


    private void renderDefenseTips() {
        if (gameController.defenseTipFactor.get() == 0) return;
        if (gameController.defenseTips.size() == 0) return;
        Color c = batchMovable.getColor();
        batchMovable.setColor(c.r, c.g, c.b, gameController.defenseTipFactor.get());
        float x, y, size;
        for (Hex defenseTip : gameController.defenseTips) {
            PointYio tipPos = defenseTip.getPos();
            PointYio cPos = gameController.defTipHex.getPos();
            if (gameController.defenseTipFactor.getDy() >= 0) {
                x = cPos.x + gameController.defenseTipFactor.get() * (tipPos.x - cPos.x);
                y = cPos.y + gameController.defenseTipFactor.get() * (tipPos.y - cPos.y);
                size = (0.5f + 0.1f * gameController.defenseTipFactor.get()) * hexViewSize;
            } else {
                x = tipPos.x;
                y = tipPos.y;
                size = (0.7f - 0.1f * gameController.defenseTipFactor.get()) * hexViewSize;
            }
            drawFromCenter(batchMovable, defenseIcon, x, y, size);
        }
        batchMovable.setColor(c.r, c.g, c.b, c.a);
    }


    private void renderInternals() {
        if (YioGdxGame.CHECKING_BALANCE_MODE) return;
        batchMovable.begin();
        renderCacheLevelTextures();
        if (YioGdxGame.isScreenVerySmall()) renderAllSolidObjects();

        renderAnimHexes();
        renderSelectedHexes();
        renderExclamationMarks();
        renderResponseAnimHex();
        renderCertainUnits(batchMovable);
        if (gameController.moveZoneFactor.get() > 0.01) {
            renderBlackout();
        }
        renderMoveZoneAndSelectedUnit();
        renderDefenseTips();

        renderDebug();
        batchMovable.end();

        if (gameController.tutorialMode) renderForefinger();
    }


    public void render() {
        if (factorModel.get() < 0.01) {
            return;
        } else if (factorModel.get() < 1) {
            renderTransitionFrame();
        } else {
            if (gameController.backgroundVisible) {
                batchSolid.begin();
                batchSolid.draw(blackPixel, 0, 0, w, h);
                batchSolid.end();
            }
            renderInternals();
        }

        // render money
        if (gameController.selMoneyFactor.get() > 0) {
            batchSolid.begin();
            batchSolid.draw(sideShadow, w, h, 0, 0, w, 0.05f * h * gameController.selMoneyFactor.get(), 1, 1, 180);
            batchSolid.draw(sideShadow, 0, 0, w, 0.05f * h * gameController.selMoneyFactor.get());
            YioGdxGame.gameFont.draw(batchSolid, "" + gameController.selectedProvinceMoney, 0.12f * w, (1.08f - 0.1f * gameController.selMoneyFactor.get()) * h);
            YioGdxGame.gameFont.draw(batchSolid, gameController.balanceString, 0.47f * w, (1.08f - 0.1f * gameController.selMoneyFactor.get()) * h);
            batchSolid.end();
        }

        renderTip();
    }


    private void renderTransitionFrame() {
        batchSolid.begin();
        Color c = batchSolid.getColor();
        float cx = w / 2;
        float cy = h / 2;
        float fw = factorModel.get() * cx;
        float fh = factorModel.get() * cy;
        batchSolid.setColor(c.r, c.g, c.b, factorModel.get());
        batchSolid.draw(animationTextureRegion, cx - fw, cy - fh, 2 * fw, 2 * fh);
        batchSolid.setColor(c.r, c.g, c.b, c.a);
        batchSolid.end();
    }


    private void renderTip() {
        if (gameController.tipFactor.get() > 0.01) {
            batchSolid.begin();
            TextureRegion textureRegion;
            if (gameController.tipShowType == 0) textureRegion = towerTexture.getNormal();
            else textureRegion = manTextures[gameController.tipShowType - 1].getNormal();
            float s = 0.2f * w;
            batchSolid.draw(textureRegion, 0.5f * w - 0.5f * s, -s + 0.165f * h * gameController.tipFactor.get(), s, s);
            YioGdxGame.gameFont.draw(batchSolid, gameController.currentPriceString, 0.43f * w, -s + 0.165f * h * gameController.tipFactor.get());
            batchSolid.end();
        }
    }


    void moveInsideStuff() {
        if (gameController.trackerZoom < zoomLevelOne) {
            currentZoomQuality = 2;
        } else if (gameController.trackerZoom < zoomLevelTwo) {
            currentZoomQuality = 1;
        } else {
            currentZoomQuality = 0;
        }
    }


    void moveFactors() {
        factorModel.move();
    }


    private static void drawLine(double x1, double y1, double x2, double y2, double thickness, SpriteBatch spriteBatch, TextureRegion blackPixel) {
        spriteBatch.draw(blackPixel, (float) x1, (float) (y1 - thickness * 0.5), 0f, (float) thickness * 0.5f, (float) YioGdxGame.distance(x1, y1, x2, y2), (float) thickness, 1f, 1f, (float) (180 / Math.PI * YioGdxGame.angle(x1, y1, x2, y2)));
    }


    public boolean coversAllScreen() {
        return factorModel.get() > 0.99;
    }


    boolean isInMotion() {
        return factorModel.get() > 0 && factorModel.get() < 1;
    }
}
