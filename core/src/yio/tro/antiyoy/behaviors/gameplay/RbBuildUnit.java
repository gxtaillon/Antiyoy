package yio.tro.antiyoy.behaviors.gameplay;

import yio.tro.antiyoy.ButtonLighty;
import yio.tro.antiyoy.YioGdxGame;
import yio.tro.antiyoy.behaviors.ReactBehavior;

/**
 * Created by ivan on 31.05.2015.
 */
public class RbBuildUnit extends ReactBehavior {

    @Override
    public void reactAction(ButtonLighty buttonLighty) {
        if (!getGameController(buttonLighty).isSomethingSelected()) {
            YioGdxGame.say("detected strange bug in RbBuildUnit");
            return;
        }
        int t = getGameController(buttonLighty).getTipType();
        t += 1;
        if (t > 4) t = 1;
        getGameController(buttonLighty).awakeTip(t);
        getGameController(buttonLighty).detectAndShowMoveZoneForBuildingUnit(t);
    }
}
