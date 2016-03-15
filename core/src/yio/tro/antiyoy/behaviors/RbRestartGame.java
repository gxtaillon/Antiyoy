package yio.tro.antiyoy.behaviors;

import yio.tro.antiyoy.ButtonLighty;
import yio.tro.antiyoy.YioGdxGame;

/**
 * Created by ivan on 11.11.2015.
 */
public class RbRestartGame extends ReactBehavior {

    @Override
    public void reactAction(ButtonLighty buttonLighty) {
        if (YioGdxGame.interface_type == YioGdxGame.INTERFACE_SIMPLE) {
            getYioGdxGame(buttonLighty).restartGame();
        } else {
            if (buttonLighty.id == 221) { // restart game confirmed
                getYioGdxGame(buttonLighty).restartGame();
            } else {
                buttonLighty.menuControllerLighty.createConfirmRestartMenu();
            }
        }
    }
}
