package io.aklivity.zilla.runtime.default_welcome_path.engine.test;

import io.aklivity.zilla.runtime.engine.EngineContext;
import io.aklivity.zilla.runtime.engine.binding.BindingContext;
import io.aklivity.zilla.runtime.engine.binding.BindingHandler;
import io.aklivity.zilla.runtime.engine.config.BindingConfig;


public class TestWelcomePathBindingContext implements BindingContext
{
    private final TestWelcomePathBindingFactory factory;

    TestWelcomePathBindingContext(
            EngineContext context)
    {
        factory = new TestWelcomePathBindingFactory(context);
    }

    @Override
    public BindingHandler attach(
            BindingConfig binding)
    {
        factory.attach(binding);
        return factory;
    }

    @Override
    public void detach(
            BindingConfig binding)
    {
        factory.detach(binding);
    }
}