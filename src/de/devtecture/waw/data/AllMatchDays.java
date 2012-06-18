package de.devtecture.waw.data;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class AllMatchDays extends ArrayList<MatchDay> implements Serializable {
// -------------------------- OTHER METHODS --------------------------

    @SuppressWarnings({"UnusedDeclaration"})
    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        for (int i = 0; i < in.readInt(); ++i) {
            this.add((MatchDay) (in.readObject()));
        }
    }

    public void updateMatch(final Match match) {
        boolean finished = false;
        for (final MatchDay matchDay : this) {
            final List<Match> matches = matchDay.getMatches();
            for (int i = 0; i < matches.size(); ++i) {
                if (matches.get(i).getId().equals(match.getId())) {
                    matches.remove(i);
                    matches.add(i, match);
                    finished = true;
                    break;
                }
            }
            if (finished) {
                break;
            }
        }
    }

    @SuppressWarnings({"UnusedDeclaration"})
    private void writeObject(final ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.write(this.size());
        for (final MatchDay m : this) {
            out.writeObject(m);
        }
    }
}
