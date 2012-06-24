package de.draigon.waw.data;

import android.util.Log;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedList;

/**
 * Created by IntelliJ IDEA.
 * User: Schnabel
 * Date: 21.06.12
 * Time: 11:46
 * To change this template use File | Settings | File Templates.
 */
public class User implements Serializable {
// ------------------------------ FIELDS ------------------------------

    private static final String TAG = User.class.getName();
    private final String userName;
    private final String password;
    //private String activeGroup;
    private final LinkedList<String> groups = new LinkedList<String>();
// --------------------------- CONSTRUCTORS ---------------------------

    public User(final String userName, final String password) {
        this.userName = userName;
        this.password = password;
    }
// --------------------- GETTER / SETTER METHODS ---------------------

    public String getPassword() {
        return this.password;
    }

    public String getUserName() {
        return this.userName;
    }

    public boolean setActiveGroup(final String activeGroup) {
        Log.d(TAG, "groups: " + this.groups.toString() + " activeGroup: " + activeGroup);
        if (!this.groups.contains(activeGroup)) {
            return false;
        }
        final int j = this.groups.indexOf(activeGroup);
        for (int i = 0; i < j; ++i) {
            switchGroup();
        }
        return true;
    }

    public String setNewGroups(final Collection<String> newGroups) {
        this.groups.clear();
        Log.d(TAG, "setting groups to: " + newGroups);
        this.groups.addAll(newGroups);
        return getActiveGroup();
    }
// -------------------------- OTHER METHODS --------------------------

    public String getActiveGroup() {
        return this.groups.peek();
    }

    public int getNumberOfGroups() {
        return this.groups.size();
    }

    public String switchGroup() {
        this.groups.add(this.groups.poll());
        return this.groups.peek();
    }
}
