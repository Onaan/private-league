package de.draigon.waw.layouts;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import de.draigon.waw.data.Match;

import java.text.SimpleDateFormat;

/**
 * Created by IntelliJ IDEA.
 * User: Schnabel
 * Date: 06.06.12
 * Time: 11:16
 * To change this template use File | Settings | File Templates.
 */
public class MatchLayout extends TableLayout {
    private Context context;
    private static SimpleDateFormat sdf = new SimpleDateFormat("E',' dd.MM.yyyy HH:mm");
    private Match match;


    public MatchLayout(Context context, Match m) {
        super(context);
        this.context = context;
        this.match = m;
        TableLayout.LayoutParams tlp = new TableLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.WRAP_CONTENT);
        this.setLayoutParams(tlp);
        this.addView(createMatchDate());

        this.addView(createTableRow(m.getHomeTeam(), m.getHomeScore(), m.getHomeScoreTip()));
        this.addView(createTableRow(m.getGuestTeam(), m.getGuestScore(), m.getGuestScoreTip()));
        this.setColumnShrinkable(1, true);
        this.setColumnStretchable(1, true);

    }

    private TableRow createTableRow(final String team, final String score, final String bet) {
        TableRow r = new TableRow(context);

        r.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        TextView teamName = new TextView(context);
        teamName.setText(team);
        r.addView(teamName);
        TextView teamScore = new TextView(context);
        teamScore.setText(score + " (" + bet + ")");
        teamScore.setGravity(Gravity.RIGHT);
        r.addView(teamScore);

        return r;
    }

    private TextView createMatchDate() {
        TextView dateView = new TextView(context);
        dateView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        dateView.setText(sdf.format(match.getKickOff()));
        dateView.setTextColor(getDateColor());
        return dateView;
    }

    private int getDateColor() {
        if (match.isBettable()) {


            try {
                Integer.parseInt(match.getGuestScoreTip());
                Integer.parseInt(match.getHomeScoreTip());
                // Das Spiel ist getippt
                return Color.BLUE;
            } catch (IllegalArgumentException e) {
                // Das Spiel muss noch getippt werden
                return Color.RED;
            }
        }
        // das Spiel laeuft bzw ist beendet
        return Color.YELLOW;
    }


    public Match getMatch() {
        return match;
    }
}
