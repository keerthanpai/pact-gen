package com.hltech.pact.gen.testfeignclient;

import com.hltech.pact.gen.domain.client.feign.InteractionInfo;
import com.hltech.pact.gen.domain.client.jaxrs.JaxRSClient;
import org.springframework.http.HttpStatus;

import javax.ws.rs.POST;
import javax.ws.rs.Path;

import javax.ws.rs.GET;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import java.util.Optional;

@Path("test-url")
@JaxRSClient(name = "test-provider2")
public interface TestJaxRsClient {

    @GET
    @Path("/{integerClassPathVariable}/{booleanPathVariable}/}")
    @InteractionInfo(responseStatus = HttpStatus.OK)
    Optional<TestDto> getTestDtoAPI(
        @PathParam("integerClassPathVariable") Integer integerClassPathVariable,
        @PathParam("booleanPathVariable") boolean booleanPathVariable,
        @QueryParam("floatRequestParam") float floatRequestParam,
        @QueryParam("stringRequestParam") String stringRequestParam
    );

    @POST
    @Path("/")
    @InteractionInfo(responseStatus = HttpStatus.OK)
    GenericDto<TestDto> postTestDtoAPI(TestDto testDto);

}
