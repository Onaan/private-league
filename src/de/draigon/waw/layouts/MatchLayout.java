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
    public static final String TAG = MatchLayout.class.getName();

    public MatchLayout(final Context context, final Match m) {
        super(context);
        this.context = context;
        this.match = m;
        drawView();
        this.setColumnShrinkable(1, true);
        this.setColumnStretchable(1, true);

    }

    private void drawView() {
        final LayoutParams tlp = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
        this.setLayoutParams(tlp);
        this.addView(createMatchDate());
        this.addView(createTableRow(this.match.getHome(), this.match.getHomeScore(), this.match.getHomeTempScore(), this.match.getHomeScoreBet()));
        this.addView(createTableRow(this.match.getGuest(), this.match.getGuestScore(), this.match.getGuestTempScore(), this.match.getGuestScoreBet()));
    }

    private TableRow createTableRow(final String team, final String score, final String tempScore, final String bet) {
        final TableRow r = new TableRow(this.context);
        r.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        final TextView teamName = new TextView(this.context);
        teamName.setText(team);
        r.addView(teamName);
        final TextView teamScore = new TextView(this.context);
        final String showScore;
        if ("-".equals(score) && !"".equals(tempScore)) {
            showScore = tempScore;
            teamScore.setTextColor(Color.YELLOW);
        } else {
            showScore = score;
        }
        teamScore.setText(showScore + " (" + bet + ")");
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
                Integer.parseInt(this.match.getGuestScoreBet());
                Integer.parseInt(this.match.getHomeScoreBet());
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
