package de.draigon.waw.utils;

import android.util.Log;
import de.draigon.waw.Match;
import de.draigon.waw.Spieltag;
import de.draigon.waw.TeamBetData;
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
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Schnabel
 * Date: 07.06.12
 * Time: 11:16
 * To change this template use File | Settings | File Templates.
 */
public class WAWHttpClient {
    public static final String TAG = WAWHttpClient.class.getName();
    private static final BasicNameValuePair login = new BasicNameValuePair("login", "true");
    private SpieltagParser spielTagParser = new SpieltagParser();

    public WAWHttpClient() {


    }

    private Document doPost(URI uri, List<NameValuePair> params) {
        params.add(login);

        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(uri);
        HttpResponse response = null;
        String document = null;
        try {
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, "ISO8859_1");         //
            post.setEntity(entity);


            response = client.execute(post);
            document = EntityUtils.toString(response.getEntity());
        } catch (IOException e) {
            Log.e(TAG, "Error getting data", e);
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
        Log.d(TAG, document);
        return xml;
    }


    public List<Spieltag> getPlayingSchedule(URI uri, String username, String password) {


        List<NameValuePair> formparams = new ArrayList<NameValuePair>();
        formparams.add(new BasicNameValuePair(PrefConstants.USERNAME, username));
        formparams.add(new BasicNameValuePair(PrefConstants.PASSWORD, password));
        Document xml = doPost(uri, formparams);

        return spielTagParser.createSpielplan(xml, username);
    }


    public Boolean uploadBet(URI uri, String username, String password, Match match) {
        List<NameValuePair> formparams = new ArrayList<NameValuePair>();
        formparams.add(new BasicNameValuePair(PrefConstants.USERNAME, username));
        formparams.add(new BasicNameValuePair(PrefConstants.PASSWORD, password));
        formparams.add(new BasicNameValuePair("tips", match.getId() + ":" + match.getHomeScoreTip() + ":" + match.getGuestScoreTip()));
        Document xml = doPost(uri, formparams);

        return true;

    }

    public CharSequence[] getRankings(URI uri, String username, String password) {
        List<NameValuePair> formparams = new ArrayList<NameValuePair>();
        formparams.add(new BasicNameValuePair(PrefConstants.USERNAME, username));
        formparams.add(new BasicNameValuePair(PrefConstants.PASSWORD, password));
        Document xml = doPost(uri, formparams);

        return parseStandings(xml);
    }

    private CharSequence[] parseStandings(Document xml) {
        NodeList playerNodes = xml.getElementsByTagName("player");
        CharSequence[] scores = new CharSequence[playerNodes.getLength()];
        for (int i = 0; i < playerNodes.getLength(); ++i) {
            scores[i] = playerNodes.item(i).getAttributes().getNamedItem(PrefConstants.USERNAME).getTextContent() + " " + playerNodes.item(i).getAttributes().getNamedItem("score").getTextContent() + " (" + playerNodes.item(i).getAttributes().getNamedItem("tempscore").getTextContent() + ")";

        }
        return scores;
    }

    public TeamBetData getTeamBetData(URI uri, String username, String password) {
        List<NameValuePair> formparams = new ArrayList<NameValuePair>();
        formparams.add(new BasicNameValuePair(PrefConstants.USERNAME, username));
        formparams.add(new BasicNameValuePair(PrefConstants.PASSWORD, password));
        Document xml = doPost(uri, formparams);
        return parseTeamBetData(xml);
    }

    private TeamBetData parseTeamBetData(Document xml) {
        NodeList playerNodes = xml.getElementsByTagName("team");
        CharSequence[] teams = new CharSequence[playerNodes.getLength()];
        for (int i = 0; i < playerNodes.getLength(); ++i) {
            teams[i] = playerNodes.item(i).getAttributes().getNamedItem("name").getTextContent();

        }
        TeamBetData tbd = new TeamBetData();
        Arrays.sort(teams);
        tbd.setChoices(teams);
        tbd.setBettable(isTeamBettable(xml));
        tbd.setSelected("");
        return tbd;

    }

    private boolean isTeamBettable(Document xml) {
        return false;  //To change body of created methods use File | Settings | File Templates.
    }
}
