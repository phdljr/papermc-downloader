package me.phdljr.application.port.in;

import java.io.File;

public class DownloadPaperCommand {

    private final String version;
    private final String build;
    private final File destination;
    private final String memory;
    private final boolean createStartFile;

    public DownloadPaperCommand(String version, String build, File destination, String memory, boolean createStartFile) {
        this.version = version;
        this.build = build;
        this.destination = destination;
        this.memory = memory;
        this.createStartFile = createStartFile;
    }

    public String getVersion() {
        return version;
    }

    public String getBuild() {
        return build;
    }

    public File getDestination() {
        return destination;
    }

    public String getMemory() {
        return memory;
    }

    public boolean isCreateStartFile() {
        return createStartFile;
    }
}
