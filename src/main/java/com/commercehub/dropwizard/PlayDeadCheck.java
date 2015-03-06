package com.commercehub.dropwizard;

import com.codahale.metrics.health.HealthCheck;

public class PlayDeadCheck extends HealthCheck {
    private final PlayDead playDead;
    public PlayDeadCheck(PlayDeadConfiguration playDeadConfiguration) {
        playDead = new PlayDead(playDeadConfiguration);
    }

    @Override
    protected Result check() throws Exception {
        if (playDead.isPlayingDead()) {
            return Result.unhealthy("playDead in progress");
        }

        return Result.healthy("No playDead happening.  Server is running normally.");
    }
}
