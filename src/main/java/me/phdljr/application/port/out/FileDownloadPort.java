package me.phdljr.application.port.out;

import java.io.File;

public interface FileDownloadPort {

    void download(String url, File outFile) throws Exception;
}
