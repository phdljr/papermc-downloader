package me.phdljr.application.port.in;

public interface QueryJarNameUseCase {

    String getJarName(String version, String build);
}
