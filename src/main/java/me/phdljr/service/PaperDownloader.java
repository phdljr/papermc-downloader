package me.phdljr.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

public class PaperDownloader {

    private final HttpClient client = HttpClient.newHttpClient();

    public void download(String url, File outFile) throws Exception {
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
}
