/*
 * Copyright 2021-2022 Aklivity Inc.
 *
 * Aklivity licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package io.aklivity.zilla.specs.engine.config;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonPatch;
import jakarta.json.JsonReader;
import jakarta.json.spi.JsonProvider;
import jakarta.json.stream.JsonParser;
import net.logstash.logback.composite.ContextJsonProvider;
import net.logstash.logback.composite.loggingevent.*;
import net.logstash.logback.encoder.LoggingEventCompositeJsonEncoder;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.leadpony.justify.api.JsonSchema;
import org.leadpony.justify.api.JsonSchemaReader;
import org.leadpony.justify.api.JsonValidationService;
import org.leadpony.justify.api.ProblemHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import static java.lang.System.out;
import static org.junit.Assert.assertEquals;

public final class LogConfigSchemaRule extends TestWatcher {
    private JsonProvider provider;
    private String schemaName = "io/aklivity/zilla/specs/engine/schema/engine.schema.json";
    private List<String> schemaPatchNames = new ArrayList<>();
    private String configurationRoot;
    private Function<String, InputStream> findConfig;
    private static Logger logger;

    String type, options;

    static {
        logger = LoggerFactory.getLogger(LogConfigSchemaRule.class);
    }

    public static Logger getLogger()
    {
        return logger;
    }

    public LogConfigSchemaRule schema(
            String schemaName)
    {
        logger.info("name {}", schemaName);
        this.schemaName = schemaName;
        return this;
    }
    public LogConfigSchemaRule schemaPatch(
            String schemaPatchName)
    {
        logger.info("schema {}", schemaName);
        this.schemaPatchNames.add(schemaPatchName);
        return this;
    }
    public LogConfigSchemaRule configurationRoot(
            String configurationRoot)
    {
        logger.info("name {}", configurationRoot);
        this.configurationRoot = configurationRoot;
        return this;
    }

    @Override
    public Statement apply(
            Statement base,
            Description description)
    {
        logger.info("base {}", base);
        logger.info("description {}", description);
        Objects.requireNonNull(schemaName, "schema");
        schemaPatchNames.forEach(n -> Objects.requireNonNull(n, "schemaPatch"));

        Function<String, InputStream> findResource = description.getTestClass().getClassLoader()::getResourceAsStream;

        InputStream schemaInput = findResource.apply(schemaName);
        JsonProvider schemaProvider = JsonProvider.provider();
        JsonReader schemaReader = schemaProvider.createReader(schemaInput);
        JsonObject schemaObject = schemaReader.readObject();

        schemaObject.keySet().forEach(e->{
            logger.debug("key {}", e);
        });

        assertEquals(2, schemaObject.size());

        for (String schemaPatchName : schemaPatchNames) {
            InputStream schemaPatchInput = findResource.apply(schemaPatchName);

            Objects.requireNonNull(schemaPatchInput, "schemaPatch");

            JsonReader schemaPatchReader = schemaProvider.createReader(schemaPatchInput);
            JsonArray schemaPatchArray = schemaPatchReader.readArray();
            JsonPatch schemaPatch = schemaProvider.createPatch(schemaPatchArray);

            schemaObject = schemaPatch.apply(schemaObject);
        }

        JsonParser schemaParser = schemaProvider.createParserFactory(null)
                .createParser(new StringReader(schemaObject.toString()));

        JsonValidationService service = JsonValidationService.newInstance();
        JsonSchemaReader reader = service.createSchemaReader(schemaParser);
        JsonSchema schema = reader.read();
        logger.info("schemaObject {}", schemaObject);
        provider = service.createJsonProvider(schema, parser -> ProblemHandler.throwing());

        if (configurationRoot != null) {
            String configFormat = String.format("%s/%%s", configurationRoot);
            findConfig = configName -> findResource.apply(String.format(configFormat, configName));
        } else {
            Class<?> testClass = description.getTestClass();
            String configFormat = String.format("%s-%%s", testClass.getSimpleName());
            findConfig = configName -> testClass.getResourceAsStream(String.format(configFormat, configName));
        }
        logger.info("base {}", base);
        return base;
    }
    public JsonObject validate(
            String configName) {
        logger.info("configName {}", configName);
        InputStream input = findConfig.apply(configName);
        JsonReader reader = provider.createReader(input);
        JsonObject jsonObject = reader.readObject();
        logger.info("jsonObject {}", jsonObject);
        return jsonObject;
    }
}
