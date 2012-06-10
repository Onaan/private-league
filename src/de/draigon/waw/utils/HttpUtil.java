package de.draigon.waw.utils;

import android.util.Log;
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
    private static final String TAG = HttpUtil.class.getName();
    private static final BasicNameValuePair login = new BasicNameValuePair("login", "true");
    private final MatchDayParser matchDayParser = new MatchDayParser();

    public HttpUtil() {
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
            throw new RuntimeException();
        }
        Document xml = null;
        try {
            xml = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(
                    new InputSource(new StringReader(document))
            );
        } catch (SAXException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (ParserConfigurationException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        Log.v(TAG, document);
        return xml;
    }


    public List<MatchDay> getPlayingSchedule(final URI uri, final String username, final String password) throws ConnectException {
        final List<NameValuePair> formparams = new ArrayList<NameValuePair>();
        formparams.add(new BasicNameValuePair(PrefConstants.USERNAME, username));
        formparams.add(new BasicNameValuePair(PrefConstants.PASSWORD, password));
        final Document xml = doPost(uri, formparams);
        return this.matchDayParser.createSpielplan(xml, username);
    }


    public BetState uploadBet(final URI uri, final String username, final String password, final Match match) throws ConnectException {
        final List<NameValuePair> formparams = new ArrayList<NameValuePair>();
        formparams.add(new BasicNameValuePair(PrefConstants.USERNAME, username));
        formparams.add(new BasicNameValuePair(PrefConstants.PASSWORD, password));
        formparams.add(new BasicNameValuePair("tips", match.getId() + ":" + match.getHomeScoreBet() + ":" + match.getGuestScoreBet()));
        final Document xml = doPost(uri, formparams);
        return isBetPlacementSuccessful(xml);

    }

    private BetState isBetPlacementSuccessful(final Document xml) {
        final NodeList betNode = xml.getElementsByTagName("bet");
        return BetState.valueOf(betNode.item(0).getAttributes().getNamedItem("status").getTextContent());
    }

    public CharSequence[] getRankings(final URI uri, final String username, final String password) throws ConnectException {
        final List<NameValuePair> formparams = new ArrayList<NameValuePair>();
        formparams.add(new BasicNameValuePair(PrefConstants.USERNAME, username));
        formparams.add(new BasicNameValuePair(PrefConstants.PASSWORD, password));
        final Document xml = doPost(uri, formparams);
        return parseStandings(xml);
    }

    private CharSequence[] parseStandings(final Document xml) {
        final NodeList playerNodes = xml.getElementsByTagName("player");
        final CharSequence[] scores = new CharSequence[playerNodes.getLength()];
        for (int i = 0; i < playerNodes.getLength(); ++i) {
            scores[i] = playerNodes.item(i).getAttributes().getNamedItem(PrefConstants.USERNAME).getTextContent() + " " + playerNodes.item(i).getAttributes().getNamedItem("score").getTextContent() + " (" + playerNodes.item(i).getAttributes().getNamedItem("tempscore").getTextContent() + ")";

        }
        return scores;
    }

    public TeamBet getTeamBetData(final URI uri, final String username, final String password) throws ConnectException {
        final List<NameValuePair> formparams = new ArrayList<NameValuePair>();
        formparams.add(new BasicNameValuePair(PrefConstants.USERNAME, username));
        formparams.add(new BasicNameValuePair(PrefConstants.PASSWORD, password));
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

    private boolean isTeamBettable(final Document xml) {
        return false;  //To change body of created methods use File | Settings | File Templates.
    }

    public String getServerAppVersion(final URI uri) throws ConnectException {
        final Document result = doPost(uri, new ArrayList<NameValuePair>());
        return result.getElementsByTagName("waw").item(0).getAttributes().getNamedItem("version").getTextContent();
    }
}
