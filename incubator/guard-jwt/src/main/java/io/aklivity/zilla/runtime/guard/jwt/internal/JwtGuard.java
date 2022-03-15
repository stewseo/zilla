/*
 * Copyright 2021-2022 Aklivity Inc
 *
 * Licensed under the Aklivity Community License (the "License"); you may not use
 * this file except in compliance with the License.  You may obtain a copy of the
 * License at
 *
 *   https://www.aklivity.io/aklivity-community-license/
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 */
package io.aklivity.zilla.runtime.guard.jwt.internal;

import static io.aklivity.zilla.runtime.engine.EngineConfiguration.ENGINE_WORKERS;

import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.function.LongPredicate;
import java.util.function.LongToIntFunction;

import io.aklivity.zilla.runtime.engine.Configuration;
import io.aklivity.zilla.runtime.engine.EngineContext;
import io.aklivity.zilla.runtime.engine.config.GuardedConfig;
import io.aklivity.zilla.runtime.engine.guard.Guard;

public final class JwtGuard implements Guard
{
    public static final String NAME = "jwt";

    private final Configuration config;
    private final JwtGuardContext[] contexts;

    JwtGuard(
        Configuration config)
    {
        this.config = config;
        this.contexts = new JwtGuardContext[ENGINE_WORKERS.get(config)];
    }

    @Override
    public String name()
    {
        return JwtGuard.NAME;
    }

    @Override
    public URL type()
    {
        return getClass().getResource("schema/jwt.schema.patch.json");
    }

    @Override
    public JwtGuardContext supply(
        EngineContext context)
    {
        JwtGuardContext guard = new JwtGuardContext(config, context);
        contexts[context.index()] = guard;
        return guard;
    }

    @Override
    public LongPredicate verifier(
        LongToIntFunction indexOf,
        GuardedConfig config)
    {
        Objects.requireNonNull(indexOf);

        final long guardId = config.id;
        List<String> roles = config.roles;

        return session -> verify(guardId, indexOf.applyAsInt(session), session, roles);
    }

    private boolean verify(
        long guardId,
        int index,
        long sessionId,
        List<String> roles)
    {
        final JwtGuardContext context = contexts[index];
        final JwtGuardHandler handler = context != null ? context.handler(guardId) : null;
        return handler != null && handler.verify(sessionId, roles);
    }
}
