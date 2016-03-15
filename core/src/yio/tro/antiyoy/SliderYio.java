package yio.tro.antiyoy;

import com.badlogic.gdx.Gdx;
import yio.tro.antiyoy.factor_yio.FactorYio;

import java.util.ArrayList;

/**
 * Created by ivan on 15.08.2015.
 */
class SliderYio {

    private final MenuControllerLighty menuControllerLighty;
    float runnerValue, currentVerticalPos, circleSize, segmentSize, textWidth;
    float viewMagnifier, circleDefaultSize, verticalTouchOffset;
    FactorYio appearFactor;
    FactorYio sizeFactor;
    boolean fromUp, isCurrentlyPressed;
    int numberOfSegments, configureType, minNumber;
    public static final int CONFIGURE_SIZE = 0;
    public static final int CONFIGURE_HUMANS = 1;
    public static final int CONFIGURE_COLORS = 2;
    public static final int CONFIGURE_DIFFICULTY = 3;
    public static final int CONFIGURE_SOUND = 4;
    public static final int CONFIGURE_SKIN = 5;
    public static final int CONFIGURE_SLOT_NUMBER = 6;
    public static final int CONFIGURE_AUTOSAVE = 7;
    Rect pos;
    String valueString;
    ArrayList<SliderYio> listeners;


    public SliderYio(MenuControllerLighty menuControllerLighty) {
        this.menuControllerLighty = menuControllerLighty;
        appearFactor = new FactorYio();
        sizeFactor = new FactorYio();
        pos = new Rect(0, 0, 0, 0);
        fromUp = true;
        circleDefaultSize = 0.015f * Gdx.graphics.getHeight();
        circleSize = circleDefaultSize;
        listeners = new ArrayList<SliderYio>();
        verticalTouchOffset = 0.1f * Gdx.graphics.getHeight();
    }


    void setPos(double kx, double ky, double kw, double kh) {
        pos.x = (int) (kx * Gdx.graphics.getWidth());
        pos.y = (int) (ky * Gdx.graphics.getHeight());
        pos.width = (int) (kw * Gdx.graphics.getWidth());
        pos.height = (int) (kh * Gdx.graphics.getHeight());
    }


    private boolean isCoorInsideSlider(float x, float y) {
        return x > pos.x - 0.05f * Gdx.graphics.getWidth() &&
                x < pos.x + pos.width + 0.05f * Gdx.graphics.getWidth() &&
                y > currentVerticalPos - verticalTouchOffset &&
                y < currentVerticalPos + verticalTouchOffset;
    }


    boolean touchDown(float x, float y) {
        if (isCoorInsideSlider(x, y) && appearFactor.get() == 1) {
            sizeFactor.beginSpawning(3, 2);
            isCurrentlyPressed = true;
            setValueByX(x);
            return true;
        }
        return false;
    }


    boolean touchUp(float x, float y) {
        if (isCurrentlyPressed) {
            sizeFactor.beginDestroying(1, 1);
            isCurrentlyPressed = false;
            updateValueString();
            return true;
        }
        return false;
    }


    void touchDrag(float x, float y) {
        if (isCurrentlyPressed) {
            setValueByX(x);
        }
    }


    boolean isVisible() {
        return appearFactor.get() > 0;
    }


    private void setValueByX(float x) {
        x -= pos.x;
        runnerValue = x / pos.width;
        if (runnerValue < 0) runnerValue = 0;
        if (runnerValue > 1) runnerValue = 1;
        updateValueString();
    }


    private void pullRunnerToCenterOfSegment() {
        double cx = getCurrentRunnerIndex() * segmentSize;
        double delta = cx - runnerValue;
        runnerValue += 0.2 * delta;
    }


    void move() {
        if (appearFactor.needsToMove()) appearFactor.move();
        if (sizeFactor.needsToMove()) sizeFactor.move();
        circleSize = circleDefaultSize + 0.01f * Gdx.graphics.getHeight() * sizeFactor.get();
        if (fromUp) {
            currentVerticalPos = (1 - appearFactor.get()) * (1.1f * Gdx.graphics.getHeight() - pos.y) + pos.y;
        } else {
            currentVerticalPos = appearFactor.get() * (pos.y + 0.1f * Gdx.graphics.getHeight()) - 0.1f * Gdx.graphics.getHeight();
        }
        if (!isCurrentlyPressed) pullRunnerToCenterOfSegment();
    }


    public void setValues(double runnerValue, int minNumber, int maxNumber, boolean fromUp, int configureType) {
        setRunnerValue((float) runnerValue);
        setNumberOfSegments(maxNumber - minNumber);
        setFromUp(fromUp);
        this.configureType = configureType;
        this.minNumber = minNumber;
        updateValueString();
    }


    public void setRunnerValue(float runnerValue) {
        this.runnerValue = runnerValue;
    }


    public int getCurrentRunnerIndex() {
        return (int) (runnerValue / segmentSize + 0.5);
    }


    private void setNumberOfSegments(int numberOfSegments) {
        this.numberOfSegments = numberOfSegments;
        segmentSize = 1.01f / numberOfSegments;
        viewMagnifier = (numberOfSegments + 1f) / numberOfSegments;
    }


    public void addListener(SliderYio sliderYio) {
        if (listeners.contains(sliderYio)) return;
        listeners.add(sliderYio);
    }


    private void notifyListeners() {
        for (int i = 0; i < listeners.size(); i++) {
            listeners.get(i).beNotifiedAboutChange(this);
        }
    }


    private void beNotifiedAboutChange(SliderYio sliderYio) {
        int s = sliderYio.getCurrentRunnerIndex() + sliderYio.minNumber;
        setNumberOfSegments(s);
        updateValueString();
    }


    float getSegmentCenterSize(int index) {
//        float cx = index * segmentSize;
//        float dist = Math.abs(runnerValue - cx);
//        if (dist > 0.5f * segmentSize) dist = 0.5f * segmentSize;
//        dist /= segmentSize;
//        dist *= 2;
//        if (!isCurrentlyPressed) dist = 1.0f;
//        float f = 0.5f + (1.0f - dist);
//        return f * circleDefaultSize;
        return 0.4f * circleSize;
    }


    float getSegmentLeftSidePos(int index) {
        return pos.x + index * segmentSize * pos.width;
    }


    void updateValueString() {
        LanguagesManager languagesManager = menuControllerLighty.languagesManager;
        switch (configureType) {
            default:
            case CONFIGURE_HUMANS:
                if (getCurrentRunnerIndex() + minNumber <= 1)
                    valueString = (getCurrentRunnerIndex() + minNumber) + " " + languagesManager.getString("human1");
                else if (getCurrentRunnerIndex() + minNumber <= 4)
                    valueString = (getCurrentRunnerIndex() + minNumber) + " " + languagesManager.getString("human2");
                else
                    valueString = (getCurrentRunnerIndex() + minNumber) + " " + languagesManager.getString("human3");
                break;
            case CONFIGURE_COLORS:
                if (getCurrentRunnerIndex() + minNumber <= 4)
                    valueString = (getCurrentRunnerIndex() + minNumber) + " " + languagesManager.getString("color");
                else
                    valueString = (getCurrentRunnerIndex() + minNumber) + " " + languagesManager.getString("colors");
                break;
            case CONFIGURE_SIZE:
                int size = getCurrentRunnerIndex();
                switch (size) {
                    default:
                    case 0:
                        valueString = languagesManager.getString("small");
                        break;
                    case 1:
                        valueString = languagesManager.getString("medium");
                        break;
                    case 2:
                        valueString = languagesManager.getString("big");
                        break;
                }
                break;
            case CONFIGURE_DIFFICULTY:
                switch (getCurrentRunnerIndex()) {
                    case 0:
                        valueString = languagesManager.getString("easy");
                        break;
                    case 1:
                        valueString = languagesManager.getString("normal");
                        break;
                    case 2:
                        valueString = languagesManager.getString("hard");
                        break;
                    case 3:
                        valueString = languagesManager.getString("expert");
                        break;
                }
                break;
            case CONFIGURE_AUTOSAVE:
            case CONFIGURE_SOUND:
                if (getCurrentRunnerIndex() == 0)
                    valueString = languagesManager.getString("off");
                else
                    valueString = languagesManager.getString("on");
                break;
            case CONFIGURE_SKIN:
                switch (getCurrentRunnerIndex()) {
                    case 0:
                        valueString = languagesManager.getString("original");
                        break;
                    case 1:
                        valueString = languagesManager.getString("points");
                        break;
                    case 2:
                        valueString = languagesManager.getString("grid");
                        break;
                }
                break;
            case CONFIGURE_SLOT_NUMBER:
                switch (getCurrentRunnerIndex()) {
                    case 0:
                        valueString = languagesManager.getString("interface_simple");
                        break;
                    case 1:
                        valueString = languagesManager.getString("interface_complicated");
                        break;
                }
                break;
        }
        textWidth = YioGdxGame.gameFont.getBounds(valueString).width;
        notifyListeners();
    }


    public void setVerticalTouchOffset(float verticalTouchOffset) {
        this.verticalTouchOffset = verticalTouchOffset;
    }


    String getValueString() {
        return valueString;
    }


    private void setFromUp(boolean fromUp) {
        this.fromUp = fromUp;
    }
}
