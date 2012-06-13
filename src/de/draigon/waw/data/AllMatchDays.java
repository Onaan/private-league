package de.draigon.waw.data;

import java.io.*;
import java.util.ArrayList;
import java.util.List;


public class AllMatchDays extends ArrayList<MatchDay> implements Serializable {

    private void writeObject(final ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.write(this.size());
        for (final MatchDay m : this) {
            out.writeObject(m);

        }
    }

    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        for (int i = 0; i < in.readInt(); ++i) {
            this.add((MatchDay) (in.readObject()));
        }
    }

    private void readObjectNoData() throws ObjectStreamException {
    }

    public void updateMatch(Match match) {
        boolean finished = false;
        for (MatchDay matchDay : this) {
            List<Match> matches = matchDay.getMatches();
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


}
