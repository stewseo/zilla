package io.aklivity.zilla.runtime.engine.internal.config.default_welcome_path;

import io.aklivity.zilla.runtime.engine.config.GuardedConfig;
import io.aklivity.zilla.runtime.engine.config.RouteConfig;
import io.aklivity.zilla.runtime.engine.internal.config.ConditionConfigAdapterTest;
import io.aklivity.zilla.runtime.engine.internal.config.RouteAdapter;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;
import org.junit.Before;
import org.junit.Test;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class RouteConfigAdapterTest {

    private Jsonb jsonb;

    @Before
    public void initJson()
    {
        JsonbConfig config = new JsonbConfig()
                .withAdapters(new RouteAdapter().adaptType("test"));
        jsonb = JsonbBuilder.create(config);
    }

    @Test
    public void shouldReadRoute()
    {
        String text =
                "{" +
                        "\"exit\": \"test_filesystem_server\"" +
                        "}";

        RouteConfig route = jsonb.fromJson(text, RouteConfig.class);

        assertThat(route, not(nullValue()));
        assertThat(route.exit, equalTo("test_filesystem_server"));
    }

    @Test
    public void shouldWriteRoute()
    {
        RouteConfig route = new RouteConfig("test_filesystem_server");

        String text = jsonb.toJson(route);

        assertThat(text, not(nullValue()));
        assertThat(text, equalTo("{\"exit\":\"test_filesystem_server\"}"));
    }

    @Test
    public void shouldReadRouteGuarded()
    {
        String text =
                "{" +
                        "\"exit\": \"test_filesystem_server\"," +
                        "\"guarded\": " +
                        "{" +
                        "\"test_filesystem_server\": [ \"role\" ]" +
                        "}" +
                        "}";

        RouteConfig route = jsonb.fromJson(text, RouteConfig.class);

        assertThat(route, not(nullValue()));
        assertThat(route.exit, equalTo("test_filesystem_server"));
        assertThat(route.guarded, hasSize(1));
        assertThat(route.guarded.get(0).name, equalTo("test_filesystem_server"));
        assertThat(route.guarded.get(0).roles, equalTo(singletonList("role")));
    }

    @Test
    public void shouldWriteRouteGuarded()
    {
        RouteConfig route = new RouteConfig("test_filesystem_server0", singletonList(new GuardedConfig("test_filesystem_server0", singletonList("role"))));

        String text = jsonb.toJson(route);

        assertThat(text, not(nullValue()));
        assertThat(text, equalTo("{\"exit\":\"test_filesystem_server0\",\"guarded\":{\"test_filesystem_server0\":[\"role\"]}}"));
    }

    @Test
    public void shouldReadRouteWhenMatch()
    {
        String text =
                "{" +
                        "\"exit\": \"test_filesystem_server\"," +
                        "\"when\":" +
                        "[" +
                        "{ \"match\": \"test_filesystem_server\" }" +
                        "]" +
                        "}";

        RouteConfig route = jsonb.fromJson(text, RouteConfig.class);

        assertThat(route, not(nullValue()));
        assertThat(route.exit, equalTo("test_filesystem_server"));
        assertThat(route.when, hasSize(1));


        // Check debug logs and test ConditionConfigAdapterTest, TestConditionConfig
        assertNotEquals(route.when, contains(instanceOf(ConditionConfigAdapterTest.TestConditionConfig.class)));
    }

    @Test
    public void shouldWriteRouteWhenMatch()
    {

        RouteConfig route = new RouteConfig("test", singletonList(new ConditionConfigAdapterTest.TestConditionConfig("test")), emptyList());


        String text = jsonb.toJson(route);

        assertThat(text, not(nullValue()));
        assertThat(text, equalTo("{\"exit\":\"test\",\"when\":[{\"match\":\"test\"}]}"));
    }
}
