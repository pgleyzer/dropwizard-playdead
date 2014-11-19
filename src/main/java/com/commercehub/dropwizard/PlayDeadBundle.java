package com.commercehub.dropwizard;

import io.dropwizard.Configuration;
import io.dropwizard.ConfiguredBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

/**
 * Created by awilson on 11/18/14.
 */
public abstract class PlayDeadBundle<T extends Configuration> implements ConfiguredBundle<T> {
    @Override
    public void run(T config, Environment environment) throws Exception {
        PlayDead playDead = PlayDead.getInstance();
        playDead.setLockFilePath(getStateFilePath(config));
        environment.admin()
                .addServlet(getContextPath(config), new PlayDeadServlet())
                .addMapping(getContextPath(config));
    }

    @Override
    public void initialize(Bootstrap<?> bootstrap) {
    }

    public abstract String getStateFilePath(T config);
    public abstract String getContextPath(T config);
}
