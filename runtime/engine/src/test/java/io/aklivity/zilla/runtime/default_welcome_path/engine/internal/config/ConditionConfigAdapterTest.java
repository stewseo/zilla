package io.aklivity.zilla.runtime.default_welcome_path.engine.internal.config;

import io.aklivity.zilla.runtime.engine.config.ConditionConfig;
import io.aklivity.zilla.runtime.engine.config.ConditionConfigAdapterSpi;
import io.aklivity.zilla.runtime.engine.internal.config.ConditionAdapter;
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

public class ConditionConfigAdapterTest {
    private ConditionAdapter adapter;
    private Jsonb jsonb;

    @Before
    public void initJson()
    {
        adapter = new ConditionAdapter();
        adapter.adaptType("test_condition");
        JsonbConfig config = new JsonbConfig()
                .withAdapters(adapter);
        jsonb = JsonbBuilder.create(config);
    }

    @Test
    public void shouldReadCondition()
    {
        String text =
                "{" +
                        "\"match\": \"test\"" +
                        "}";

        ConditionConfig condition = jsonb.fromJson(text, ConditionConfig.class);

        assertThat(condition, not(nullValue()));
        assertThat(((io.aklivity.zilla.runtime.engine.internal.config.ConditionConfigAdapterTest.TestConditionConfig) condition).match, equalTo("test"));
    }

    @Test
    public void shouldWriteCondition()
    {
        ConditionConfig condition = new io.aklivity.zilla.runtime.engine.internal.config.ConditionConfigAdapterTest.TestConditionConfig("test_condition");

        String text = jsonb.toJson(condition);

        assertThat(text, not(nullValue()));
        assertThat(text, equalTo("{\"match\":\"test_condition\"}"));
    }

    @Test
    public void shouldReadNullWhenNotAdapting()
    {
        String text =
                "{" +
                        "\"match\": \"test_condition\"" +
                        "}";

        adapter.adaptType(null);
        ConditionConfig condition = jsonb.fromJson(text, ConditionConfig.class);

        assertThat(condition, nullValue());
    }

    @Test
    public void shouldWriteNullWhenNotAdapting()
    {
        ConditionConfig condition = new io.aklivity.zilla.runtime.engine.internal.config.ConditionConfigAdapterTest.TestConditionConfig("test_condition");

        adapter.adaptType(null);
        String text = jsonb.toJson(condition);

        assertThat(text, not(nullValue()));
        assertThat(text, equalTo("null"));
    }

    public static final class TestConditionConfig extends ConditionConfig
    {
        public final String match;

        public TestConditionConfig(
                String match)
        {
            this.match = match;
        }
    }

    public static final class TestConditionConfigAdapter implements ConditionConfigAdapterSpi
    {
        private static final String MATCH_NAME = "match";

        @Override
        public String type()
        {
            return "test";
        }

        @Override
        public JsonObject adaptToJson(
                ConditionConfig condition)
        {
            io.aklivity.zilla.runtime.engine.internal.config.ConditionConfigAdapterTest.TestConditionConfig testCondition = (io.aklivity.zilla.runtime.engine.internal.config.ConditionConfigAdapterTest.TestConditionConfig) condition;

            JsonObjectBuilder object = Json.createObjectBuilder();

            object.add(MATCH_NAME, testCondition.match);

            return object.build();
        }

        @Override
        public ConditionConfig adaptFromJson(
                JsonObject object)
        {
            String match = object.getString(MATCH_NAME);

            return new io.aklivity.zilla.runtime.engine.internal.config.ConditionConfigAdapterTest.TestConditionConfig(match);
        }
    }
}
