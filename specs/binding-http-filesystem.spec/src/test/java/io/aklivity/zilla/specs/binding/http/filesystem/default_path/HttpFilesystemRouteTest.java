package io.aklivity.zilla.specs.binding.http.filesystem.route;

import io.aklivity.zilla.specs.engine.config.ConfigSchemaRule;
import jakarta.json.JsonObject;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import static junit.framework.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HttpFilesystemRouteTest
    {
        private static final Logger logger = LoggerFactory.getLogger("jsonLogger");
        @Rule
        public final ConfigSchemaRule schema = new ConfigSchemaRule()
                .schemaPatch("io/aklivity/zilla/specs/binding/http/filesystem/schema/http.filesystem.schema.patch.json")
                .configurationRoot("io/aklivity/zilla/specs/binding/http/filesystem/config");
        @Test
        public void shouldValidateProxyWithPath()
        {
            JsonObject config = schema.validate("proxy.with.path.json");
            logger.trace("JsonObject config {}", config);
            assertNotNull(config);

            assertEquals(2, config.size());
            assertEquals("\"test\"", config.getValue("/name").toString());
            assertEquals("" +
                    "{\"http0\":" +
                        "{\"type\":\"http-filesystem\"," +
                        "\"kind\":\"proxy\"," +
                        "\"routes\":" +
                            "[" +
                                "{\"" +
                                "exit\":\"filesystem0\"," +
                                    "\"when\":" +
                                    "[" +
                                        "{\"" +
                                            "path\":\"/index.html\"" +
                                       "}" +
                                   "]," +
                                   "\"with\":" +
                                   "{\"" +
                                       "path\":\"index.html\"" +
                                   "}" +
                               "}" +
                           "]" +
                       "}" +
                    "}",
                    config.getValue("/bindings").toString());
        }
        @Test
        public void shouldValidateProxyWithPathDynamic()
        {
            JsonObject config = schema.validate("proxy.with.path.dynamic.json");
            assertNotNull(config);
            assertEquals(2, config.size());
            assertEquals("\"test\"", config.getValue("/name").toString());

            assertEquals("test", config.getValue("/binding").toString());
        }
    }



