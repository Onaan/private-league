package de.draigon.waw.utils;

import de.draigon.waw.Match;
import de.draigon.waw.Spieltag;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Schnabel
 * Date: 08.06.12
 * Time: 11:17
 * To change this template use File | Settings | File Templates.
 */
public class SpieltagParser {


    public List<Spieltag> createSpielplan(Document xml, String username) {

        List<Spieltag> spieltage = new ArrayList<Spieltag>();

        NodeList rounds = xml.getElementsByTagName("round");
        for (int i = 0; i < rounds.getLength(); ++i) {
            spieltage.add(this.parseRound(rounds.item(i), username));
        }


        return spieltage;  //To change body of created methods use File | Settings | File Templates.
    }

    private Spieltag parseRound(Node round, String username) {
        Spieltag spieltag = new Spieltag(round.getAttributes().getNamedItem("name").getTextContent());
        List<Match> matches = new ArrayList<Match>();

        NodeList childs = round.getChildNodes();
        for (int i = 0; i < childs.getLength(); ++i) {
            if (Node.ELEMENT_NODE == childs.item(i).getNodeType()) {
                matches.add(this.parseMatch(childs.item(i), username));
            }
        }
        spieltag.setMatches(matches);
        return spieltag;
    }

    private Match parseMatch(Node matchNode, String username) {
        Match match = new Match();
        NamedNodeMap home = this.getTeamAttributes(matchNode, "home");
        NamedNodeMap guest = this.getTeamAttributes(matchNode, "guest");
        NamedNodeMap bet = this.getBet(matchNode, username);

        match.setGuestTeam(guest.getNamedItem("name").getTextContent());
        match.setGuestScore(guest.getNamedItem("goals").getTextContent());

        match.setHomeScore(home.getNamedItem("goals").getTextContent());

        match.setHomeTeam(home.getNamedItem("name").getTextContent());
        match.setId(matchNode.getAttributes().getNamedItem("id").getTextContent());
        match.setKickOff(new Date(Long.parseLong(matchNode.getAttributes().getNamedItem("kickoff").getTextContent()) * 1000));
        match.setBettable(Boolean.valueOf(matchNode.getAttributes().getNamedItem("bettable").getTextContent()));
        try {
            match.setGuestScoreTip(bet.getNamedItem("guest").getTextContent());
            match.setHomeScoreTip(bet.getNamedItem("home").getTextContent());
        } catch (NullPointerException e) {
            // hier wurde noch kein tip abgegeben, es wird "-" angezeigt
        }

        return match;  //To change body of created methods use File | Settings | File Templates.
    }

    private NamedNodeMap getTeamAttributes(Node matchNode, String team) {
        for (int i = 0; i < matchNode.getChildNodes().getLength(); ++i) {
            if (matchNode.getChildNodes().item(i).getNodeName().equals(team)) {
                return matchNode.getChildNodes().item(i).getAttributes();
            }
        }

        return null;  //To change body of created methods use File | Settings | File Templates.
    }

    private NamedNodeMap getBet(Node matchNode, String username) {


        for (int i = 0; i < matchNode.getChildNodes().getLength(); ++i) {
            if ("bets".equals(matchNode.getChildNodes().item(i).getNodeName())) {
                for (int j = 0; j < matchNode.getChildNodes().item(i).getChildNodes().getLength(); ++j) {
                    if ("bet".equals(matchNode.getChildNodes().item(i).getChildNodes().item(j).getNodeName())
                            && username.equals(matchNode.getChildNodes().item(i).getChildNodes().item(j).getAttributes().getNamedItem(PrefConstants.USERNAME).getTextContent())
                            ) {
                        return matchNode.getChildNodes().item(i).getChildNodes().item(j).getAttributes();
                    }
                }
            }
        }

        return null;  //To change body of created methods use File | Settings | File Templates.
    }
}
