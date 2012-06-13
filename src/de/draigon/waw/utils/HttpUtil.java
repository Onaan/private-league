package de.draigon.waw.utils;

import android.util.Log;
import de.draigon.waw.Constants;
import de.draigon.waw.data.Match;
import de.draigon.waw.data.MatchDay;
import de.draigon.waw.data.TeamBet;
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
import java.util.List;


public class HttpUtil {
// ------------------------------ FIELDS ------------------------------

    private static final BasicNameValuePair login = new BasicNameValuePair("login", "true");
    private static final String TAG = HttpUtil.class.getName();
    private final MatchDayParser matchDayParser = new MatchDayParser();
// --------------------------- CONSTRUCTORS ---------------------------

    public HttpUtil() {
    }
// -------------------------- OTHER METHODS --------------------------

    private BetState isBetPlacementSuccessful(final Document xml) {
        final NodeList betNode = xml.getElementsByTagName("bet");
        return BetState.valueOf(betNode.item(0).getAttributes().getNamedItem("status").getTextContent());
    }

    public BetState uploadBet(final URI uri, final String username, final String password, final Match match) throws ConnectException {
        final List<NameValuePair> formparams = new ArrayList<NameValuePair>();
        formparams.add(new BasicNameValuePair(Constants.USERNAME, username));
        formparams.add(new BasicNameValuePair(Constants.PASSWORD, password));
        formparams.add(new BasicNameValuePair("tips", match.getId() + ":" + match.getHomeScoreBet() + ":" + match.getGuestScoreBet()));
        final Document xml = doPost(uri, formparams);
        return isBetPlacementSuccessful(xml);
    }

    public CharSequence[] getRankings(final URI uri, final String username, final String password) throws ConnectException {
        final List<NameValuePair> formparams = new ArrayList<NameValuePair>();
        formparams.add(new BasicNameValuePair(Constants.USERNAME, username));
        formparams.add(new BasicNameValuePair(Constants.PASSWORD, password));
        final Document xml = doPost(uri, formparams);
        return parseStandings(xml);
    }

    private CharSequence[] parseStandings(final Document xml) {
        final NodeList playerNodes = xml.getElementsByTagName("player");
        final CharSequence[] scores = new CharSequence[playerNodes.getLength()];
        for (int i = 0; i < playerNodes.getLength(); ++i) {
            scores[i] = playerNodes.item(i).getAttributes().getNamedItem(Constants.USERNAME).getTextContent() + " " + playerNodes.item(i).getAttributes().getNamedItem("score").getTextContent() + " (" + playerNodes.item(i).getAttributes().getNamedItem("tempscore").getTextContent() + ")";
        }
        return scores;
    }

    private Document doPost(final URI uri, final List<NameValuePair> params) throws ConnectException {
        params.add(login);
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
        Document xml;
        try {
            xml = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(
                    new InputSource(new StringReader(document))
            );
        } catch (SAXException e) {
            Log.e(TAG, "Error getting data", e);
            throw new RuntimeException(e);
        } catch (IOException e) {
            Log.e(TAG, "Error getting data", e);
            throw new RuntimeException(e);
        } catch (ParserConfigurationException e) {
            Log.e(TAG, "Error getting data", e);
            throw new RuntimeException(e);
        }
        //Log.v(TAG, document);
        return xml;
    }

    public List<MatchDay> getPlayingSchedule(final URI uri, final String username, final String password) throws ConnectException {
        final List<NameValuePair> formparams = new ArrayList<NameValuePair>();
        formparams.add(new BasicNameValuePair(Constants.USERNAME, username));
        formparams.add(new BasicNameValuePair(Constants.PASSWORD, password));
        final Document xml = doPost(uri, formparams);
        return this.matchDayParser.createSpielplan(xml, username);
    }

    /**
     * Gets a single {@link Match} with the corresponding id.
     *
     * @param uri      URI of the server
     * @param matchId  Id of the match to get
     * @param username Users name on the server
     * @param password Users password on the server
     * @return The corresponding match, or null if no match is found
     * @throws ConnectException If there is no internet or no server available
     */
    public Match getSingleMatch(final URI uri, final String matchId, final String username, final String password) throws ConnectException {
        if (matchId != null) {
            final List<MatchDay> allMatches = getPlayingSchedule(uri, username, password);
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

    public String getServerAppVersion(final URI uri) throws ConnectException {
        final Document result = doPost(uri, new ArrayList<NameValuePair>());
        return result.getElementsByTagName("waw").item(0).getAttributes().getNamedItem("version").getTextContent();
    }

    public TeamBet getTeamBetData(final URI uri, final String username, final String password) throws ConnectException {
        final List<NameValuePair> formparams = new ArrayList<NameValuePair>();
        formparams.add(new BasicNameValuePair(Constants.USERNAME, username));
        formparams.add(new BasicNameValuePair(Constants.PASSWORD, password));
        final Document xml = doPost(uri, formparams);
        return parseTeamBetData(xml);
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

    @SuppressWarnings({"UnusedParameters"})
    private boolean isTeamBettable(final Document xml) {
        //TODO: Implement
        return false;
    }
}
