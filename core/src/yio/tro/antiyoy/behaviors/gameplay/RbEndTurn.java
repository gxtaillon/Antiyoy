package yio.tro.antiyoy.behaviors.gameplay;

import yio.tro.antiyoy.ButtonLighty;
import yio.tro.antiyoy.behaviors.ReactBehavior;

/**
 * Created by ivan on 26.10.2014.
 */
public class RbEndTurn extends ReactBehavior {

    @Override
    public void reactAction(ButtonLighty buttonLighty) {
        getGameController(buttonLighty).endTurnButtonPressed();
    }
}
