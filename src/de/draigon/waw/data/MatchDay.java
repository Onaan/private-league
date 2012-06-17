package de.draigon.waw.data;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MatchDay implements Serializable {
// ------------------------------ FIELDS ------------------------------

    private final String name;
    private transient List<Match> matches;
// --------------------------- CONSTRUCTORS ---------------------------

    public MatchDay(final String name) {
        this.name = name;
    }
// --------------------- GETTER / SETTER METHODS ---------------------

    public List<Match> getMatches() {
        return this.matches;
    }

    public void setMatches(final List<Match> matches) {
        this.matches = matches;
    }

    public String getName() {
        return this.name;
    }
// -------------------------- OTHER METHODS --------------------------

    @SuppressWarnings({"UnusedDeclaration"})
    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.matches = new ArrayList<Match>();
        for (int i = 0; i < in.readInt(); ++i) {
            this.matches.add((Match) (in.readObject()));
        }
    }

    @SuppressWarnings({"UnusedDeclaration"})
    private void writeObject(final ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.write(this.matches.size());
        for (final Match m : this.matches) {
            out.writeObject(m);
        }
    }
}
