package com.hltech.pact.gen.domain.client.util;

import com.hltech.pact.gen.domain.client.model.Param;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ValueConstants;

import javax.ws.rs.QueryParam;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public final class RequestParametersExtractor {

    private RequestParametersExtractor() {}

    public static List<Param> extractAll(Method clientMethod) {
        if (clientMethod.isAnnotationPresent(QueryParam.class)) {
            return Arrays.stream(clientMethod.getParameters())
                .filter(param -> param.getAnnotation(QueryParam.class) != null)
                .map(RequestParametersExtractor::extractJaxrsParam)
                .collect(Collectors.toList());
        }

        return Arrays.stream(clientMethod.getParameters())
            .filter(param -> param.getAnnotation(RequestParam.class) != null)
            .filter(param -> param.getType() != Map.class)
            .map(RequestParametersExtractor::extract)
            .collect(Collectors.toList());
    }

    private static Param extract(Parameter param) {
        Param.ParamBuilder builder = Param.builder();

        extractParamDefaultValue(param).ifPresent(builder::defaultValue);

        List<Class<?>> paramTypes = TypeExtractor.extractParameterTypesFromType(param.getParameterizedType());

        return builder
            .name(extractParamName(param))
            .type(param.getType())
            .genericArgumentType(paramTypes.isEmpty() ? null : paramTypes.get(0))
            .build();
    }

    private static Param extractJaxrsParam(Parameter param) {
        Param.ParamBuilder builder = Param.builder();

        List<Class<?>> paramTypes = TypeExtractor.extractParameterTypesFromType(param.getParameterizedType());

        return builder
            .name(extractJaxRsParamName(param))
            .type(param.getType())
            .genericArgumentType(paramTypes.isEmpty() ? null : paramTypes.get(0))
            .build();
    }

    private static Optional<Object> extractParamDefaultValue(Parameter param) {
        RequestParam annotation = param.getAnnotation(RequestParam.class);

        if (annotation.defaultValue().equals(ValueConstants.DEFAULT_NONE) || annotation.defaultValue().isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(annotation.defaultValue());
    }

    private static String extractParamName(Parameter param) {
        RequestParam annotation = param.getAnnotation(RequestParam.class);

        if (!annotation.name().isEmpty()) {
            return annotation.name();
        } else if (!annotation.value().isEmpty()) {
            return annotation.value();
        }

        return param.getName();
    }

    private static String extractJaxRsParamName(Parameter param) {
        QueryParam annotation = param.getAnnotation(QueryParam.class);

        if (!annotation.value().isEmpty()) {
            return annotation.value();
        }

        return param.getName();
    }
}
