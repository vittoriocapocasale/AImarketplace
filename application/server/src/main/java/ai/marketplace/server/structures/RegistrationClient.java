package ai.marketplace.server.structures;


//class that works as interface with the client to exchange data
public class RegistrationClient {
    private String clientId;
    private String secret;

    public RegistrationClient() {
        this.clientId="";
        this.secret="";
    }

    public RegistrationClient(String clientId, String secret) {
        this.clientId = clientId;
        this.secret = secret;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }
}
