package io.axoniq.demo.hotel.booking.command.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.module.kotlin.KotlinModule;
import io.axoniq.dataprotection.api.FieldEncryptingSerializer;
import io.axoniq.dataprotection.cryptoengine.CryptoEngine;
import io.axoniq.dataprotection.cryptoengine.vault.VaultCryptoEngine;
import okhttp3.OkHttpClient;
import org.axonframework.serialization.Serializer;
import org.axonframework.serialization.json.JacksonSerializer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("dataprotection")
@ConfigurationProperties(prefix = "vault.server")
@Configuration
public class DataProtectionConfiguration {

    private String url;
    private String token;
    private String prefix;

    @Bean
    public OkHttpClient okHttpClient() {
        return new OkHttpClient();
    }

    @Bean
    public CryptoEngine cryptoEngine(OkHttpClient okHttpClient) {
        return new VaultCryptoEngine(okHttpClient, url, token, prefix);
    }

    @Bean("eventSerializer")
    public Serializer eventSerializer(CryptoEngine cryptoEngine) {
        ObjectMapper objectMapper = new ObjectMapper()
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .registerModule(new Jdk8Module())
                .registerModule(new KotlinModule());
        return new FieldEncryptingSerializer(cryptoEngine, JacksonSerializer
                .builder()
                .objectMapper(objectMapper)
                .build());
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
}
