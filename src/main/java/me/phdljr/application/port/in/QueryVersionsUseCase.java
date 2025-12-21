package me.phdljr.application.port.in;

import java.util.List;

public interface QueryVersionsUseCase {

    List<String> getVersions() throws Exception;
}
