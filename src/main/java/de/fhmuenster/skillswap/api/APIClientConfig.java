package de.fhmuenster.skillswap.api;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class APIClientConfig {

    @Bean
    public RestClient holidayApiClient(ObjectProvider<RestClient.Builder> builderProvider) {
        // Fragt Spring nach einem Builder.
        // Falls Spring keinen hat (wie beim bootRun), nutzen wir RestClient::builder als Fallback.
        RestClient.Builder builder = builderProvider.getIfAvailable(RestClient::builder);

        return builder
                .baseUrl("https://date.nager.at/api/v3")
                .build();
    }
}