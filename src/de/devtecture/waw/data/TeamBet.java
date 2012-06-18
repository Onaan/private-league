package de.devtecture.waw.data;

public class TeamBet {
// ------------------------------ FIELDS ------------------------------

    private CharSequence[] choices;
    private CharSequence selected = null;
    private boolean bettable = false;
// --------------------- GETTER / SETTER METHODS ---------------------

    public CharSequence[] getChoices() {
        return this.choices;
    }

    public void setChoices(final CharSequence[] choices) {
        this.choices = choices;
    }

    public CharSequence getSelected() {
        return this.selected;
    }

    @SuppressWarnings({"SameParameterValue"})
    public void setSelected(final CharSequence selected) {
        this.selected = selected;
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public boolean isBettable() {
        return this.bettable;
    }

    public void setBettable(final boolean bettable) {
        this.bettable = bettable;
    }
}
