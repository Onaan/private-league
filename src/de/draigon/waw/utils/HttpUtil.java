package de.draigon.waw.utils;

import android.util.Log;
import de.draigon.waw.data.Match;
import de.draigon.waw.data.MatchDay;
import de.draigon.waw.data.TeamBet;
import de.draigon.waw.data.User;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.net.ConnectException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static de.draigon.waw.Constants.*;


public class HttpUtil {
// ------------------------------ FIELDS ------------------------------

    private static final String TAG = HttpUtil.class.getName();
    private final MatchDayParser matchDayParser = new MatchDayParser();
// --------------------------- CONSTRUCTORS ---------------------------

    public HttpUtil() {
    }
// -------------------------- OTHER METHODS --------------------------

    private Document doPost(final URI uri, final List<NameValuePair> params) throws ConnectException {
        Log.v(TAG, params.toString());
        final HttpClient client = new DefaultHttpClient();
        final HttpPost post = new HttpPost(uri);
        final String document;
        try {
            final UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, "ISO8859_1");
            post.setEntity(entity);
            final HttpResponse response = client.execute(post);
            document = EntityUtils.toString(response.getEntity());
        } catch (ConnectException e) {
            throw e;
        } catch (IOException e) {
            Log.e(TAG, "Error getting data", e);
            throw new RuntimeException(e);
        }
        Log.v(TAG, document);
        final Document xml;
        try {
            xml = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(
                    new InputSource(new StringReader(document))
            );
        } catch (SAXException e) {
            Log.e(TAG, "Error getting data", e);
            throw new ConnectException("Error getting data");
        } catch (IOException e) {
            Log.e(TAG, "Error getting data", e);
            throw new ConnectException("Error getting data");
        } catch (ParserConfigurationException e) {
            Log.e(TAG, "Error getting data", e);
            throw new ConnectException("Error getting data");
        }
        return xml;
    }

    public CharSequence[] getGroups(final URI uri, final String username, final String password) throws ConnectException {
        final List<NameValuePair> formparams = new ArrayList<NameValuePair>();
        formparams.add(new BasicNameValuePair(USERNAME, username));
        formparams.add(new BasicNameValuePair(PASSWORD, password));
        formparams.add(new BasicNameValuePair(COMMAND, COMMAND_GROUPS));
        final Document xml = doPost(uri, formparams);
        return parseGroups(xml);
    }

    public List<MatchDay> getPlayingSchedule(final URI uri, final User user) throws ConnectException {
        final List<NameValuePair> formparams = new ArrayList<NameValuePair>();
        formparams.add(new BasicNameValuePair(USERNAME, user.getUserName()));
        formparams.add(new BasicNameValuePair(PASSWORD, user.getPassword()));
        formparams.add(new BasicNameValuePair(GROUP, user.getActiveGroup()));
        formparams.add(new BasicNameValuePair(COMMAND, COMMAND_MATCHES + ":" + COMMAND_GROUPS));
        final Document xml = doPost(uri, formparams);
        final String screenName = xml.getElementsByTagName("waw").item(0).getAttributes().getNamedItem("username").getTextContent();
        updateUserGroups(xml, user);
        return this.matchDayParser.createSpielplan(xml, screenName);
    }

    public CharSequence[] getRankings(final URI uri, final User user) throws ConnectException {
        final List<NameValuePair> formparams = new ArrayList<NameValuePair>();
        formparams.add(new BasicNameValuePair(USERNAME, user.getUserName()));
        formparams.add(new BasicNameValuePair(PASSWORD, user.getPassword()));
        formparams.add(new BasicNameValuePair(GROUP, user.getActiveGroup()));
        formparams.add(new BasicNameValuePair(COMMAND, COMMAND_HIGHSCORES + ":" + COMMAND_GROUPS));
        final Document xml = doPost(uri, formparams);
        updateUserGroups(xml, user);
        return parseStandings(xml);
    }

    public String getServerAppVersion(final URI uri) throws ConnectException {
        final List<NameValuePair> formparams = new ArrayList<NameValuePair>();
        formparams.add(new BasicNameValuePair(COMMAND, COMMAND_VERSION));
        final Document result = doPost(uri, formparams);
        return result.getElementsByTagName("waw").item(0).getAttributes().getNamedItem("version").getTextContent();
    }

    /**
     * Gets a single {@link Match} with the corresponding id.
     *
     * @param uri     URI of the server
     * @param matchId Id of the match to get
     * @return The corresponding match, or null if no match is found
     * @throws ConnectException If there is no internet or no server available
     */
    public Match getSingleMatch(final URI uri, final String matchId, final User user) throws ConnectException {
        if (matchId != null) {
            final List<MatchDay> allMatches = getPlayingSchedule(uri, user);
            for (final MatchDay day : allMatches) {
                for (final Match match : day.getMatches()) {
                    if (matchId.equals(match.getId())) {
                        return match;
                    }
                }
            }
        }
        return null;
    }

    public TeamBet getTeamBetData(final URI uri, final String username, final String password) throws ConnectException {
        final List<NameValuePair> formparams = new ArrayList<NameValuePair>();
        formparams.add(new BasicNameValuePair(USERNAME, username));
        formparams.add(new BasicNameValuePair(PASSWORD, password));
        final Document xml = doPost(uri, formparams);
        return parseTeamBetData(xml);
    }

    private BetState isBetPlacementSuccessful(final Document xml) {
        final NodeList betNode = xml.getElementsByTagName("bet");
        return BetState.valueOf(betNode.item(0).getAttributes().getNamedItem("status").getTextContent());
    }

    @SuppressWarnings({"UnusedParameters", "SameReturnValue"})
    private boolean isTeamBettable(final Document xml) {
        //TODO: Implement
        return false;
    }

    private CharSequence[] parseGroups(final Document xml) {
        final NodeList groupNodes = xml.getElementsByTagName("group");
        final CharSequence[] groups = new CharSequence[groupNodes.getLength()];
        for (int i = 0; i < groupNodes.getLength(); ++i) {
            groups[i] = groupNodes.item(i).getAttributes().getNamedItem("name").getTextContent();
        }
        Arrays.sort(groups);
        return groups;
    }

    private CharSequence[] parseStandings(final Document xml) {
        final NodeList playerNodes = xml.getElementsByTagName("player");
        final CharSequence[] scores = new CharSequence[playerNodes.getLength()];
        for (int i = 0; i < playerNodes.getLength(); ++i) {
            scores[i] = playerNodes.item(i).getAttributes().getNamedItem(USERNAME).getTextContent() + " " + playerNodes.item(i).getAttributes().getNamedItem("score").getTextContent() + " (" + playerNodes.item(i).getAttributes().getNamedItem("tempscore").getTextContent() + ")";
        }
        return scores;
    }

    private TeamBet parseTeamBetData(final Document xml) {
        final NodeList playerNodes = xml.getElementsByTagName("team");
        final CharSequence[] teams = new CharSequence[playerNodes.getLength()];
        for (int i = 0; i < playerNodes.getLength(); ++i) {
            teams[i] = playerNodes.item(i).getAttributes().getNamedItem("name").getTextContent();
        }
        final TeamBet teamBet = new TeamBet();
        Arrays.sort(teams);
        teamBet.setChoices(teams);
        teamBet.setBettable(isTeamBettable(xml));
        teamBet.setSelected("");
        return teamBet;
    }

    private void updateUserGroups(final Document xml, final User user) {
        final String activeGroup = xml.getElementsByTagName("waw").item(0).getAttributes().getNamedItem("selectedGroup").getTextContent();
        final NodeList groupNodes = xml.getElementsByTagName("group");
        final Collection<String> groups = new ArrayList<String>();
        for (int i = 0; i < groupNodes.getLength(); ++i) {
            groups.add(groupNodes.item(i).getAttributes().getNamedItem("name").getTextContent());
        }
        user.setNewGroups(groups);
        user.setActiveGroup(activeGroup);
    }

    public BetState uploadBet(final URI uri, final User user, final Match match) throws ConnectException {
        final List<NameValuePair> formparams = new ArrayList<NameValuePair>();
        formparams.add(new BasicNameValuePair(USERNAME, user.getUserName()));
        formparams.add(new BasicNameValuePair(PASSWORD, user.getPassword()));
        formparams.add(new BasicNameValuePair("tips", match.getId() + ":" + match.getHomeScoreBet() + ":" + match.getGuestScoreBet()));
        final Document xml = doPost(uri, formparams);
        return isBetPlacementSuccessful(xml);
    }
}
