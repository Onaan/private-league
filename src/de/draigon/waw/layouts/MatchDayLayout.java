package de.draigon.waw.layouts;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import de.draigon.waw.R;
import de.draigon.waw.data.Match;
import de.draigon.waw.data.MatchDay;

import java.util.List;


public class MatchDayLayout extends LinearLayout implements AdapterView.OnItemSelectedListener {
    private final OnClickListener listener;
    private final Context context;
    private Spinner spinner;
    private final List<MatchDay> matchDays;


    public MatchDayLayout(final Context context, final OnClickListener listener, final List<MatchDay> spieltage) {
        super(context);
        this.context = context;
        this.listener = listener;
        this.matchDays = spieltage;
        setUp();

    }

    private void setUp() {
        this.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
        this.setOrientation(VERTICAL);
        createSpinner();
        this.spinner.setOnItemSelectedListener(this);
        final CharSequence[] days = new CharSequence[this.matchDays.size()];
        for (int i = 0; i < this.matchDays.size(); ++i) {
            days[i] = this.matchDays.get(i).getName();
        }
        final ArrayAdapter<CharSequence> st = new ArrayAdapter<CharSequence>(this.context, android.R.layout.simple_spinner_item, days);
        this.spinner.setAdapter(st);
        updateMatchDayData(0);


    }

    private void updateMatchDayData(final int pos) {
        removeAllViews();
        this.addView(this.spinner);
        addLine();
        for (final Match m : this.matchDays.get(pos).getMatches()) {
            createMatch(m);

        }
    }

    private void createMatch(final Match m) {
        final MatchLayout t = new MatchLayout(this.context, m);
        t.setOnClickListener(this.listener);
        this.addView(t);
        addLine();


    }


    private void addLine() {
        final View v = new View(this.context);
        v.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, getPixels(2)));
        v.setBackgroundColor(0xFF909090);
        this.addView(v);
    }

    private int getPixels(final int dipValue) {
        final Resources r = getResources();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, r.getDisplayMetrics());
    }

    private void createSpinner() {
        this.spinner = new Spinner(this.context);
        final Spinner.LayoutParams slp = new Spinner.LayoutParams(Spinner.LayoutParams.FILL_PARENT, Spinner.LayoutParams.WRAP_CONTENT);
        this.spinner.setLayoutParams(slp);
        this.spinner.setPrompt(getResources().getString(R.string.spielplan_spinner_label));
    }

    public void onItemSelected(final AdapterView<?> adapterView, final View view, final int i, final long l) {
        updateMatchDayData(i);
    }

    public void onNothingSelected(final AdapterView<?> adapterView) {
    }
}

