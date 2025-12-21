package me.phdljr.application.port.out;

import java.io.File;

public interface StartFilePort {

    void createStartFile(File jarFile, String memory) throws Exception;
}
