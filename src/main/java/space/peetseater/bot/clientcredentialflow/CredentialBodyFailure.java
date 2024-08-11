package space.peetseater.bot.clientcredentialflow;

class CredentialBodyFailure extends RuntimeException {
    public CredentialBodyFailure(String msg) {
        super(msg);
    }
}
