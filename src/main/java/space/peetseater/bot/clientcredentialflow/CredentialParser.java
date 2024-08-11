package space.peetseater.bot.clientcredentialflow;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.stream.JsonParser;
import java.io.StringReader;
import java.util.Optional;

public class CredentialParser {
    static public Optional<ClientCredentialFlowResponse> parse(String raw) {
        JsonParser parser = Json.createParser(new StringReader(raw));
        if (!parser.hasNext()) {
            return Optional.empty();
        }

        JsonParser.Event event = parser.next();
        switch (event) {
            case START_OBJECT -> {
                JsonObject rootObject = parser.getObject();
                String token = rootObject.getString("access_token");
                int expires = rootObject.getInt("expires_in");
                parser.close();
                return Optional.of(new ClientCredentialFlowResponse(token, expires));
            }
            default -> {
                return Optional.empty();
            }
        }
    }
}
