package me.phdljr.application.port.in;

public interface DownloadPaperUseCase {

    void download(DownloadPaperCommand command) throws Exception;
}
