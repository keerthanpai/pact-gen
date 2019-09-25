package com.hltech.pact.gen;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
//import com.hltech.pact.gen.domain.client.feign.FeignClientsFinder;
import com.hltech.pact.gen.domain.client.jaxrs.JaxrsClientsFinder;
import com.hltech.pact.gen.domain.pact.PactFactory;
import com.hltech.pact.gen.domain.pact.PactJsonGenerator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.File;

@RunWith(JUnit4.class)
public class JaxRsPactGeneratorFTJava {

    static final String PACKAGE_ROOT = "com.hltech.pact.gen.testfeignclient";
    static final String PACT_DIRECTORY = "pacts/";
    static final String JAXRS_PACT_DIRECTORY = "pacts/jaxrs";
    static final String CONSUMER_NAME = "test-jaxrs-consumer";
    static final String PROVIDER_NAME = "test-provider";
    static final String SCHEMA_FILE_PATH = "src/test/resources/pact-json-schema-v1.json";

    //FeignClientsFinder feignClientsFinder = new FeignClientsFinder();
    PactFactory pactFactory = new PactFactory();
    PactJsonGenerator jsonGenerator = new PactJsonGenerator();
    //PactGenerator feignPactGenerator = new PactGenerator(feignClientsFinder, pactFactory, jsonGenerator);
    ObjectMapper mapper = new ObjectMapper();

    JsonSchemaFactory schemaFactory = JsonSchemaFactory.byDefault();

    JaxrsClientsFinder jaxrsClientsFinder = new JaxrsClientsFinder();
    PactGenerator jaxRSPactGenerator = new PactGenerator(jaxrsClientsFinder, pactFactory, jsonGenerator);

    @Before
    public void setUp() throws Exception {
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Test
    public void name() throws Exception {
        File schemaFile = new File(SCHEMA_FILE_PATH);
        JsonNode jsonNode = JsonLoader.fromFile(schemaFile);
        JsonSchema schema = schemaFactory.getJsonSchema(jsonNode);

        jaxRSPactGenerator.writePactFiles(PACKAGE_ROOT, CONSUMER_NAME, mapper, new File(JAXRS_PACT_DIRECTORY));

        File testFile = new File(JAXRS_PACT_DIRECTORY + "/" + CONSUMER_NAME + "-" + PROVIDER_NAME + "2.json");
        JsonNode testJSON = JsonLoader.fromFile(testFile);
        ProcessingReport report = schema.validate(testJSON);
        Assert.assertTrue(report.isSuccess());

    }

}
