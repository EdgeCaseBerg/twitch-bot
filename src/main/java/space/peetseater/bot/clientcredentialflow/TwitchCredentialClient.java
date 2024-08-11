package space.peetseater.bot.clientcredentialflow;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class TwitchCredentialClient {
    private final HttpClient httpClient;
    transient private final String clientId;
    transient private final String clientSecret;

    public TwitchCredentialClient(String clientId, String clientSecret) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.httpClient = HttpClient.newHttpClient();
    }

    public ClientCredentialFlowResponse getCredential() throws URISyntaxException, IOException, InterruptedException {
        URI providerUrl = new URI("https://id.twitch.tv/oauth2/token");

        String form = (new StringBuilder()).append("client_id=").append(clientId)
                .append("&client_secret=").append(clientSecret)
                .append("&grant_type=client_credentials").toString();

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(providerUrl)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(form))
                .build();

        HttpResponse<ClientCredentialFlowResponse> response = httpClient.send(httpRequest, new CredentialBodyHandler());
        return response.body();
    }

}
