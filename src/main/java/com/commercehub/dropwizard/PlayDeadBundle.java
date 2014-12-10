package com.commercehub.dropwizard;

import io.dropwizard.Configuration;
import io.dropwizard.ConfiguredBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public abstract class PlayDeadBundle<T extends Configuration> implements ConfiguredBundle<T> {
    @Override
    public void run(T config, Environment environment) throws Exception {
        PlayDeadConfiguration playDeadConfiguration = getPlayDeadConfiguration(config);
        PlayDead playDead = new PlayDead(playDeadConfiguration);

        if (playDeadConfiguration.getEnvironment().equals(PlayDeadConfiguration.ADMIN_ENVIRONMENT)) {
            environment.admin()
                    .addServlet(playDeadConfiguration.getContextPath(), new PlayDeadServlet(playDead))
                    .addMapping(playDeadConfiguration.getContextPath());
        } else if (playDeadConfiguration.getEnvironment().equals(PlayDeadConfiguration.APPLICATION_ENVIRONMENT)) {
            environment.servlets()
                    .addServlet(playDeadConfiguration.getContextPath(), new PlayDeadServlet(playDead))
                    .addMapping(playDeadConfiguration.getContextPath());
        } else {
            throw new Exception("PlayDeadConfiguration Environment invalid: "+playDeadConfiguration.getEnvironment());
        }
    }

    @Override
    public void initialize(Bootstrap<?> bootstrap) {
    }

    public abstract PlayDeadConfiguration getPlayDeadConfiguration(T config);
}
