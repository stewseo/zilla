package io.aklivity.zilla.runtime.default_welcome_path.engine.internal.config;

import io.aklivity.zilla.runtime.engine.config.BindingConfig;
import io.aklivity.zilla.runtime.engine.config.RouteConfig;
import io.aklivity.zilla.runtime.engine.internal.config.BindingConfigsAdapter;
import io.aklivity.zilla.runtime.engine.test.internal.binding.config.TestBindingOptionsConfig;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;
import org.junit.Before;
import org.junit.Test;

import static io.aklivity.zilla.runtime.engine.config.KindConfig.SERVER;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class BindingConfigsAdapterTest {
    private Jsonb jsonb;

    @Before
    public void initJson()
    {
        JsonbConfig config = new JsonbConfig()
                .withAdapters(new BindingConfigsAdapter());
        jsonb = JsonbBuilder.create(config);

    }

    @Test
    public void shouldReadBinding()
    {
        String text =
                "{" +
                        "\"test_filesystem\":" +
                        "{" +
                        "\"type\": \"test_type\"," +
                        "\"kind\": \"server\"," +
                        "\"routes\":" +
                        "[" +
                        "]" +
                        "}" +
                        "}";

        BindingConfig[] bindings = jsonb.fromJson(text, BindingConfig[].class);

        assertThat(bindings[0], not(nullValue()));
        assertThat(bindings[0].type, equalTo("test_type"));
        assertThat(bindings[0].kind, equalTo(SERVER));
        assertThat(bindings[0].routes, emptyCollectionOf(RouteConfig.class));
    }

    @Test
    public void shouldWriteBinding()
    {
        BindingConfig[] bindings = { new BindingConfig(null, "test_filesystem", "test_type", SERVER, null, emptyList()) };

        String text = jsonb.toJson(bindings);

        assertThat(text, not(nullValue()));
        assertThat(text, equalTo("{\"test_filesystem\":{\"type\":\"test_type\",\"kind\":\"server\"}}"));
    }

    @Test
    public void shouldReadBindingWithVault()
    {
        String text =
                "{" +
                        "\"test_filesystem\":" +
                        "{" +
                        "\"vault\": \"test_vault\"," +
                        "\"type\": \"test_type\"," +
                        "\"kind\": \"server\"," +
                        "\"routes\":" +
                        "[" +
                        "]" +
                        "}" +
                        "}";

        BindingConfig[] bindings = jsonb.fromJson(text, BindingConfig[].class);

        assertThat(bindings[0], not(nullValue()));
        assertThat(bindings[0].vault, not(nullValue()));
        assertThat(bindings[0].vault, equalTo("test_vault"));
        assertThat(bindings[0].kind, equalTo(SERVER));
        assertThat(bindings[0].routes, emptyCollectionOf(RouteConfig.class));
    }

    @Test
    public void shouldWriteBindingWithVault()
    {
        BindingConfig[] bindings = { new BindingConfig("test_vault", "test_filesystem", "test_type", SERVER, null, emptyList()) };

        String text = jsonb.toJson(bindings);

        assertThat(text, not(nullValue()));
        assertThat(text, equalTo("{\"test_filesystem\":{\"vault\":\"test_vault\",\"type\":\"test_type\",\"kind\":\"server\"}}"));
    }

    @Test
    public void shouldReadBindingWithOptions()
    {
        String text =
                "{" +
                        "\"test_filesystem\":" +
                        "{" +
                        "\"type\": \"test\"," +
                        "\"kind\": \"server\"," +
                        "\"options\":" +
                        "{" +
                        "\"mode\": \"test\"" +
                        "}" +
                        "}" +
                        "}";


        BindingConfig[] bindings = jsonb.fromJson(text, BindingConfig[].class);

        assertThat(bindings[0], not(nullValue()));
        assertThat(bindings[0].type, equalTo("test"));
        assertThat(bindings[0].entry, equalTo("test_filesystem"));
        assertThat(bindings[0].kind, equalTo(SERVER));
        assertThat(bindings[0].options, instanceOf(TestBindingOptionsConfig.class));
        assertThat(((TestBindingOptionsConfig) bindings[0].options).mode, equalTo("test"));
    }

    @Test
    public void shouldWriteBindingWithOptions()
    {
        BindingConfig[] bindings =
                { new BindingConfig(null, "test_http_filesystem", "test", SERVER, new TestBindingOptionsConfig("test"), emptyList()) };

        String text = jsonb.toJson(bindings);

        assertThat(text, not(nullValue()));
        assertThat(text, equalTo("{\"test_http_filesystem\":{\"type\":\"test\",\"kind\":\"server\",\"options\":{\"mode\":\"test\"}}}"));
    }

    @Test
    public void shouldReadBindingWithRoute()
    {
        String text =
                "{" +
                        "\"test_http_filesystem\":" +
                        "{" +
                        "\"type\": \"test\"," +
                        "\"kind\": \"server\"," +
                        "\"routes\":" +
                        "[" +
                        "{" +
                        "\"exit\": \"test_filesystem_server\"" +
                        "}" +
                        "]" +
                        "}" +
                        "}";

        BindingConfig[] bindings = jsonb.fromJson(text, BindingConfig[].class);

        assertThat(bindings[0], not(nullValue()));
        assertThat(bindings[0].entry, equalTo("test_http_filesystem"));
        assertThat(bindings[0].kind, equalTo(SERVER));
        assertThat(bindings[0].routes, hasSize(1));
        assertThat(bindings[0].routes.get(0).exit, equalTo("test_filesystem_server"));
        assertThat(bindings[0].routes.get(0).when, empty());
    }

    @Test
    public void shouldWriteBindingWithRoute()
    {
        BindingConfig[] bindings =
                { new BindingConfig(null, "test_http_filesystem", "test", SERVER, null, singletonList(new RouteConfig("test_filesystem_server"))) };

        String text = jsonb.toJson(bindings);

        assertThat(text, not(nullValue()));
        assertThat(text, equalTo("{\"test_http_filesystem\":{\"type\":\"test\",\"kind\":\"server\",\"routes\":[{\"exit\":\"test_filesystem_server\"}]}}"));
    }

}
