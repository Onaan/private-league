package de.draigon.waw.data;

import de.draigon.waw.data.Match;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Schnabel
 * Date: 06.06.12
 * Time: 11:47
 * To change this template use File | Settings | File Templates.
 */
public class MatchDay {
    private String name;
    private List<Match> matches;


    public MatchDay(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Match> getMatches() {
        return matches;
    }

    public void setMatches(List<Match> matches) {
        this.matches = matches;
    }
}
