package io.aklivity.zilla.runtime.engine.internal.config.default_welcome_path;

import io.aklivity.zilla.runtime.engine.config.ConditionConfig;
import io.aklivity.zilla.runtime.engine.config.ConditionConfigAdapterSpi;
import io.aklivity.zilla.runtime.engine.config.WithConfig;
import io.aklivity.zilla.runtime.engine.config.WithConfigAdapterSpi;
import io.aklivity.zilla.runtime.engine.internal.config.WithAdapter;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertNotEquals;


public class WithConfigAdapterTest
    {
        private WithAdapter adapter;
        private Jsonb jsonb;

        @Before
        public void initJson()
        {
            adapter = new WithAdapter();
            adapter.adaptType("test");
            JsonbConfig config = new JsonbConfig()
                    .withAdapters(adapter);
            jsonb = JsonbBuilder.create(config);
        }

        @Test
        public void shouldReadWith()
        {
            String text =
                    "{" +
                            "\"name\": \"test_index.html\"" +
                            "}";

            WithConfig with = jsonb.fromJson(text, WithConfig.class);

            assertThat(with, not(nullValue()));
            assertThat(((io.aklivity.zilla.runtime.engine.internal.config.WithConfigAdapterTest.TestWithConfig) with).name, not("test"));
            assertThat(((io.aklivity.zilla.runtime.engine.internal.config.WithConfigAdapterTest.TestWithConfig) with).name, equalTo("test_index.html"));
        }

        @Test
        public void shouldWriteWith()
        {
            ConditionConfig condition = new io.aklivity.zilla.runtime.engine.internal.config.WithConfigAdapterTest.TestCondition("test_index.html");

            String text = jsonb.toJson(condition);

            assertThat(text, not(nullValue()));
            assertThat(text, not("{\"match\":\"test\"}"));
            assertThat(text, equalTo("{\"match\":\"test_index.html\"}"));
        }

        @Test
        public void shouldReadNullWhenNotAdapting()
        {
            String text =
                    "{" +
                            "\"name\": \"test_index.html\"" +
                            "}";

            adapter.adaptType(null);
            WithConfig with = jsonb.fromJson(text, WithConfig.class);

            assertThat(with, nullValue());
        }

        @Test
        public void shouldWriteNullWhenNotAdapting()
        {
            WithConfig with = new io.aklivity.zilla.runtime.engine.internal.config.WithConfigAdapterTest.TestWithConfig("test_index.html");

            adapter.adaptType(null);
            String text = jsonb.toJson(with);

            assertThat(text, not(nullValue()));
            assertThat(text, equalTo("null"));
        }

        public static final class TestCondition extends ConditionConfig
        {
            public final String match;

            public TestCondition(
                    String match)
            {
                this.match = match;
            }
        }

        public static final class TestConditionAdapter implements ConditionConfigAdapterSpi
        {
            private static final String MATCH_NAME = "match";

            @Override
            public String type()
            {
                return "test_index.html";
            }

            @Override
            public JsonObject adaptToJson(
                    ConditionConfig condition)
            {
                io.aklivity.zilla.runtime.engine.internal.config.WithConfigAdapterTest.TestCondition testCondition = (io.aklivity.zilla.runtime.engine.internal.config.WithConfigAdapterTest.TestCondition) condition;

                JsonObjectBuilder object = Json.createObjectBuilder();

                object.add(MATCH_NAME, testCondition.match);

                return object.build();
            }

            @Override
            public ConditionConfig adaptFromJson(
                    JsonObject object)
            {
                String match = object.getString(MATCH_NAME);

                return new io.aklivity.zilla.runtime.engine.internal.config.WithConfigAdapterTest.TestCondition(match);
            }
        }

        public static final class TestWithConfig extends WithConfig
        {
            public final String name;

            public TestWithConfig(
                    String name)
            {
                this.name = name;
            }
        }

        public static final class TestWithConfigAdapter implements WithConfigAdapterSpi
        {
            private static final String NAME_NAME = "name";

            @Override
            public String type()
            {
                return "test_index.html";
            }

            @Override
            public JsonObject adaptToJson(
                    WithConfig condition)
            {
                io.aklivity.zilla.runtime.engine.internal.config.WithConfigAdapterTest.TestWithConfig testWith = (io.aklivity.zilla.runtime.engine.internal.config.WithConfigAdapterTest.TestWithConfig) condition;

                JsonObjectBuilder object = Json.createObjectBuilder();

                object.add(NAME_NAME, testWith.name);

                return object.build();
            }

            @Override
            public WithConfig adaptFromJson(
                    JsonObject object)
            {
                String name = object.getString(NAME_NAME);

                return new io.aklivity.zilla.runtime.engine.internal.config.WithConfigAdapterTest.TestWithConfig(name);
            }
        }
    }

