package de.draigon.waw.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Match implements Serializable {
    private String id;
    private String home;
    private String guest;
    private String homeScore = "-";
    private String guestScore = "-";
    private String homeScoreBet = "-";
    private String guestScoreBet = "-";
    private String homeTempScore = "";
    private String guestTempScore = "";

    private Date kickOff;
    private boolean bettable;

    private List<CharSequence> bets = new ArrayList<CharSequence>();


    public boolean isRunning() {
        return (!this.bettable && "-".equals(this.homeScore) && "-".equals(this.guestScore));
    }

    public List<CharSequence> getBets() {
        return this.bets;
    }

    public void setBets(final List<CharSequence> bets) {
        this.bets = bets;
    }

    public String getHomeTempScore() {
        return this.homeTempScore;
    }

    public void setHomeTempScore(final String homeTempScore) {
        try {
            Integer.parseInt(homeTempScore);
            this.homeTempScore = homeTempScore;
        } catch (IllegalArgumentException e) {
            // Argument is not a number, so we keep the ""
        }

    }

    public String getGuestTempScore() {
        return this.guestTempScore;
    }

    public void setGuestTempScore(final String guestTempScore) {
        try {
            Integer.parseInt(guestTempScore);
            this.guestTempScore = guestTempScore;
        } catch (IllegalArgumentException e) {
            // Argument is not a number, so we keep the ""
        }
    }

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

    public String getHome() {
        return this.home;
    }

    public void setHome(final String home) {
        this.home = home;
    }

    public String getGuest() {
        return this.guest;
    }

    public void setGuest(final String guest) {
        this.guest = guest;
    }

    public String getHomeScore() {
        return this.homeScore;
    }

    public void setHomeScore(final String homeScore) {
        try {
            Integer.parseInt(homeScore);
            this.homeScore = homeScore;

        } catch (IllegalArgumentException e) {
            // Argument is not a number, so we keep the "-"
        }
    }

    public String getGuestScore() {
        return this.guestScore;
    }

    public void setGuestScore(final String guestScore) {
        try {
            Integer.parseInt(guestScore);
            this.guestScore = guestScore;

        } catch (IllegalArgumentException e) {
            // Argument is not a number, so we keep the "-"
        }
    }

    public String getHomeScoreBet() {
        return this.homeScoreBet;
    }

    public void setHomeScoreBet(final String homeScoreBet) {
        this.homeScoreBet = homeScoreBet;
    }

    public String getGuestScoreBet() {
        return this.guestScoreBet;
    }

    public void setGuestScoreBet(final String guestScoreBet) {
        this.guestScoreBet = guestScoreBet;
    }

    public Date getKickOff() {
        return this.kickOff;
    }

    public void setKickOff(final Date kickOff) {
        this.kickOff = kickOff;
    }

    @Override
    public String toString() {
        return "Match{" +
                "id='" + this.id + '\'' +
                ", home='" + this.home + '\'' +
                ", guest='" + this.guest + '\'' +
                ", homeScore='" + this.homeScore + '\'' +
                ", guestScore='" + this.guestScore + '\'' +
                ", homeScoreBet='" + this.homeScoreBet + '\'' +
                ", guestScoreBet='" + this.guestScoreBet + '\'' +
                ", homeTempScore='" + this.homeTempScore + '\'' +
                ", guestTempScore='" + this.guestTempScore + '\'' +
                ", kickOff=" + this.kickOff +
                ", bettable=" + this.bettable +
                '}';
    }
}
