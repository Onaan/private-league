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
import de.draigon.waw.data.MatchDay;
import de.draigon.waw.data.Match;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Schnabel
 * Date: 05.06.12
 * Time: 15:10
 * To change this template use File | Settings | File Templates.
 */
public class MatchDayLayout extends LinearLayout implements AdapterView.OnItemSelectedListener {
    private OnClickListener listener;
    private Context context;
    private Spinner spinner;
    private List<MatchDay> spieltage;


    public MatchDayLayout(Context context, OnClickListener listener, List<MatchDay> spieltage) {
        super(context);
        this.context = context;
        this.listener = listener;
        this.spieltage = spieltage;
        setUp();

    }

    private void setUp() {
        this.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
        this.setOrientation(VERTICAL);
        createSpinner();

        spinner.setOnItemSelectedListener(this);


        CharSequence[] days = new CharSequence[spieltage.size()];
        for (int i = 0; i < spieltage.size(); ++i) {
            days[i] = spieltage.get(i).getName();
        }
        ArrayAdapter<CharSequence> st = new ArrayAdapter<CharSequence>(context, android.R.layout.simple_spinner_item, days);
        spinner.setAdapter(st);

        updateSpieltagData(0);


    }

    private void updateSpieltagData(int pos) {
        removeAllViews();
        this.addView(spinner);
        addLine();

        for (Match m : spieltage.get(pos).getMatches()) {
            createMatch(m);

        }
    }

    private void createMatch(Match m) {


        MatchLayout t = new MatchLayout(context, m);

        t.setOnClickListener(listener);
        this.addView(t);


        addLine();


    }


    private void addLine() {
        View v = new View(context);
        v.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, getPixels(2)));
        v.setBackgroundColor(0xFF909090);
        this.addView(v);
    }

    private int getPixels(int dipValue) {
        Resources r = getResources();
        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, r.getDisplayMetrics());
        return px;
    }

    private void createSpinner() {
        spinner = new Spinner(context);
        Spinner.LayoutParams slp = new Spinner.LayoutParams(Spinner.LayoutParams.FILL_PARENT, Spinner.LayoutParams.WRAP_CONTENT);
        spinner.setLayoutParams(slp);
        spinner.setPrompt(getResources().getString(R.string.spielplan_spinner_label));
    }

    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        updateSpieltagData(i);
    }

    public void onNothingSelected(AdapterView<?> adapterView) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}

