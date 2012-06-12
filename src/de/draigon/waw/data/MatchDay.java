package de.draigon.waw.data;


import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class MatchDay implements Serializable {
    private final String name;
    private transient List<Match> matches;


    private void writeObject(final ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.write(this.matches.size());
        for (final Match m : this.matches) {
            out.writeObject(m);

        }
    }

    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.matches = new ArrayList<Match>();
        for (int i = 0; i < in.readInt(); ++i) {
            this.matches.add((Match) (in.readObject()));
        }
    }

    private void readObjectNoData() throws ObjectStreamException {
    }

    public MatchDay(final String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public List<Match> getMatches() {
        return this.matches;
    }

    public void setMatches(final List<Match> matches) {
        this.matches = matches;
    }
}
