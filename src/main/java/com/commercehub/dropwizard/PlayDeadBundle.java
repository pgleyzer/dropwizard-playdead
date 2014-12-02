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

        environment.admin()
                .addServlet(playDeadConfiguration.getContextPath(), new PlayDeadServlet(playDead))
                .addMapping(playDeadConfiguration.getContextPath());
    }

    @Override
    public void initialize(Bootstrap<?> bootstrap) {
    }

    public abstract PlayDeadConfiguration getPlayDeadConfiguration(T config);
}
