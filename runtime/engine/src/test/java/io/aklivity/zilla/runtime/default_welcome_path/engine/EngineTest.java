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
package io.aklivity.zilla.runtime.default_welcome_path.engine;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;
import io.aklivity.zilla.runtime.engine.Engine;
import io.aklivity.zilla.runtime.engine.EngineConfiguration;
import io.aklivity.zilla.runtime.engine.EngineStats;
import io.aklivity.zilla.runtime.engine.ext.EngineExtContext;
import io.aklivity.zilla.runtime.engine.ext.EngineExtSpi;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import static io.aklivity.zilla.runtime.engine.EngineConfiguration.ENGINE_DIRECTORY;
import static io.aklivity.zilla.runtime.engine.EngineConfiguration.ENGINE_WORKERS;
import static junit.framework.Assert.assertEquals;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.mockito.AdditionalMatchers.not;

public class EngineTest {
    private EngineConfiguration config;
    Logger logger = LoggerFactory.getLogger(EngineTest.class);

    @Before
    public void initConfig()
    {
        Properties properties = new Properties();
        properties.put(ENGINE_DIRECTORY.name(), "target/zilla-itests");
        properties.put(ENGINE_WORKERS.name(), "1");
        config = new EngineConfiguration(properties);
        logger.info("Engine init config");
    }

    // debug: target\zilla-itests\tuning is not writeable
    @Test
    public void shouldConfigureEmpty() throws Exception
    {

        Logger logger = LoggerFactory.getLogger(EngineTest.class);
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();

        try
        {
            JoranConfigurator configurator = new JoranConfigurator();
            configurator.setContext(lc);
            lc.reset();

            List<Throwable> errors = new LinkedList<>();

            try (Engine engine = Engine.builder()
                    .config(config)
                    .errorHandler(errors::add)
                    .build()) {
                engine.start().get();
            } catch (Throwable ex) {
                errors.add(ex);
            }

            configurator.doConfigure(errors.toString());

            assertThat(errors, empty());

        } catch (JoranException je)
        {
            StatusPrinter.printInCaseOfErrorsOrWarnings(lc);
        }
    }

    @Test
    public void shouldNotConfigureWithoutSpecificOption() throws Exception
    {
        String resource = String.format("%s-%s.default_welcome-path.json", getClass().getSimpleName(), "default_welcome-path");
        URL configURL = getClass().getResource(resource);
        List<Throwable> errors = new LinkedList<>();
        try (Engine engine = Engine.builder()
                .config(config)
                .configURL(configURL)
                .errorHandler(errors::add)
                .build())
        {
            engine.start().get();
        }
        catch (Throwable ex)
        {
            errors.add(ex);
        }
        finally
        {
            assertThat(errors, not(empty()));
            logger.debug("Throwable errors");
        }
    }

    @Test
    public void shouldConfigure() throws Exception
    {

        String resource = String.format("%s-%s.http_filesystem_proxy.json", getClass().getSimpleName(), "http_filesystem_proxy");
        URL configURL = getClass().getResource(resource);

        List<Throwable> errors = new LinkedList<>();
        try (Engine engine = Engine.builder()
                .config(config)
                .configURL(configURL)
                .errorHandler(errors::add)
                .build())
        {
            engine.start().get();

            EngineStats stats = engine.stats("default", "test0");
            assertEquals(0L, stats.initialOpens());
            assertEquals(0L, stats.initialCloses());
            assertEquals(0L, stats.initialErrors());
            assertEquals(0L, stats.initialBytes());
            assertEquals(0L, stats.replyOpens());
            assertEquals(0L, stats.replyCloses());
            assertEquals(0L, stats.replyErrors());
            assertEquals(0L, stats.replyBytes());
        }
        catch (Throwable ex)
        {
            errors.add(ex);
        }
        finally
        {
            assertThat(errors, empty());
        }
    }

    public static final class TestEngineExt implements EngineExtSpi
    {
        @Override
        public void onConfigured(
                EngineExtContext context)
        {
        }

        @Override
        public void onClosed(
                EngineExtContext context)
        {
        }
    }

}
