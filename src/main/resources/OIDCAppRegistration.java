
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.IdTokenClaimNames;

@Configuration
public class OIDCAppRegistration {

    private static String CLIENT_REGISTRATION = "spring.security.oauth2.client.registration.wso2.";
    private static String WSO2_IS_PROPERTY = "spring.security.oauth2.client.provider.wso2.";
    @Autowired
    private Environment env;

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {

        return new InMemoryClientRegistrationRepository(this.WSO2ClientRegistration());
    }

    private ClientRegistration WSO2ClientRegistration() {

        ClientRegistration registration = ClientRegistration.withRegistrationId("wso2")
                .clientId(env.getProperty(CLIENT_REGISTRATION + "client-id"))
                .clientSecret(env.getProperty(CLIENT_REGISTRATION + "client-secret"))
                .clientAuthenticationMethod(ClientAuthenticationMethod.BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUriTemplate(env.getProperty(CLIENT_REGISTRATION + "redirect-uri"))
                .scope(env.getProperty(CLIENT_REGISTRATION + "scope"))
                .authorizationUri(env.getProperty(WSO2_IS_PROPERTY + "authorization-uri"))
                .tokenUri(env.getProperty(WSO2_IS_PROPERTY + "token-uri"))
                .userInfoUri(env.getProperty(WSO2_IS_PROPERTY + "user-info-uri"))
                .userNameAttributeName(IdTokenClaimNames.SUB)
                .jwkSetUri(env.getProperty(WSO2_IS_PROPERTY + "jwk-set-uri"))
                .clientName(env.getProperty(CLIENT_REGISTRATION + "client-name"))
                .build();
        return registration;
    }
}