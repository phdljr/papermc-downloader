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
import me.phdljr.application.port.out.PaperApiPort;

public class PaperApiClient implements PaperApiPort {

    private static final String BASE = "https://fill.papermc.io/v3/projects/paper";
    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public List<String> getVersions() throws Exception {
        HttpRequest req = HttpRequest.newBuilder(URI.create(BASE)).GET().build();
        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
        JsonNode root = mapper.readTree(resp.body());
        List<String> versions = new ArrayList<>();
        JsonNode versionsNode = root.get("versions");

        versionsNode.fields().forEachRemaining(entry -> {
            JsonNode arr = entry.getValue();
            arr.forEach(n -> versions.add(n.asText()));
        });

        return versions;
    }

    @Override
    public List<String> getBuilds(String version) throws Exception {
        HttpRequest req = HttpRequest.newBuilder(URI.create(BASE + "/versions/" + version)).GET()
            .build();
        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
        JsonNode root = mapper.readTree(resp.body());
        List<String> builds = new ArrayList<>();
        root.get("builds").forEach(n -> builds.add(n.asText()));
        return builds;
    }

    @Override
    public String getDownloadUrl(String version, String build)
        throws IOException, InterruptedException {
        HttpRequest req = HttpRequest.newBuilder(
                URI.create(BASE + "/versions/" + version + "/builds/" + build))
            .GET()
            .build();
        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
        JsonNode root = mapper.readTree(resp.body());

        return root.get("downloads").findValue("url").asText();
    }

    @Override
    public String getJarName(String version, String build) {
        return String.format("paper-%s-%s.jar", version, build);
    }
}
