package io.dropwizard.jersey.optional;

import io.dropwizard.jersey.AbstractJerseyTest;
import io.dropwizard.jersey.DropwizardResourceConfig;
import org.junit.jupiter.api.Test;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class OptionalMessageBodyWriterTest extends AbstractJerseyTest {

    @Override
    protected Application configure() {
        return DropwizardResourceConfig.forTesting()
                .register(new EmptyOptionalExceptionMapper())
                .register(OptionalReturnResource.class);
    }

    @Test
    void presentOptionalsReturnTheirValue() {
        assertThat(target("optional-return")
                .queryParam("id", "woo").request()
                .get(String.class))
                .isEqualTo("woo");
    }

    @Test
    void presentOptionalsReturnTheirValueWithResponse() {
        assertThat(target("optional-return/response-wrapped")
                .queryParam("id", "woo").request()
                .get(String.class))
                .isEqualTo("woo");
    }

    @Test
    void absentOptionalsThrowANotFound() {
        Invocation.Builder request = target("optional-return").request();
        assertThatExceptionOfType(WebApplicationException.class)
            .isThrownBy(() -> request.get(String.class))
            .satisfies(e -> assertThat(e.getResponse().getStatus()).isEqualTo(404));
    }

    @Path("optional-return")
    @Produces(MediaType.TEXT_PLAIN)
    public static class OptionalReturnResource {
        @GET
        public Optional<String> showWithQueryParam(@QueryParam("id") String id) {
            return Optional.ofNullable(id);
        }

        @POST
        public Optional<String> showWithFormParam(@FormParam("id") String id) {
            return Optional.ofNullable(id);
        }

        @Path("response-wrapped")
        @GET
        public Response showWithQueryParamResponse(@QueryParam("id") String id) {
            return Response.ok(Optional.ofNullable(id)).build();
        }
    }
}
