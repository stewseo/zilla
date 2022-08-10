package io.aklivity.zilla.runtime.engine.internal.config.default_welcome_path;

import io.aklivity.zilla.runtime.engine.config.OptionsConfig;
import io.aklivity.zilla.runtime.engine.config.OptionsConfigAdapterSpi;
import io.aklivity.zilla.runtime.engine.internal.config.OptionsAdapter;
import io.aklivity.zilla.runtime.engine.test.internal.binding.config.TestBindingOptionsConfig;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.equalTo;

public class OptionsConfigAdapterTest {
        private OptionsAdapter adapter;
        private Jsonb jsonb;

        @Before
        public void initJson()
        {
            adapter = new OptionsAdapter(OptionsConfigAdapterSpi.Kind.BINDING);
            adapter.adaptType("test");
            JsonbConfig config = new JsonbConfig()
                    .withAdapters(adapter);
            jsonb = JsonbBuilder.create(config);
        }

        @Test
        public void shouldReadOptions()
        {
            String text =
                    "{" +
                            "\"mode\": \"test\"" +
                            "}";

            TestBindingOptionsConfig options = (TestBindingOptionsConfig) jsonb.fromJson(text, OptionsConfig.class);

            assertThat(options, not(nullValue()));
            assertThat(options.mode, equalTo("test"));
        }

        @Test
        public void shouldWriteOptions()
        {
            OptionsConfig options = new TestBindingOptionsConfig("test_welcome-path");

            String text = jsonb.toJson(options);

            assertThat(text, not(nullValue()));
            assertThat(text, equalTo("{\"mode\":\"test_welcome-path\"}"));
        }

        @Test
        public void shouldReadNullWhenNotAdapting()
        {
            String text =
                    "{" +
                            "\"mode\": \"test_welcome-path\"" +
                            "}";

            adapter.adaptType(null);
            TestBindingOptionsConfig options = (TestBindingOptionsConfig) jsonb.fromJson(text, OptionsConfig.class);

            assertThat(options, nullValue());
        }

        @Test
        public void shouldWriteNullWhenNotAdapting()
        {
            OptionsConfig options = new TestBindingOptionsConfig("test_welcome-path");

            adapter.adaptType(null);
            String text = jsonb.toJson(options);

            assertThat(text, not(nullValue()));
            assertThat(text, equalTo("null"));
        }
}
