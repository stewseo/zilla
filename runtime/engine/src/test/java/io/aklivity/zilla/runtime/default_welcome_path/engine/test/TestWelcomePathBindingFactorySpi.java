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
package io.aklivity.zilla.runtime.default_welcome_path.engine.test;

import io.aklivity.zilla.runtime.engine.Configuration;
import io.aklivity.zilla.runtime.engine.binding.Binding;
import io.aklivity.zilla.runtime.engine.binding.BindingFactorySpi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public final class TestWelcomePathBindingFactorySpi implements BindingFactorySpi
{
    static final Logger logger = LoggerFactory.getLogger(TestWelcomePathBindingFactorySpi.class);

    @Override
    public String name()
    {
        logger.debug("TestWelcomePathBindingFactorySpi name()");
        return "test";
    }

    @Override
    public Binding create(
            Configuration config)
    {
        logger.debug("creating with Configuration: " + config);
        return new TestWelcomePathBinding(config);
    }
}
