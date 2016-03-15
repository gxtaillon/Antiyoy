package yio.tro.antiyoy;

/**
 * Created by ivan on 12.11.2015.
 */
public class TutorialScript {

    final GameController gameController;
    MenuControllerLighty menuControllerLighty;
    final String map = "10 6 0 0 0 0 106#10 7 0 3 0 0 106#10 8 1 0 0 0 0#11 4 4 0 0 0 9#11 5 1 3 0 0 9#11 6 1 0 0 0 9#12 2 0 3 0 0 104#12 3 4 3 0 0 9#12 4 4 0 0 0 9#12 5 1 0 0 0 9#13 2 0 2 0 0 104#13 3 4 0 0 0 9#13 4 1 0 1 0 9#13 5 3 3 0 0 6#14 2 4 0 1 0 9#14 3 2 0 0 0 5#14 4 2 0 2 0 5#14 5 3 0 0 0 6#14 9 4 0 0 0 2#15 2 2 0 0 0 5#15 3 2 0 0 0 5#15 4 2 0 0 0 5#15 5 1 0 0 0 12#15 6 4 0 1 0 2#15 7 4 3 0 0 2#15 8 4 4 0 0 2#16 2 2 0 1 0 5#16 3 2 3 0 0 5#16 4 1 0 2 0 12#16 5 1 0 0 0 12#16 6 1 0 0 0 12#16 7 4 0 0 0 2#16 8 4 0 0 0 2#16 9 4 0 0 0 2#17 2 2 0 0 0 5#17 3 4 0 0 0 3#17 4 1 0 0 0 12#17 5 1 3 0 0 12#17 6 4 0 1 0 2#17 7 2 0 0 0 10#18 2 4 3 0 0 3#18 4 1 0 0 0 12#18 5 2 0 0 0 10#18 6 2 0 0 0 10#18 7 2 3 0 0 10#18 8 4 0 0 0 17#18 11 0 0 0 0 238#19 4 3 0 0 0 15#19 5 2 0 2 0 10#19 6 2 0 0 0 10#19 7 4 3 0 0 17#19 8 0 0 2 0 238#19 10 0 0 0 0 238#19 11 0 0 0 0 238#20 4 3 3 0 0 15#20 5 3 0 1 0 15#20 6 0 0 0 0 238#20 7 0 0 1 1 238#20 8 0 0 1 1 238#20 9 0 2 0 0 238#20 10 0 0 0 0 238#20 11 0 0 0 0 238#21 4 3 0 0 0 15#21 5 0 0 2 1 238#21 6 0 0 0 0 238#21 7 0 0 0 0 238#21 8 0 0 0 0 238#21 9 0 0 0 0 238#22 2 2 0 1 0 0#22 3 2 3 0 0 0#22 4 0 0 0 0 238#22 5 0 0 0 0 238#22 6 0 3 0 0 238#22 7 0 0 0 0 238#22 8 0 0 0 0 238#23 4 0 0 0 0 238#23 5 0 0 0 0 238#23 6 0 0 0 0 238#24 4 0 0 0 0 238#24 5 0 0 0 0 238#25 3 0 0 0 0 238";
    public static final int STEP_SELECT_SPEARMAN = 0;
    public static final int STEP_ATTACK_WITH_SPEARMAN = 1;
    public static final int STEP_SELECT_MAN = 2;
    public static final int STEP_ATTACK_WITH_MAN = 3;
    public static final int STEP_PRESS_BUILD_TOWER_BUTTON = 4;
    public static final int STEP_BUILD_TOWER = 5;
    public static final int STEP_PRESS_BUILD_UNIT_BUTTON_FIRST_TIME = 6;
    public static final int STEP_PRESS_BUILD_UNIT_BUTTON_SECOND_TIME = 7;
    public static final int STEP_BUILD_SPEARMAN = 8;
    public static final int STEP_PRESS_UNDO = 9;
    public static final int STEP_PRESS_END_TURN = 10;
    public static final int STEP_SELECT_PROVINCE = 11;
    public static final int STEP_HOLD_TO_MARCH = 12;
    public static final int STEP_PRESS_UNDO_AGAIN = 13;
    public static final int STEP_SELECT_UNIT_TO_MERGE = 14;
    public static final int STEP_MERGE_UNITS = 15;
    public static final int STEP_GOOD_LUCK = 16;
    int currentStep;
    LanguagesManager languagesManager;
    boolean waitingBeforeNextStep, tipIsCurrentlyShown;
    long timeToCheck, timeForNextStep;


    public TutorialScript(GameController gameController) {
        this.gameController = gameController;
    }


    void createTutorialGame() {
        GameSaver gameSaver = gameController.gameSaver;
        gameSaver.setActiveHexesString(map);
        gameSaver.beginRecreation();
        gameSaver.setBasicInfo(0, 1, 5, 1, 0);
        gameSaver.endRecreation();

        menuControllerLighty = gameController.yioGdxGame.menuControllerLighty;
        languagesManager = menuControllerLighty.languagesManager;
        currentStep = -1;
        waitingBeforeNextStep = true;
        allButtonsIgnoreTouches();
        allHexesIgnoreTouches();
        showTutorialTip("tip_capture_with_units");
    }


    private Hex getHex(int x, int y) {
        return gameController.field[x + gameController.compensationOffsetY][y];
    }


    private void pointToHex(int x, int y) {
        gameController.forefinger.setPointTo(getHex(x, y));
    }


    private void pointToMenu(double x, double y, double rotation) {
        gameController.forefinger.setPointTo(x, y, rotation);
    }


    private void showMessage(String key) {
        menuControllerLighty.showNotification(languagesManager.getString(key), false);
    }


    private void showTutorialTip(String key) {
        menuControllerLighty.createTutorialTip(menuControllerLighty.getArrayListFromString(languagesManager.getString(key)));
        tipIsCurrentlyShown = true;
    }


    private void stepSetup() {
        ButtonLighty buttonLighty;
        switch (currentStep) {
            default:
            case STEP_SELECT_SPEARMAN:
                setOnlyHexToRespond(21, 5);
                showMessage("tut_select_unit");
                break;
            case STEP_ATTACK_WITH_SPEARMAN:
                setOnlyHexToRespond(19, 7);
                showMessage("tut_tap_on_hex");
                break;
            case STEP_SELECT_MAN:
                setOnlyHexToRespond(20, 7);
                showMessage("tut_select_unit");
                break;
            case STEP_ATTACK_WITH_MAN:
                setOnlyHexToRespond(18, 8);
                showMessage("tut_tap_on_hex");
                break;
            case STEP_PRESS_BUILD_TOWER_BUTTON:
                buttonLighty = setOnlyButtonToRespond(38, "tut_press_button");
                pointToMenu(buttonLighty.x1, buttonLighty.y2, -0.75 * Math.PI);
                break;
            case STEP_BUILD_TOWER:
                setOnlyHexToRespond(21, 5);
                showMessage("tut_build_tower");
                break;
            case STEP_PRESS_BUILD_UNIT_BUTTON_FIRST_TIME:
                buttonLighty = setOnlyButtonToRespond(39, "tut_press_button");
                pointToMenu(buttonLighty.x2, buttonLighty.y2, 0.75 * Math.PI);
                break;
            case STEP_PRESS_BUILD_UNIT_BUTTON_SECOND_TIME:
                buttonLighty = setOnlyButtonToRespond(39, "tut_again");
                pointToMenu(buttonLighty.x2, buttonLighty.y2, 0.75 * Math.PI);
                break;
            case STEP_BUILD_SPEARMAN:
                setOnlyHexToRespond(20, 5);
                showMessage("tut_build_spearman");
                break;
            case STEP_PRESS_UNDO:
                buttonLighty = setOnlyButtonToRespond(32, "tut_tap_to_undo");
                pointToMenu(buttonLighty.x2, buttonLighty.y2, 0.75 * Math.PI);
                break;
            case STEP_PRESS_END_TURN:
                buttonLighty = setOnlyButtonToRespond(31, "tut_tap_to_end_turn");
                pointToMenu(buttonLighty.x1, buttonLighty.y2, -0.75 * Math.PI);
                break;
            case STEP_SELECT_PROVINCE:
                setOnlyHexToRespond(22, 5);
                for (int i = 0; i < 6; i++) {
                    Hex adjHex = getHex(22, 5).adjacentHex(i);
                    adjHex.setIgnoreTouch(false);
                }
                showMessage("tut_tap_on_hex");
                break;
            case STEP_HOLD_TO_MARCH:
                setOnlyHexToRespond(22, 4);
                showMessage("tut_hold_hex");
                break;
            case STEP_PRESS_UNDO_AGAIN:
                buttonLighty = setOnlyButtonToRespond(32, "tut_tap_to_undo");
                pointToMenu(buttonLighty.x2, buttonLighty.y2, 0.75 * Math.PI);
                break;
            case STEP_SELECT_UNIT_TO_MERGE:
                setOnlyHexToRespond(20, 8);
                showMessage("tut_select_unit");
                break;
            case STEP_MERGE_UNITS:
                setOnlyHexToRespond(18, 8);
                showMessage("tut_merge_units");
                break;
            case STEP_GOOD_LUCK:
                resetIgnores();
//                menuControllerLighty.showNotification(languagesManager.getString("tut_good_luck"), true);
                break;
        }
    }


    private void checkToShowTip() {
        switch (currentStep) {
            default:
            case STEP_SELECT_SPEARMAN:

                break;
            case STEP_ATTACK_WITH_SPEARMAN:
                showTutorialTip("tip_about_money");
                break;
            case STEP_SELECT_MAN:

                break;
            case STEP_ATTACK_WITH_MAN:
                showTutorialTip("tip_build_towers");
                break;
            case STEP_PRESS_BUILD_TOWER_BUTTON:

                break;
            case STEP_BUILD_TOWER:
                showTutorialTip("tip_about_defense");
                break;
            case STEP_PRESS_BUILD_UNIT_BUTTON_FIRST_TIME:

                break;
            case STEP_PRESS_BUILD_UNIT_BUTTON_SECOND_TIME:

                break;
            case STEP_BUILD_SPEARMAN:
                showTutorialTip("tip_about_taxes");
                break;
            case STEP_PRESS_UNDO:
                showTutorialTip("tip_trees");
                break;
            case STEP_PRESS_END_TURN:
                showTutorialTip("tip_hold_to_march");
                break;
            case STEP_SELECT_PROVINCE:

                break;
            case STEP_HOLD_TO_MARCH:

                break;
            case STEP_PRESS_UNDO_AGAIN:
                showTutorialTip("tip_merging");
                break;
            case STEP_SELECT_UNIT_TO_MERGE:

                break;
            case STEP_MERGE_UNITS:
                showTutorialTip("tip_help");
                menuControllerLighty.addHelpButtonToTutorialTip();
                break;
            case STEP_GOOD_LUCK:

                break;
        }
    }


    private boolean isStepComplete() {
        switch (currentStep) {
            default:
            case STEP_SELECT_SPEARMAN:
                if (gameController.selectedUnit != null) return true;
                return false;
            case STEP_ATTACK_WITH_SPEARMAN:
                if (getHex(19, 7).colorIndex == 0) return true;
                return false;
            case STEP_SELECT_MAN:
                if (gameController.selectedUnit != null) return true;
                return false;
            case STEP_ATTACK_WITH_MAN:
                if (getHex(18, 8).colorIndex == 0) return true;
                return false;
            case STEP_PRESS_BUILD_TOWER_BUTTON:
                if (gameController.tipFactor.get() > 0) return true;
                return false;
            case STEP_BUILD_TOWER:
                if (getHex(21, 5).objectInside == Hex.OBJECT_TOWER) return true;
                return false;
            case STEP_PRESS_BUILD_UNIT_BUTTON_FIRST_TIME:
                if (gameController.tipFactor.get() > 0) return true;
                return false;
            case STEP_PRESS_BUILD_UNIT_BUTTON_SECOND_TIME:
                if (gameController.tipType == 2) return true;
                return false;
            case STEP_BUILD_SPEARMAN:
                if (getHex(20, 5).colorIndex == 0) return true;
                return false;
            case STEP_PRESS_UNDO:
                if (getHex(20, 5).colorIndex != 0) return true;
                return false;
            case STEP_PRESS_END_TURN:
                if (menuControllerLighty.getButtonById(31).isCurrentlyTouched()) return true;
                return false;
            case STEP_SELECT_PROVINCE:
                if (gameController.isSomethingSelected()) return true;
                return false;
            case STEP_HOLD_TO_MARCH:
                if (!getHex(19, 8).containsUnit()) return true;
                return false;
            case STEP_PRESS_UNDO_AGAIN:
                if (getHex(19, 8).containsUnit()) return true;
                return false;
            case STEP_SELECT_UNIT_TO_MERGE:
                if (gameController.selectedUnit != null) return true;
                return false;
            case STEP_MERGE_UNITS:
                if (getHex(18, 8).unit.strength == 2) return true;
                return false;
            case STEP_GOOD_LUCK:
                return false;
        }
    }


    private ButtonLighty setOnlyButtonToRespond(int id, String message) {
        allButtonsIgnoreTouches();
        allHexesIgnoreTouches();
        ButtonLighty buttonLighty = menuControllerLighty.getButtonById(id);
        buttonLighty.setTouchable(true);
        showMessage(message);
        return buttonLighty;
    }


    private void resetIgnores() {
        for (int i = 30; i <= 32; i++) {
            ButtonLighty buttonLighty = menuControllerLighty.getButtonById(i);
            buttonLighty.setTouchable(true);
        }
        for (int i = 38; i <= 39; i++) {
            ButtonLighty buttonLighty = menuControllerLighty.getButtonById(i);
            if (buttonLighty == null) continue;
            buttonLighty.setTouchable(true);
        }
        for (int i = 0; i < gameController.fWidth; i++) {
            for (int j = 0; j < gameController.fHeight; j++) {
                gameController.field[i][j].setIgnoreTouch(false);
            }
        }
    }


    private void allButtonsIgnoreTouches() {
        for (int i = 30; i <= 32; i++) {
            ButtonLighty buttonLighty = menuControllerLighty.getButtonById(i);
            buttonLighty.setTouchable(false);
        }
        for (int i = 38; i <= 39; i++) {
            ButtonLighty buttonLighty = menuControllerLighty.getButtonById(i);
            if (buttonLighty == null) continue;
            buttonLighty.setTouchable(false);
        }
    }


    private void setOnlyHexToRespond(int x, int y) {
        allButtonsIgnoreTouches();
        allHexesIgnoreTouches();
        getHex(x, y).setIgnoreTouch(false);
        pointToHex(x, y);
    }


    private void allHexesIgnoreTouches() {
        for (int i = 0; i < gameController.fWidth; i++) {
            for (int j = 0; j < gameController.fHeight; j++) {
                gameController.field[i][j].setIgnoreTouch(true);
            }
        }
    }


    void move() {
        if (waitingBeforeNextStep) {
            if (gameController.currentTime > timeForNextStep && !tipIsCurrentlyShown) {
                waitingBeforeNextStep = false;
                timeToCheck = gameController.currentTime + 200;
                currentStep++;
                stepSetup();
            }
        } else {
            if (gameController.currentTime > timeToCheck) {
                timeToCheck = gameController.currentTime + 200;
                if (isStepComplete()) {
                    allHexesIgnoreTouches();
                    allButtonsIgnoreTouches();
                    waitingBeforeNextStep = true;
                    timeForNextStep = gameController.currentTime + 500;
                    menuControllerLighty.hideNotification();
                    gameController.forefinger.hide();
                    checkToShowTip();
                }
            }
        }
    }


    public void setTipIsCurrentlyShown(boolean tipIsCurrentlyShown) {
        this.tipIsCurrentlyShown = tipIsCurrentlyShown;
    }
}
