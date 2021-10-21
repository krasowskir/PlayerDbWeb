package org.richard.home;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

class MeinServletTest {

    @Test
    public void test_servlet() {
        try {
            HttpClient client = HttpClient.newBuilder().build();
            HttpRequest request = HttpRequest.newBuilder(new URI("http://localhost:8080/mein")).GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("resp status: " + response.statusCode() + " body: " + response.body());
        } catch (IOException | InterruptedException | URISyntaxException e) {
            e.printStackTrace();
        }
    }
}