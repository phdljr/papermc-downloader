package me.phdljr.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.junit.runners.Parameterized.Parameters;

public class PaperApiClientTest {

    private static final String BASE = "https://fill.papermc.io/v3/projects/paper";
    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    public void getVersions() throws Exception {
        HttpRequest req = HttpRequest.newBuilder(URI.create(BASE)).GET().build();
        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
        JsonNode root = mapper.readTree(resp.body());
        List<String> versions = new ArrayList<>();

        JsonNode versionsNode = root.get("versions");

        versionsNode.fields().forEachRemaining(entry -> {
            JsonNode arr = entry.getValue(); // 배열 노드
            arr.forEach(n -> versions.add(n.asText())); // 배열 요소 추가
        });
    }

    @Test
    public void getDownloadUrl() throws IOException, InterruptedException {
        String version = "1.21.10";
        String build = "115";
        HttpRequest req = HttpRequest.newBuilder(
                URI.create(BASE + "/versions/" + version + "/builds/" + build))
            .GET()
            .build();
        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
        JsonNode root = mapper.readTree(resp.body());
        String downloadUrl = root.get("downloads").findValue("url").asText();
    }
}