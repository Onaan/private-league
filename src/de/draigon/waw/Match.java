package de.draigon.waw;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: Schnabel
 * Date: 06.06.12
 * Time: 11:07
 * To change this template use File | Settings | File Templates.
 */
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
        return bettable;
    }

    public void setBettable(boolean bettable) {
        this.bettable = bettable;
    }


    private State state;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public static enum State {
        UPCOMMING, RUNNING, FINISHED
    }


    public String getHomeTeam() {
        return homeTeam;
    }

    public void setHomeTeam(String homeTeam) {
        this.homeTeam = homeTeam;
    }

    public String getGuestTeam() {
        return guestTeam;
    }

    public void setGuestTeam(String guestTeam) {
        this.guestTeam = guestTeam;
    }

    public String getHomeScore() {
        return homeScore;
    }

    public void setHomeScore(String homeScore) {
        this.homeScore = homeScore;
    }

    public String getGuestScore() {
        return guestScore;
    }

    public void setGuestScore(String guestScore) {
        this.guestScore = guestScore;
    }

    public String getHomeScoreTip() {
        return homeScoreTip;
    }

    public void setHomeScoreTip(String homeScoreTip) {
        this.homeScoreTip = homeScoreTip;
    }

    public String getGuestScoreTip() {
        return guestScoreTip;
    }

    public void setGuestScoreTip(String guestScoreTip) {
        this.guestScoreTip = guestScoreTip;
    }

    public Date getKickOff() {
        return kickOff;
    }

    public void setKickOff(Date kickOff) {
        this.kickOff = kickOff;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }


}
