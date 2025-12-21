package me.phdljr.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.List;

public class PaperApiClient {

    private static final String BASE = "https://fill.papermc.io/v3/projects/paper";
    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

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

    public List<String> getBuilds(String version) throws Exception {
        HttpRequest req = HttpRequest.newBuilder(URI.create(BASE + "/versions/" + version)).GET()
            .build();
        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
        JsonNode root = mapper.readTree(resp.body());
        List<String> builds = new ArrayList<>();
        root.get("builds").forEach(n -> builds.add(n.asText()));
        return builds;
    }

    public String getJarName(String version, String build) {
        return String.format("paper-%s-%s.jar", version, build);
    }

    public void downloadJar(String version, String build, File outFile) throws Exception {
        String url = getDownloadUrl(version, build);
        HttpRequest req = HttpRequest.newBuilder(URI.create(url)).GET().build();
        HttpResponse<InputStream> resp = client.send(req,
            HttpResponse.BodyHandlers.ofInputStream());
        if (resp.statusCode() != 200) {
            throw new RuntimeException("다운로드 실패: HTTP " + resp.statusCode());
        }
        try (ReadableByteChannel rbc = Channels.newChannel(resp.body());
            FileOutputStream fos = new FileOutputStream(outFile)) {
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        }
    }

    private String getDownloadUrl(String version, String build)
        throws IOException, InterruptedException {
        HttpRequest req = HttpRequest.newBuilder(
                URI.create(BASE + "/versions/" + version + "/builds/" + build))
            .GET()
            .build();
        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
        JsonNode root = mapper.readTree(resp.body());

        return root.get("downloads").findValue("url").asText();
    }
}

