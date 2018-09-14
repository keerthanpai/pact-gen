package dev.hltech.pact.generation.domain.client.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpMethod;

import java.util.List;

@Data
@Builder
public class RequestProperties {

    private final HttpMethod httpMethod;
    private final String path;
    private final List<Param> headers;
    private final Body body;
    private final List<Param> requestParameters;
    private final List<Param> pathParameters;
}
