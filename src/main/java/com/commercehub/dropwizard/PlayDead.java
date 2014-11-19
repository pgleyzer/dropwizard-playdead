package com.commercehub.dropwizard;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by awilson on 11/18/14.
 */
public class PlayDead {

    private File stateFile;

    private static final PlayDead INSTANCE = new PlayDead();

    private PlayDead() { }

    public static PlayDead getInstance() {
        return INSTANCE;
    }

    public void setLockFilePath(String path) {
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
