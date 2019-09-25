package com.hltech.pact.gen.domain.client.annotation.handlers;

import com.hltech.pact.gen.domain.client.annotation.MappingMethodHandler;
import com.hltech.pact.gen.domain.client.model.Param;
import com.hltech.pact.gen.domain.client.model.RequestRepresentation;
import com.hltech.pact.gen.domain.client.util.PathParametersExtractor;
import com.hltech.pact.gen.domain.client.util.RawHeadersParser;
import com.hltech.pact.gen.domain.client.util.RequestBodyExtractor;
import com.hltech.pact.gen.domain.client.util.RequestHeaderParamsExtractor;
import com.hltech.pact.gen.domain.client.util.RequestParametersExtractor;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.GetMapping;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@MappingMethodHandler
public class GetMappingMethodsHandler implements AnnotatedMethodHandler {

    @Override
    public boolean isSupported(Method method) {
        return method.isAnnotationPresent(GetMapping.class) || method.isAnnotationPresent(GET.class);
    }

    @Override
    public RequestRepresentation handle(Method method) {
        if (method.isAnnotationPresent(GET.class)) {
            return RequestRepresentation.builder()
                .httpMethod(HttpMethod.GET)
                .path(getPathFromMethod(method))
                .headers(Collections.emptyList())
                .body(RequestBodyExtractor.extract(method.getParameters()))
                .requestParameters(RequestParametersExtractor.extractAll(method))
                .pathParameters(PathParametersExtractor.extractAll(method))
                .build();
        }

        return RequestRepresentation.builder()
            .httpMethod(HttpMethod.GET)
            .path(getPathFromMethod(method))
            .headers(combineHeaders(
                method.getAnnotation(GetMapping.class).headers(),
                RequestHeaderParamsExtractor.extractAll(method)))
            .body(RequestBodyExtractor.extract(method.getParameters()))
            .requestParameters(RequestParametersExtractor.extractAll(method))
            .pathParameters(PathParametersExtractor.extractAll(method))
            .build();
    }

    private String getPathFromMethod(Method method) {
        Path pathAnnotation = method.getAnnotation(Path.class);
        if (pathAnnotation != null) {
            return pathAnnotation.value();
        }

        GetMapping annotation = method.getAnnotation(GetMapping.class);
        return annotation.path().length == 1 ? annotation.path()[0] : annotation.value()[0];
    }

    private static List<Param> combineHeaders(String[] rawHeaders, List<Param> headers) {
        return Stream
            .concat(RawHeadersParser.parseAll(rawHeaders).stream(), headers.stream())
            .collect(Collectors.toList());
    }
}
