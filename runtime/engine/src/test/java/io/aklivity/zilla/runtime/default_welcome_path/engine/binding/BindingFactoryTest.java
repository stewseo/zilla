package io.aklivity.zilla.runtime.default_welcome_path.engine.binding;

import io.aklivity.zilla.runtime.engine.Configuration;
import io.aklivity.zilla.runtime.engine.binding.Binding;
import io.aklivity.zilla.runtime.engine.binding.BindingFactory;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;

public class BindingFactoryTest {
    Logger logger = LoggerFactory.getLogger(BindingFactoryTest.class);

    @Test
    public void shouldLoadAndCreate() throws IOException
    {
        logger.debug("BindingFactoryTest ");

        Configuration config = new Configuration();

        BindingFactory factory = BindingFactory.instantiate();

        for (String type : List.of("kafka")) {
            Binding binding = factory.create(type, config);
            assertThat(binding, instanceOf(BindingFactory.class));
        }
    }
}
