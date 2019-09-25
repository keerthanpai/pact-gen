package com.hltech.pact.gen.domain.client.feign;

import com.hltech.pact.gen.domain.client.ClientFinder;
import org.reflections.Reflections;
import org.springframework.cloud.openfeign.FeignClient;

import java.util.Set;
import java.util.stream.Collectors;

public class FeignClientsFinder implements ClientFinder {

    @Override
    public Set<Class<?>> findClients(String packageRoot) {
        return new Reflections(packageRoot).getTypesAnnotatedWith(FeignClient.class).stream()
            .filter(feignClient -> !feignClient.isAnnotationPresent(ExcludeFeignClient.class))
            .collect(Collectors.toSet());
    }
}
