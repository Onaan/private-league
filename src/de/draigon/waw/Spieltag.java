package de.draigon.waw;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Schnabel
 * Date: 06.06.12
 * Time: 11:47
 * To change this template use File | Settings | File Templates.
 */
public class Spieltag {
    private String name;
    private List<Match> matches;


    public Spieltag(String name) {
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
