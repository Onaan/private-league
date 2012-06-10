package de.draigon.waw.utils;

import de.draigon.waw.data.Match;
import de.draigon.waw.data.MatchDay;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MatchDayParser {


    public List<MatchDay> createSpielplan(final Document xml, final String username) {
        final List<MatchDay> matchDays = new ArrayList<MatchDay>();
        final NodeList rounds = xml.getElementsByTagName("round");
        for (int i = 0; i < rounds.getLength(); ++i) {
            matchDays.add(this.parseRound(rounds.item(i), username));
        }
        return matchDays;
    }

    private MatchDay parseRound(final Node round, final String username) {
        final MatchDay spieltag = new MatchDay(round.getAttributes().getNamedItem("name").getTextContent());
        final List<Match> matches = new ArrayList<Match>();
        final NodeList childs = round.getChildNodes();
        for (int i = 0; i < childs.getLength(); ++i) {
            if (Node.ELEMENT_NODE == childs.item(i).getNodeType()) {
                matches.add(this.parseMatch(childs.item(i), username));
            }
        }
        spieltag.setMatches(matches);
        return spieltag;
    }

    private Match parseMatch(final Node matchNode, final String username) {
        final Match match = new Match();
        final NamedNodeMap home = this.getTeamAttributes(matchNode, "home");
        final NamedNodeMap guest = this.getTeamAttributes(matchNode, "guest");
        final NamedNodeMap bet = this.getBet(matchNode, username);
        populateBetList(matchNode, match);
        match.setGuest(guest.getNamedItem("name").getTextContent());
        match.setGuestScore(guest.getNamedItem("goals").getTextContent());
        match.setHomeScore(home.getNamedItem("goals").getTextContent());
        match.setHome(home.getNamedItem("name").getTextContent());
        match.setId(matchNode.getAttributes().getNamedItem("id").getTextContent());
        match.setKickOff(new Date(Long.parseLong(matchNode.getAttributes().getNamedItem("kickoff").getTextContent()) * 1000));
        match.setBettable(Boolean.valueOf(matchNode.getAttributes().getNamedItem("bettable").getTextContent()));
        try {
            match.setGuestScoreBet(bet.getNamedItem("guest").getTextContent());
            match.setHomeScoreBet(bet.getNamedItem("home").getTextContent());
        } catch (NullPointerException e) {
            // hier wurde noch kein tip abgegeben, es wird "-" angezeigt
        }
        return match;
    }

    private void populateBetList(final Node matchNode, final Match match) {
        final List<CharSequence> bets = match.getBets();
        for (int i = 0; i < matchNode.getChildNodes().getLength(); ++i) {
            if ("bets".equals(matchNode.getChildNodes().item(i).getNodeName())) {
                for (int j = 0; j < matchNode.getChildNodes().item(i).getChildNodes().getLength(); ++j) {
                    if ("bet".equals(matchNode.getChildNodes().item(i).getChildNodes().item(j).getNodeName())) {
                        final NamedNodeMap attributes = matchNode.getChildNodes().item(i).getChildNodes().item(j).getAttributes();
                        final String bet = attributes.getNamedItem("username").getTextContent() + " " + attributes.getNamedItem("home").getTextContent() + ":" + attributes.getNamedItem("guest").getTextContent();
                        bets.add(bet);

                    }
                }
            }
        }
    }

    private NamedNodeMap getTeamAttributes(final Node matchNode, final String team) {
        for (int i = 0; i < matchNode.getChildNodes().getLength(); ++i) {
            if (matchNode.getChildNodes().item(i).getNodeName().equals(team)) {
                return matchNode.getChildNodes().item(i).getAttributes();
            }
        }
        return null;
    }

    private NamedNodeMap getBet(final Node matchNode, final String username) {
        for (int i = 0; i < matchNode.getChildNodes().getLength(); ++i) {
            if ("bets".equals(matchNode.getChildNodes().item(i).getNodeName())) {
                for (int j = 0; j < matchNode.getChildNodes().item(i).getChildNodes().getLength(); ++j) {
                    if ("bet".equals(matchNode.getChildNodes().item(i).getChildNodes().item(j).getNodeName())
                            && username.equalsIgnoreCase(matchNode.getChildNodes().item(i).getChildNodes().item(j).getAttributes().getNamedItem(PrefConstants.USERNAME).getTextContent())
                            ) {
                        return matchNode.getChildNodes().item(i).getChildNodes().item(j).getAttributes();
                    }
                }
            }
        }
        return null;
    }
}
