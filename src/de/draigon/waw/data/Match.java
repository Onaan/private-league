package de.draigon.waw.data;

import java.io.Serializable;
import java.util.Date;

public class Match implements Serializable {
    private String id;
    private String homeTeam;
    private String guestTeam;
    private String homeScore;
    private String guestScore;
    private String homeScoreTip = "-";
    private String guestScoreTip = "-";
    private Date kickOff;
    private boolean bettable;

    public boolean isBettable() {
        return this.bettable;
    }

    public void setBettable(final boolean bettable) {
        this.bettable = bettable;
    }

    public String getId() {
        return this.id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getHomeTeam() {
        return this.homeTeam;
    }

    public void setHomeTeam(final String homeTeam) {
        this.homeTeam = homeTeam;
    }

    public String getGuestTeam() {
        return this.guestTeam;
    }

    public void setGuestTeam(final String guestTeam) {
        this.guestTeam = guestTeam;
    }

    public String getHomeScore() {
        return this.homeScore;
    }

    public void setHomeScore(final String homeScore) {
        this.homeScore = homeScore;
    }

    public String getGuestScore() {
        return this.guestScore;
    }

    public void setGuestScore(final String guestScore) {
        this.guestScore = guestScore;
    }

    public String getHomeScoreTip() {
        return this.homeScoreTip;
    }

    public void setHomeScoreTip(final String homeScoreTip) {
        this.homeScoreTip = homeScoreTip;
    }

    public String getGuestScoreTip() {
        return this.guestScoreTip;
    }

    public void setGuestScoreTip(final String guestScoreTip) {
        this.guestScoreTip = guestScoreTip;
    }

    public Date getKickOff() {
        return this.kickOff;
    }

    public void setKickOff(final Date kickOff) {
        this.kickOff = kickOff;
    }
}
