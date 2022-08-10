package io.aklivity.zilla.runtime.default_welcome_path.engine.test;

import io.aklivity.zilla.runtime.engine.Configuration;
import io.aklivity.zilla.runtime.engine.EngineContext;
import io.aklivity.zilla.runtime.engine.binding.Binding;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;

public class TestWelcomePathBinding implements Binding
{
    static final Logger logger = LoggerFactory.getLogger(TestWelcomePathBinding.class);
    TestWelcomePathBinding(
            Configuration config) {
        logger.debug("TestWelcomePathBinding being created with config: " + config);
    }

    @Override
    public String name() {
        return "test";
    }

    @Override
    public URL type() {
        return getClass().getResource("test.schema.patch.json");
    }

    @Override
    public TestWelcomePathBindingContext supply(
            EngineContext context)
    {
        return new TestWelcomePathBindingContext(context);
    }
}


