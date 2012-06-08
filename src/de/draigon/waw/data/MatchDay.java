package de.draigon.waw.data;

import java.util.List;

public class MatchDay {
    private final String name;
    private List<Match> matches;


    public MatchDay(final String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public List<Match> getMatches() {
        return this.matches;
    }

    public void setMatches(final List<Match> matches) {
        this.matches = matches;
    }
}
