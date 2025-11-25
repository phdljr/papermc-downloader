package me.phdljr.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class PaperApiClient {

    private static final String BASE = "https://api.papermc.io/v2/projects/paper";
    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    public List<String> getVersions() throws Exception {
        HttpRequest req = HttpRequest.newBuilder(URI.create(BASE)).GET().build();
        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
        JsonNode root = mapper.readTree(resp.body());
        List<String> versions = new ArrayList<>();
        root.get("versions").forEach(n -> versions.add(n.asText()));
        return versions;
    }

    public List<String> getBuilds(String version) throws Exception {
        HttpRequest req = HttpRequest.newBuilder(URI.create(BASE + "/versions/" + version)).GET()
            .build();
        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
        JsonNode root = mapper.readTree(resp.body());
        List<String> builds = new ArrayList<>();
        root.get("builds").forEach(n -> builds.add(n.asText()));
        return builds;
    }

    public String getDownloadUrl(String version, String build) {
        String jarName = String.format("paper-%s-%s.jar", version, build);
        return String.format("%s/versions/%s/builds/%s/downloads/%s", BASE, version, build,
            jarName);
    }

    public String getJarName(String version, String build) {
        return String.format("paper-%s-%s.jar", version, build);
    }
}

