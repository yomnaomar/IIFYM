package com.karimchehab.IIFYM.Activities.Settings;

/**
 * Created by Kareem on 27-May-17.
 */

public class changesMade {
    private boolean changed = false;
    private ChangeListener listener;

    public changesMade(boolean changed) {
        this.changed = changed;
    }

    public boolean isChanged() {
        return changed;
    }

    public void setChanged(boolean changed) {
        this.changed = changed;
        if (listener != null) listener.onChange();
    }

    public ChangeListener getListener() {
        return listener;
    }

    public void setListener(ChangeListener listener) {
        this.listener = listener;
    }

    public interface ChangeListener {
        void onChange();
    }
}
