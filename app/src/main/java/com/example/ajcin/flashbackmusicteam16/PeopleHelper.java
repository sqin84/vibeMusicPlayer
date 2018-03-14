package com.example.ajcin.flashbackmusicteam16;

        import android.content.Context;

        import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
        import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
        import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
        import com.google.api.client.http.HttpTransport;
        import com.google.api.client.http.javanet.NetHttpTransport;
        import com.google.api.client.json.jackson2.JacksonFactory;
        import com.google.api.services.people.v1.People;

        import java.io.IOException;

public class PeopleHelper {

    private static final String APPLICATION_NAME = "Flashback App";


    public static People setUp(Context context, String serverAuthCode) throws IOException {
        HttpTransport httpTransport = new NetHttpTransport();
        JacksonFactory jsonFactory = JacksonFactory.getDefaultInstance();

        // Redirect URL for web based applications.
        // Can be empty too.
        String redirectUrl = "urn:ietf:wg:oauth:2.0:oob";


        // Exchange auth code for access token
        GoogleTokenResponse tokenResponse = new GoogleAuthorizationCodeTokenRequest(
                httpTransport,
                jsonFactory,
                Constants.WEB_CLIENT_ID,
                Constants.WEB_CLIENT_SECRET,
                serverAuthCode,
                redirectUrl).execute();

        // Then, create a GoogleCredential object using the tokens from GoogleTokenResponse
        GoogleCredential credential = new GoogleCredential.Builder()
                .setClientSecrets(Constants.WEB_CLIENT_ID, Constants.WEB_CLIENT_SECRET)
                .setTransport(httpTransport)
                .setJsonFactory(jsonFactory)
                .build();

        credential.setFromTokenResponse(tokenResponse);

        // credential can then be used to access Google services
        return new People.Builder(httpTransport, jsonFactory, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

}
