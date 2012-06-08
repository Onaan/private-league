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


public class MatchLayout extends TableLayout {
    private final Context context;
    private static final SimpleDateFormat sdf = new SimpleDateFormat("E',' dd.MM.yyyy HH:mm");
    private final Match match;


    public MatchLayout(final Context context, final Match m) {
        super(context);
        this.context = context;
        this.match = m;
        final TableLayout.LayoutParams tlp = new TableLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.WRAP_CONTENT);
        this.setLayoutParams(tlp);
        this.addView(createMatchDate());
        this.addView(createTableRow(m.getHomeTeam(), m.getHomeScore(), m.getHomeScoreTip()));
        this.addView(createTableRow(m.getGuestTeam(), m.getGuestScore(), m.getGuestScoreTip()));
        this.setColumnShrinkable(1, true);
        this.setColumnStretchable(1, true);

    }

    private TableRow createTableRow(final String team, final String score, final String bet) {
        final TableRow r = new TableRow(this.context);
        r.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        final TextView teamName = new TextView(this.context);
        teamName.setText(team);
        r.addView(teamName);
        final TextView teamScore = new TextView(this.context);
        teamScore.setText(score + " (" + bet + ")");
        teamScore.setGravity(Gravity.RIGHT);
        r.addView(teamScore);
        return r;
    }

    private TextView createMatchDate() {
        final TextView dateView = new TextView(this.context);
        dateView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        dateView.setText(sdf.format(this.match.getKickOff()));
        dateView.setTextColor(getDateColor());
        return dateView;
    }

    private int getDateColor() {
        if (this.match.isBettable()) {
            try {
                Integer.parseInt(this.match.getGuestScoreTip());
                Integer.parseInt(this.match.getHomeScoreTip());
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
        return this.match;
    }
}
