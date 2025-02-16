package com.hltech.pact.gen;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.hltech.pact.gen.domain.client.ClientFinder;
import com.hltech.pact.gen.domain.client.feign.FeignClientsFinder;
import com.hltech.pact.gen.domain.client.jaxrs.JaxrsClientsFinder;
import com.hltech.pact.gen.domain.pact.PactFactory;
import com.hltech.pact.gen.domain.pact.PactJsonGenerator;
import com.hltech.pact.gen.domain.pact.Service;
import com.hltech.pact.gen.domain.pact.model.Pact;

import javax.validation.constraints.NotNull;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class PactGenerator {

    private final ClientFinder clientFinder;
    private final PactFactory pactFactory;
    private final PactJsonGenerator pactJsonGenerator;

    public PactGenerator() {
        this.clientFinder = new FeignClientsFinder();
        this.pactFactory = new PactFactory();
        this.pactJsonGenerator = new PactJsonGenerator();
    }

    public PactGenerator(FeignClientsFinder feignClientsFinder,
                         PactFactory pactFactory,
                         PactJsonGenerator pactJsonGenerator) {
        this.clientFinder = feignClientsFinder;
        this.pactFactory = pactFactory;
        this.pactJsonGenerator = pactJsonGenerator;
    }

    public PactGenerator(JaxrsClientsFinder jaxrsClientsFinder,
                         PactFactory pactFactory,
                         PactJsonGenerator pactJsonGenerator) {
        this.clientFinder = jaxrsClientsFinder;
        this.pactFactory = pactFactory;
        this.pactJsonGenerator = pactJsonGenerator;
    }

    public void writePactFiles(@NotNull String packageRoot,
                               @NotNull String consumerName,
                               @NotNull ObjectMapper objectMapper) {
        this.write(packageRoot, consumerName, objectMapper, null);
    }

    public void writePactFiles(@NotNull String packageRoot,
                               @NotNull String consumerName,
                               @NotNull ObjectMapper objectMapper,
                               @NotNull File pactFilesDestinationDir) {
        this.write(packageRoot, consumerName, objectMapper, pactFilesDestinationDir);
    }

    private void write(String packageRoot, String consumerName, ObjectMapper mapper, File pactFilesDestinationDir) {
        Multimap<Service, Pact> providerToPactMap = generatePacts(packageRoot, consumerName, mapper);

        List<Pact> pacts = providerToPactMap.keySet().stream()
            .map(providerToPactMap::get)
            .map(this::combinePactsToOne)
            .collect(Collectors.toList());

        pactJsonGenerator.writePactFiles(pactFilesDestinationDir, pacts);
    }

    private Multimap<Service, Pact> generatePacts(String packageRoot, String consumerName, ObjectMapper objectMapper) {
        Multimap<Service, Pact> providerToPactMap = HashMultimap.create();

        clientFinder.findClients(packageRoot).stream()
            .map(feignClient -> pactFactory.createFromFeignClient(feignClient, consumerName, objectMapper))
            .forEach(pact -> providerToPactMap.put(pact.getProvider(), pact));

        return providerToPactMap;
    }

    private Pact combinePactsToOne(Collection<Pact> pacts) {
        if (pacts == null || pacts.isEmpty()) {
            return null;
        }

        Pact referencePact = pacts.iterator().next();

        Pact combinedPact = Pact.builder()
                                .metadata(referencePact.getMetadata())
                                .consumer(referencePact.getConsumer())
                                .provider(referencePact.getProvider())
                                .interactions(new ArrayList<>())
                                .build();

        pacts.forEach(pact -> combinedPact.getInteractions().addAll(pact.getInteractions()));

        return combinedPact;
    }

}
