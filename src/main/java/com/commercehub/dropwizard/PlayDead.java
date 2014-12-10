package com.commercehub.dropwizard;


import java.io.IOException;
import java.nio.file.Files;

public class PlayDead {

    private PlayDeadConfiguration config;

    public PlayDead(PlayDeadConfiguration playDeadConfiguration) {
        this.config = playDeadConfiguration;
    }

    public boolean isPlayingDead() {
        return config.getStateFilePath() != null && Files.exists(config.getStateFilePath());
    }

    public void startPlayingDead() throws IOException {
        if(!Files.exists(config.getStateFilePath())) {
            Files.createFile(config.getStateFilePath());
        }
    }

    public void stopPlayingDead() throws IOException {
        Files.deleteIfExists(config.getStateFilePath());
    }

    public PlayDeadConfiguration getConfig() {
        return config;
    }

    public void setConfig(PlayDeadConfiguration config) {
        this.config = config;
    }
}
