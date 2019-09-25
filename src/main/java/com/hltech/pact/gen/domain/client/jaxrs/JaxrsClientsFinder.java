package com.hltech.pact.gen.domain.client.jaxrs;

import com.hltech.pact.gen.domain.client.ClientFinder;
import org.reflections.Reflections;

import java.util.Set;
import java.util.stream.Collectors;

public class JaxrsClientsFinder implements ClientFinder {

    @Override
    public Set<Class<?>> findClients(String packageRoot) {
        return new Reflections(packageRoot).getTypesAnnotatedWith(JaxRSClient.class).stream()
            .collect(Collectors.toSet());
    }
}
