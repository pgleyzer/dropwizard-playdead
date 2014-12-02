package com.commercehub.dropwizard;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class PlayDead {

    private File stateFile;

    public PlayDead(PlayDeadConfiguration playDeadConfiguration) {
        setStateFilePath(playDeadConfiguration.getStateFilePath());
    }

    public void setStateFilePath(String path) {
        stateFile = new File(path);
    }

    public boolean isPlayingDead() {
        return stateFile != null && stateFile.exists();
    }

    public void startPlayingDead() {
        if(!stateFile.exists()) {
            try {
                new FileOutputStream(stateFile).close();
            } catch (IOException ignore) { }
        }
        stateFile.setLastModified(System.currentTimeMillis());
    }

    public void stopPlayingDead() {
        stateFile.delete();
    }
}
