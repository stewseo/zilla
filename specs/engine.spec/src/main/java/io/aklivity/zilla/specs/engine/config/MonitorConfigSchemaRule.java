package io.aklivity.zilla.specs.engine.config;

import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonPatch;
import jakarta.json.JsonReader;
import jakarta.json.spi.JsonProvider;
import jakarta.json.stream.JsonParser;
import org.junit.*;
import org.junit.rules.ExternalResource;
import org.junit.rules.RuleChain;
import org.junit.rules.TestRule;
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

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public final class MonitorConfigSchemaRule extends TestWatcher {
    private JsonProvider provider;
    private String schemaName = "io/aklivity/zilla/specs/engine/schema/engine.schema.json";
    private List<String> schemaPatchNames = new ArrayList<>();
    private String configurationRoot;
    private Function<String, InputStream> findConfig;
    private static final Logger logger = LoggerFactory.getLogger("configSchemaRuleLogger");

    public MonitorConfigSchemaRule schema(
            String schemaName) {
        this.schemaName = schemaName;
        logger.info("schema: " + schemaName);
        return this;
    }
    public MonitorConfigSchemaRule schemaPatch(
            String schemaPatchName) {
        this.schemaPatchNames.add(schemaPatchName);
        logger.info("schemaPatch: " + schemaPatchName);
        return this;
    }
    public MonitorConfigSchemaRule configurationRoot(
            String configurationRoot) {
        this.configurationRoot = configurationRoot;
        logger.info("configurationRoot: " + configurationRoot);
        return this;
    }

    @Override
    public Statement apply(
            Statement base,
            Description description) {
        Objects.requireNonNull(schemaName, "schema");
        schemaPatchNames.forEach(n -> Objects.requireNonNull(n, "schemaPatch"));

        Function<String, InputStream> findResource = description.getTestClass().getClassLoader()::getResourceAsStream;

        InputStream schemaInput = findResource.apply(schemaName);

        JsonProvider schemaProvider = JsonProvider.provider();
        JsonReader schemaReader = schemaProvider.createReader(schemaInput);
        JsonObject schemaObject = schemaReader.readObject();

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

        provider = service.createJsonProvider(schema, parser -> ProblemHandler.throwing());
        logger.debug("JsonProvider: " + provider);

        if (configurationRoot != null) {
            String configFormat = String.format("%s/%%s", configurationRoot);
            findConfig = configName -> findResource.apply(String.format(configFormat, configName));
        } else {
            Class<?> testClass = description.getTestClass();
            String configFormat = String.format("%s-%%s", testClass.getSimpleName());
            findConfig = configName -> testClass.getResourceAsStream(String.format(configFormat, configName));
        }
        return base;
    }

    public JsonObject validate(
            String configName) {
        logger.trace("debug" + configName);
        InputStream input = findConfig.apply(configName);
        JsonReader reader = provider.createReader(input);
        return reader.readObject();
    }
}
