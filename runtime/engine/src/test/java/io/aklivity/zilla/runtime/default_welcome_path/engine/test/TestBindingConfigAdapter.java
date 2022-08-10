package io.aklivity.zilla.runtime.default_welcome_path.engine.test;

import io.aklivity.zilla.runtime.engine.config.*;
import io.aklivity.zilla.runtime.engine.internal.config.KindAdapter;
import io.aklivity.zilla.runtime.engine.internal.config.OptionsAdapter;
import io.aklivity.zilla.runtime.engine.internal.config.RouteAdapter;
import jakarta.json.*;
import jakarta.json.bind.adapter.JsonbAdapter;
import org.agrona.collections.MutableInteger;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

public class TestBindingConfigAdapter implements JsonbAdapter<BindingConfig[], JsonObject>
{
    private static final String VAULT_NAME = "vault";
    private static final String EXIT_NAME = "exit";
    private static final String TYPE_NAME = "type";
    private static final String KIND_NAME = "kind";
    private static final String OPTIONS_NAME = "options";
    private static final String ROUTES_NAME = "routes";
    private static final List<RouteConfig> ROUTES_DEFAULT = emptyList();

    private final KindAdapter kind;
    private final RouteAdapter route;
    private final OptionsAdapter options;

    public TestBindingConfigAdapter()
    {
        this.kind = new KindAdapter();
        this.route = new RouteAdapter();
        this.options = new OptionsAdapter(OptionsConfigAdapterSpi.Kind.BINDING);
    }

    public JsonObject adaptToJson(
            BindingConfig[] bindings)
    {
        JsonObjectBuilder object = Json.createObjectBuilder();

        for (BindingConfig binding : bindings)
        {
            route.adaptType(binding.type);
            options.adaptType(binding.type);

            JsonObjectBuilder item = Json.createObjectBuilder();

            if (binding.vault != null)
            {
                item.add(VAULT_NAME, binding.vault);
            }

            item.add(TYPE_NAME, binding.type);

            item.add(KIND_NAME, kind.adaptToJson(binding.kind));

            if (binding.options != null)
            {
                item.add(OPTIONS_NAME, options.adaptToJson(binding.options));
            }

            if (!ROUTES_DEFAULT.equals(binding.routes))
            {
                JsonArrayBuilder routes = Json.createArrayBuilder();
                binding.routes.forEach(r -> routes.add(route.adaptToJson(r)));
                item.add(ROUTES_NAME, routes);
            }

            object.add(binding.entry, item);
        }

        return object.build();
    }

        public BindingConfig[] adaptFromJson(
            JsonObject object)
    {
        List<BindingConfig> bindings = new LinkedList<>();

        for (String entry : object.keySet())
        {
            JsonObject item = object.getJsonObject(entry);
            String type = item.getString(TYPE_NAME);

            route.adaptType(type);
            options.adaptType(type);

            String vault = item.containsKey(VAULT_NAME)
                    ? item.getString(VAULT_NAME)
                    : null;
            KindConfig kind = this.kind.adaptFromJson(item.getJsonString(KIND_NAME));
            OptionsConfig opts = item.containsKey(OPTIONS_NAME) ?
                    options.adaptFromJson(item.getJsonObject(OPTIONS_NAME)) :
                    null;
            MutableInteger order = new MutableInteger();
            List<RouteConfig> routes = item.containsKey(ROUTES_NAME)
                    ? item.getJsonArray(ROUTES_NAME)
                    .stream()
                    .map(JsonValue::asJsonObject)
                    .peek(o -> route.adaptFromJsonIndex(order.value++))
                    .map(route::adaptFromJson)
                    .collect(toList())
                    : ROUTES_DEFAULT;

            RouteConfig exit = item.containsKey(EXIT_NAME)
                    ? new RouteConfig(routes.size(), item.getString(EXIT_NAME))
                    : null;

            if (exit != null)
            {
                List<RouteConfig> routesWithExit = new ArrayList<>();
                routesWithExit.addAll(routes);
                routesWithExit.add(exit);
                routes = routesWithExit;
            }

            bindings.add(new BindingConfig(vault, entry, type, kind, opts, routes));
        }

        return bindings.toArray(BindingConfig[]::new);
    }
}

