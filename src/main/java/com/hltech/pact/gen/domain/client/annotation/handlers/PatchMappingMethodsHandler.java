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
import org.springframework.web.bind.annotation.PatchMapping;

import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@MappingMethodHandler
public class PatchMappingMethodsHandler implements AnnotatedMethodHandler {

    @Override
    public boolean isSupported(Method method) {
        return method.isAnnotationPresent(PatchMapping.class);
    }

    @Override
    public RequestRepresentation handle(Method method) {
        return RequestRepresentation.builder()
            .httpMethod(HttpMethod.PATCH)
            .path(getPathFromMethod(method))
            .headers(combineHeaders(
                method.getAnnotation(PatchMapping.class).headers(),
                RequestHeaderParamsExtractor.extractAll(method)))
            .body(RequestBodyExtractor.extract(method.getParameters()))
            .requestParameters(RequestParametersExtractor.extractAll(method))
            .pathParameters(PathParametersExtractor.extractAll(method))
            .build();
    }

    private String getPathFromMethod(Method method) {
        PatchMapping annotation = method.getAnnotation(PatchMapping.class);
        return annotation.path().length == 1 ? annotation.path()[0] : annotation.value()[0];
    }

    private static List<Param> combineHeaders(String[] rawHeaders, List<Param> headers) {
        return Stream
            .concat(RawHeadersParser.parseAll(rawHeaders).stream(), headers.stream())
            .collect(Collectors.toList());
    }
}
