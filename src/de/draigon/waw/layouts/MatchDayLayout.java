package de.draigon.waw.layouts;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import de.draigon.waw.R;
import de.draigon.waw.data.AllMatchDays;
import de.draigon.waw.data.Match;
import de.draigon.waw.data.MatchDay;

import java.util.List;

import static de.draigon.waw.Constants.MATCH_DAY;
import static de.draigon.waw.Constants.PREFS_NAME;


public class MatchDayLayout extends LinearLayout implements AdapterView.OnItemSelectedListener {
// ------------------------------ FIELDS ------------------------------

    private static final String TAG = MatchDayLayout.class.getCanonicalName();
    private final Context context;


    private final List<MatchDay> matchDays;
    private final OnClickListener listener;
    private final SharedPreferences prefs;
    private Spinner spinner;
// --------------------------- CONSTRUCTORS ---------------------------

    public MatchDayLayout(final Context context, final OnClickListener listener, final List<MatchDay> matchDays) {
        super(context);
        this.context = context;
        this.listener = listener;
        this.matchDays = matchDays;
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        refresh();
    }
// ------------------------ INTERFACE METHODS ------------------------
// --------------------- Interface OnItemSelectedListener ---------------------

    @Override
    public void onItemSelected(final AdapterView<?> adapterView, final View view, final int selected, final long l) {
        updateMatchDayData(selected);
    }

    @Override
    public void onNothingSelected(final AdapterView<?> adapterView) {
    }
// -------------------------- OTHER METHODS --------------------------

    private void addLine() {
        Log.v(TAG, "adding line");
        final View v = new View(this.context);
        v.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, getPixels(2)));
        v.setBackgroundColor(0xFF909090);
        this.addView(v);
    }

    private void createMatch(final Match m) {
        Log.v(TAG, "creating match with id: " + m.getId());
        final MatchLayout t = new MatchLayout(this.context, m);
        t.setOnClickListener(this.listener);
        this.addView(t);
        addLine();
    }

    private void createSpinner() {
        Log.v(TAG, "creating spinner");
        this.spinner = new Spinner(this.context);
        final Spinner.LayoutParams slp = new Spinner.LayoutParams(Spinner.LayoutParams.FILL_PARENT, Spinner.LayoutParams.WRAP_CONTENT);
        this.spinner.setLayoutParams(slp);
        this.spinner.setPrompt(getResources().getString(R.string.spielplan_spinner_label));
    }

    public AllMatchDays getMatchDays() {
        final AllMatchDays am = new AllMatchDays();
        am.addAll(this.matchDays);
        return am;
    }

    @SuppressWarnings({"SameParameterValue"})
    private int getPixels(final int dipValue) {
        final Resources r = getResources();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, r.getDisplayMetrics());
    }

    public void refresh() {
        this.removeAllViews();
        Log.d(TAG, "refreshing Layout");
        final int startPosition = this.prefs.getInt(MATCH_DAY, 0);
        this.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));
        this.setOrientation(VERTICAL);
        createSpinner();
        this.spinner.setOnItemSelectedListener(this);
        final CharSequence[] days = new CharSequence[this.matchDays.size()];
        for (int i = 0; i < this.matchDays.size(); ++i) {
            days[i] = this.matchDays.get(i).getName();
        }
        final ArrayAdapter<CharSequence> matchDayAdapter = new ArrayAdapter<CharSequence>(this.context, android.R.layout.simple_spinner_item, days);
        this.spinner.setAdapter(matchDayAdapter);
        this.spinner.setSelection(startPosition);
        updateMatchDayData(startPosition);
    }

    private void updateMatchDayData(final int startPosition) {
        Log.v(TAG, "updating match day data for day " + startPosition);
        removeAllViews();
        this.addView(this.spinner);
        addLine();
        for (final Match m : this.matchDays.get(startPosition).getMatches()) {
            createMatch(m);
        }
        this.prefs.edit().putInt(MATCH_DAY, startPosition).commit();
    }
}

