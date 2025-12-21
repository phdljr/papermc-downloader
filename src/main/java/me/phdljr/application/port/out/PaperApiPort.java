package me.phdljr.application.port.out;

import java.util.List;

public interface PaperApiPort {

    List<String> getVersions() throws Exception;

    List<String> getBuilds(String version) throws Exception;

    String getDownloadUrl(String version, String build) throws Exception;

    String getJarName(String version, String build);
}
