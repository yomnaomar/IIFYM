package com.karimchehab.IIFYM.Activities.Settings;

/**
 * Created by Kareem on 27-May-17.
 */

public class ChangesSynced {
    private boolean synced = false;
    private ChangeListener listener;

    public ChangesSynced(boolean synced) {
        this.synced = synced;
    }

    public boolean isSynced() {
        return synced;
    }

    public void setSynced(boolean synced) {
        this.synced = synced;
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
