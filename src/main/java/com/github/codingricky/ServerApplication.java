package com.github.codingricky;

import com.hubspot.dropwizard.guice.GuiceBundle;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class ServerApplication extends Application<ServerConfiguration> {
	@Override
	public void initialize(Bootstrap<ServerConfiguration> bootstrap) {
		GuiceBundle<ServerConfiguration> guiceBundle = GuiceBundle
				.<ServerConfiguration> newBuilder()
				.addModule(new ServerModule())
				.setConfigClass(ServerConfiguration.class)
				.enableAutoConfig(getClass().getPackage().getName()).build();
		bootstrap.addBundle(guiceBundle);
	}

	@Override
	public void run(ServerConfiguration configuration, Environment environment)
			throws Exception {
	}

	public static void main(String[] args) throws Exception {
		new ServerApplication().run(args);
	}
}