package de.draigon.waw.data;

public class TeamBet {
    private boolean bettable = false;
    private CharSequence[] choices;
    private CharSequence selected = null;

    public CharSequence getSelected() {
        return this.selected;
    }

    public void setSelected(final CharSequence selected) {
        this.selected = selected;
    }

    public CharSequence[] getChoices() {
        return this.choices;
    }

    public void setChoices(final CharSequence[] choices) {
        this.choices = choices;
    }

    public boolean isBettable() {
        return this.bettable;
    }

    public void setBettable(final boolean bettable) {
        this.bettable = bettable;
    }
}
