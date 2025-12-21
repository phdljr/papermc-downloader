package me.phdljr.application.service;

import java.util.List;
import me.phdljr.application.port.in.DownloadPaperCommand;
import me.phdljr.application.port.in.DownloadPaperUseCase;
import me.phdljr.application.port.in.QueryBuildsUseCase;
import me.phdljr.application.port.in.QueryJarNameUseCase;
import me.phdljr.application.port.in.QueryVersionsUseCase;
import me.phdljr.application.port.out.FileDownloadPort;
import me.phdljr.application.port.out.PaperApiPort;
import me.phdljr.application.port.out.StartFilePort;

public class PaperDownloadService implements DownloadPaperUseCase, QueryVersionsUseCase,
    QueryBuildsUseCase, QueryJarNameUseCase {

    private final PaperApiPort paperApiPort;
    private final FileDownloadPort fileDownloadPort;
    private final StartFilePort startFilePort;

    public PaperDownloadService(PaperApiPort paperApiPort, FileDownloadPort fileDownloadPort,
        StartFilePort startFilePort) {
        this.paperApiPort = paperApiPort;
        this.fileDownloadPort = fileDownloadPort;
        this.startFilePort = startFilePort;
    }

    @Override
    public List<String> getVersions() throws Exception {
        return paperApiPort.getVersions();
    }

    @Override
    public List<String> getBuilds(String version) throws Exception {
        return paperApiPort.getBuilds(version);
    }

    @Override
    public String getJarName(String version, String build) {
        return paperApiPort.getJarName(version, build);
    }

    @Override
    public void download(DownloadPaperCommand command) throws Exception {
        String downloadUrl = paperApiPort.getDownloadUrl(command.getVersion(), command.getBuild());
        fileDownloadPort.download(downloadUrl, command.getDestination());
        if (command.isCreateStartFile()) {
            startFilePort.createStartFile(command.getDestination(), command.getMemory());
        }
    }
}
