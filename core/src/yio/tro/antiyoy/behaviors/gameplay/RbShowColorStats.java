package yio.tro.antiyoy.behaviors.gameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Matrix4;
import yio.tro.antiyoy.*;
import yio.tro.antiyoy.behaviors.ReactBehavior;

/**
 * Created by ivan on 29.12.2015.
 */
public class RbShowColorStats extends ReactBehavior {

    private FrameBuffer frameBuffer;
    private SpriteBatch batch;
    private SimpleRectangle pos;
    TextureRegion buttonBackground, greenPixel, redPixel, bluePixel, cyanPixel, yellowPixel, blackPixel;


    private void initEverything() {
        batch = new SpriteBatch();
        buttonBackground = GameView.loadTextureRegionByName("pixels/pixel_dark_gray.png", true);
        greenPixel = GameView.loadTextureRegionByName("pixels/pixel_green.png", false);
        redPixel = GameView.loadTextureRegionByName("pixels/pixel_red.png", false);
        bluePixel = GameView.loadTextureRegionByName("pixels/pixel_blue.png", false);
        cyanPixel = GameView.loadTextureRegionByName("pixels/pixel_cyan.png", false);
        yellowPixel = GameView.loadTextureRegionByName("pixels/pixel_yellow.png", false);
        blackPixel = GameView.loadTextureRegionByName("black_pixel.png", false);
    }


    @Override
    public void reactAction(ButtonLighty buttonLighty) {
        buttonLighty.menuControllerLighty.showColorStats();
        renderStatButton(buttonLighty.menuControllerLighty.getButtonById(56321), getGameController(buttonLighty).getPlayerHexCount());
    }


    TextureRegion getPixelByIndex(int colorIndex) {
        switch (colorIndex) {
            default:
            case 0:
                return greenPixel;
            case 1:
                return redPixel;
            case 2:
                return bluePixel;
            case 3:
                return cyanPixel;
            case 4:
                return yellowPixel;
        }
    }


    void setFontColorByIndex(int index) {
        BitmapFont font = YioGdxGame.buttonFont;
        switch (index) {
            case 0:
                font.setColor(0.37f, 0.7f, 0.36f, 1);
                break;
            case 1:
                font.setColor(0.7f, 0.36f, 0.46f, 1);
                break;
            case 2:
                font.setColor(0.45f, 0.36f, 0.7f, 1);
                break;
            case 3:
                font.setColor(0.36f, 0.7f, 0.69f, 1);
                break;
            case 4:
                font.setColor(0.7f, 0.71f, 0.39f, 1);
                break;
        }
    }


    void renderStatButton(ButtonLighty statButton, int playerHexCount[]) {
        initEverything();
        beginRender(statButton, YioGdxGame.gameFont);
        batch.begin();

        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();
        float columnWidth = 0.1f * w;
        float distanceBetweenColumns = (w - 2 * columnWidth) / (playerHexCount.length - 1);
        float maxNumber = GameController.maxNumberFromArray(playerHexCount);
        float columnHeight = 0.25f * h;
        for (int i = 0; i < playerHexCount.length; i++) {
            setFontColorByIndex(i);
            float numberLineWidth = YioGdxGame.buttonFont.getBounds("" + playerHexCount[i]).width;
            float columnX = columnWidth + distanceBetweenColumns * i;
            batch.draw(blackPixel, columnX - numberLineWidth / 2 - 0.01f * w, 0.28f * h, numberLineWidth + 0.02f * w, 0.05f * h);
            YioGdxGame.buttonFont.draw(batch, "" + playerHexCount[i], columnX - numberLineWidth / 2, 0.29f * h);

            float currentSize = (float) playerHexCount[i] / maxNumber;
            currentSize *= columnHeight;
            batch.draw(getPixelByIndex(i), columnX - columnWidth / 2, 0.01f * h + columnHeight - currentSize, columnWidth, currentSize);
        }
        batch.draw(blackPixel, 0.025f * w, 0.0125f * h + columnHeight, 0.95f * w, 0.005f * h);

        YioGdxGame.buttonFont.setColor(0, 0, 0, 1);
        batch.end();
        endRender(statButton);
    }


    private void beginRender(ButtonLighty buttonLighty, BitmapFont font) {
        if (frameBuffer != null) frameBuffer.dispose();
        frameBuffer = new FrameBuffer(Pixmap.Format.RGB565, Gdx.graphics.getWidth(), Gdx.graphics.getHeight() / 2, false);
        frameBuffer.begin();
        Gdx.gl.glClearColor(buttonLighty.backColor.r, buttonLighty.backColor.g, buttonLighty.backColor.b, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Matrix4 matrix4 = new Matrix4();
        int orthoWidth = Gdx.graphics.getWidth();
        int orthoHeight = Gdx.graphics.getHeight() / 2;
        matrix4.setToOrtho2D(0, 0, orthoWidth, orthoHeight);
        batch.setProjectionMatrix(matrix4);
        batch.begin();
        batch.draw(buttonBackground, 0, 0, orthoWidth, orthoHeight);
        batch.end();
        pos = new SimpleRectangle(buttonLighty.position);
    }


    void endRender(ButtonLighty buttonLighty) {
        Texture texture = frameBuffer.getColorBufferTexture();
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        buttonLighty.textureRegion = new TextureRegion(texture, (int) pos.width, (int) pos.height);
        frameBuffer.end();
    }
}
