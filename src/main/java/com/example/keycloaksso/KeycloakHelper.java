package com.example.keycloaksso;

import com.example.keycloaksso.model.KeycloakAccessTokenPayload;
import com.example.keycloaksso.model.UserPayload;
import java.io.IOException;
import java.util.Objects;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class KeycloakHelper {
    private final OkHttpClient client;

    public KeycloakHelper(OkHttpClient httpClient) {
        this.client = httpClient;
    }

    public Response jsonRequest(String url, String payload) throws IOException {
        MediaType mediaType = MediaType.parse("application/json");
        var body = RequestBody.create(mediaType, payload);
        var request = new Request.Builder().url(url).method("POST", body).addHeader("Content-Type", "application/json").addHeader("Authorization", java.lang.String.format("Bearer %s", getAdminAccessToken().access_token)).build();
        var res = client.newCall(request).execute();

        System.out.println(java.lang.String.format("jsonRequest response = %s", res.toString()));

        return res;
    }

    public String createUser(UserPayload user) throws IOException {
        var url = "http://localhost:9999/auth/admin/realms/UniHeart/users";
        var payload = String.format("{\"firstName\":\"Sergey\",\"lastName\":\"Kargopolov\", \"email\":\"%s\", \"enabled\":\"true\", \"username\":\"%s\", \"credentials\":[{\"type\":\"password\",\"value\":\"%s\",\"temporary\":false}]}", user.getEmail(), user.getUsername(), user.getPassword());
        return this.jsonRequest(url, payload).toString();
    }

    public String assignRole(String userId) throws IOException {
        System.out.println(java.lang.String.format("assigning role for user = %s", userId));
        var clientId = "98ea8f07-a7f2-4607-ab56-b5208a90eaa1";
        var url = java.lang.String.format("http://localhost:9999/auth/admin/realms/UniHeart/users/%s/role-mappings/clients/%s", userId, clientId);
        var payload = java.lang.String.format("[{\"id\": \"bef4bf69-371b-460a-8a0c-b2943da1983b\",\"name\":\"visitor\",\"description\":\"add roles programatically\",\"composite\":false,\"clientRole\":true,\"containerId\":\"%s\"}]", clientId);

        System.out.println(java.lang.String.format("payload = %s", payload));

        return this.jsonRequest(url, payload).toString();
    }

    public KeycloakAccessTokenPayload getAdminAccessToken() throws IOException {
        var username = System.getenv("KC_ADMIN");
        var password = System.getenv("KC_PASSWORD");

        System.out.println(java.lang.String.format("username = %s; password = %s", username, password));

        var mediaType = MediaType.parse("application/x-www-form-urlencoded");
        var body = RequestBody.create(mediaType, java.lang.String.format("username=%s&password=%s&grant_type=password&client_id=admin-cli", username, password));
        var request = new Request.Builder()
                .url("http://localhost:9999/auth/realms/master/protocol/openid-connect/token")
                .method("POST", body)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();
        var response = client.newCall(request).execute();

        var s = Objects.requireNonNull(response.body()).string();

        System.out.println(String.format("token response = %s", s));

        return JsonHelper.parseFrom(s);
    }

    public java.lang.String assignRealmRole(String userId) throws IOException {
        System.out.println(java.lang.String.format("assigning realm role for user = %s", userId));
        var clientId = "98ea8f07-a7f2-4607-ab56-b5208a90eaa1";
        var url = String.format("http://localhost:9999/auth/admin/realms/UniHeart/users/%s/role-mappings/realm", userId);
        var payload = String.format("[{\"id\": \"5e47a34a-5c22-457f-af3f-e5dea7b06839\"," +
                "\"name\":\"offline_access\",\"description\":\"add roles programatically\",\"composite\":false,\"clientRole\":false,\"containerId\":\"%s\"}]", clientId);

        System.out.println(String.format("url = %s, with payload = %s", url, payload));

        return this.jsonRequest(url, payload).toString();
    }
}