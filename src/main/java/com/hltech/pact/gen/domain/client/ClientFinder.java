package com.hltech.pact.gen.domain.client;

import java.util.Set;

public interface ClientFinder {

    public Set<Class<?>> findClients(String packageRoot);
}
