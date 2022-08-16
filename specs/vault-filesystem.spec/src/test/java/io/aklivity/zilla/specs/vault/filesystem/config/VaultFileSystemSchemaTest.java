package io.aklivity.zilla.specs.vault.filesystem.config;

import co.elastic.clients.elasticsearch.ElasticsearchAsyncClient;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import io.aklivity.zilla.specs.engine.config.LogConfigSchemaRule;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.Objects;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertEquals;

public class VaultFileSystemSchemaTest {
    @Rule
    public final LogConfigSchemaRule schema = new LogConfigSchemaRule()
            .schemaPatch("io/aklivity/zilla/specs/vault/filesystem/schema/filesystem.schema.patch.json")
            .configurationRoot("io/aklivity/zilla/specs/vault/filesystem/config");

    @Test
    public void shouldValidateVault()
    {
        JsonObject config = schema.validate("vault.json");
        assertThat(config, not(nullValue()));
    }

    @Test
    public void vaultTest()
    {
        JsonObject config = schema.validate("vault.json");

        assertEquals(2, config.size());

        assertEquals(Set.of("name", "vaults"), config.keySet());

        var schemaName = config.getValue("/name");

        assertEquals("\"test\"", schemaName.toString());

        var vaultConfig = config.getValue("/vaults");

        String expectedVaultConfig = "" +
                "{\"server\":{\"type\":\"filesystem\",\"options\":" +
                "{\"keys\":{\"store\":\"stores/server/keys\",\"type\":\"pkcs12\",\"password\":\"generated\"}," +
                "\"trust\":{\"store\":\"stores/server/trust\",\"type\":\"pkcs12\",\"password\":\"generated\"}," +
                "\"signers\":{\"store\":\"stores/server/signers\",\"type\":\"pkcs12\",\"password\":\"generated\"}}}," +
                "\"client\":{\"type\":\"filesystem\",\"options\":" +
                "{\"keys\":{\"store\":\"stores/client/keys\",\"type\":\"pkcs12\",\"password\":\"generated\"}," +
                "\"trust\":{\"store\":\"stores/client/trust\",\"type\":\"pkcs12\",\"password\":\"generated\"}," +
                "\"signers\":{\"store\":\"stores/client/signers\",\"type\":\"pkcs12\",\"password\":\"generated\"}}}}";

        assertEquals(vaultConfig.toString(), expectedVaultConfig);

    }
}
