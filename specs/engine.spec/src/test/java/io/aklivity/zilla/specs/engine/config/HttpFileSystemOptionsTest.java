package io.aklivity.zilla.specs.engine.config;
        import ch.qos.logback.classic.Level;
        import ch.qos.logback.classic.LoggerContext;
        import ch.qos.logback.classic.spi.ILoggingEvent;
        import ch.qos.logback.core.read.ListAppender;
        import jakarta.json.JsonException;
        import jakarta.json.JsonObject;

        import org.junit.Rule;
        import org.junit.Test;
        import org.junit.rules.TestRule;
        import org.junit.rules.TestWatcher;
        import org.junit.runner.Description;
        import org.junit.runners.model.Statement;
        import ch.qos.logback.classic.Logger;
        import org.slf4j.LoggerFactory;

        import java.util.ArrayList;
        import java.util.List;

        import static java.lang.System.out;
        import static org.hamcrest.MatcherAssert.assertThat;
        import static org.hamcrest.Matchers.not;
        import static org.hamcrest.Matchers.nullValue;
        import static org.junit.Assert.fail;

    public class HttpFileSystemOptionsTest {
        private static String watchedLog;

        @Rule
        public final TestRule watchman = new TestWatcher()
        {
            private final ListAppender<ILoggingEvent> listAppender = new ListAppender<ILoggingEvent>();

            public void before() {

                out.println("before " + watchedLog);
            }

            public void after() {

                out.println("after " + watchedLog);
            }

            @Override
            public Statement apply(Statement base, Description description) {
                before();
                return super.apply(base, description);
            }

            @Override
            protected void succeeded(Description description)
            {
                watchedLog += description.getDisplayName() + " " + "success!\n";
            }

            @Override
            protected void failed(Throwable e, Description description)
            {
                watchedLog += description.getDisplayName() + " " + e.getClass().getSimpleName() + "\n";
            }

            @Override
            protected void starting(Description description)
            {
                super.starting(description);
            }

            @Override
            protected void finished(Description description)
            {
                after();
                super.finished(description);
            }
        };

        @Rule
        public final ConfigSchemaRule schema = new ConfigSchemaRule()
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
        public void shouldValidateServerBindingWithRoutesAndNoExit()
        {
            JsonObject config = schema.validate("server.binding.with.routes.and.no.exit.json");
            assertThat(config, not(nullValue()));
        }

        @Test(expected = JsonException.class)
        public void shouldRejectServerBindingWithNoType() {
            schema.validate("server.binding.with.no.type.json");
        }

        @Test(expected = JsonException.class)
        public void shouldRejectServerBindingWithNoKind() {
            schema.validate("server.binding.with.no.kind.json");
        }

        @Test(expected = JsonException.class)
        public void shouldRejectServerBindingWithNoExit() {
            schema.validate("server.binding.with.no.exit.json");
        }
}
