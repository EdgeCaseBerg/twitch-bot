package space.peetseater.bot.clientcredentialflow;

import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class CredentialBodyHandler implements HttpResponse.BodyHandler<ClientCredentialFlowResponse> {

    @Override
    public HttpResponse.BodySubscriber<ClientCredentialFlowResponse> apply(HttpResponse.ResponseInfo responseInfo) {
        HttpResponse.BodySubscriber<String> stringBodySubscriber = HttpResponse.BodySubscribers.ofString(StandardCharsets.UTF_8);
        return HttpResponse.BodySubscribers.mapping(stringBodySubscriber, (s) -> {
            Optional<ClientCredentialFlowResponse> parsed = CredentialParser.parse(s);
            if (parsed.isEmpty()) {
                throw new CredentialBodyFailure("Cannot parse body as credential: %s".formatted(s));
            }
            return parsed.get();
        });
    }
}
