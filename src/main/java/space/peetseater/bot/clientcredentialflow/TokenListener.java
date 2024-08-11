package space.peetseater.bot.clientcredentialflow;

public interface TokenListener {
    void onNewCredential(ClientCredentialFlowResponse credential);
}
