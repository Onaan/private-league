package de.draigon.waw;

/**
 * Created by IntelliJ IDEA.
 * User: Schnabel
 * Date: 07.06.12
 * Time: 19:30
 * To change this template use File | Settings | File Templates.
 */
public class TeamBetData {
    private boolean bettable = false;
    private CharSequence[] choices;
    private CharSequence selected = null;

    public CharSequence getSelected() {
        return selected;
    }

    public void setSelected(CharSequence selected) {
        this.selected = selected;
    }

    public CharSequence[] getChoices() {
        return choices;
    }

    public void setChoices(CharSequence[] choices) {
        this.choices = choices;
    }

    public boolean isBettable() {
        return bettable;
    }

    public void setBettable(boolean bettable) {
        this.bettable = bettable;
    }
}
