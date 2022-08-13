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

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import jakarta.json.JsonException;
import jakarta.json.JsonObject;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

public class MonitorSchemaTest
{
    @Rule
    public final MonitorConfigSchemaRule schema = new MonitorConfigSchemaRule()
            .schemaPatch("io/aklivity/zilla/specs/engine/schema/binding/test.schema.patch.json")
            .schemaPatch("io/aklivity/zilla/specs/engine/schema/guard/test.schema.patch.json")
            .schemaPatch("io/aklivity/zilla/specs/engine/schema/vault/test.schema.patch.json")
            .configurationRoot("io/aklivity/zilla/specs/engine/config");

    @Test
    public void shouldValidateServerBinding()
    {
        JsonObject config = schema.validate("server.json");
        assertThat(config, not(nullValue()));
    }

    @Test
    public void shouldValidateServerBindingWithOptions()
    {
        JsonObject config = schema.validate("server.binding.with.routes.and.no.exit.json");
        assertThat(config, not(nullValue()));
    }

    @Test(expected = JsonException.class)
    public void shouldRejectServerBindingWithoutCorrectOptions()
    {
        schema.validate("server.binding.with.no.kind.json");
    }

}



