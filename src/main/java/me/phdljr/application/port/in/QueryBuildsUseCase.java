package me.phdljr.application.port.in;

import java.util.List;

public interface QueryBuildsUseCase {

    List<String> getBuilds(String version) throws Exception;
}
