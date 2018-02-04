package com.erayzal.springbootsandbox.eureka;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.support.BasicAuthorizationInterceptor;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext
@ActiveProfiles("test")
public class EurekaSecurityTests {

	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;

	@Test
	public void anonymous_access_to_home_should_be_authorized() throws Exception {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.TEXT_HTML));
		ResponseEntity<String> entity = this.restTemplate.exchange("/", HttpMethod.GET,
				new HttpEntity<Void>(headers), String.class);
		assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);
	}

    @Test
    public void anonymous_access_to_eureka_should_be_unauthorized() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

        ResponseEntity<String> entity = this.restTemplate.exchange("/eureka/apps", HttpMethod.GET,
                new HttpEntity<Void>(headers), String.class);
        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void anonymous_access_to_actuator_should_be_unauthorized() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

        ResponseEntity<String> entity = this.restTemplate.exchange("/actuator/env", HttpMethod.GET,
                new HttpEntity<Void>(headers), String.class);
        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void authenticated_access_to_actuator_should_return_information() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        BasicAuthorizationInterceptor basicAuthInterceptor = new BasicAuthorizationInterceptor(
                "microservice", "pwd");
        try {
            this.restTemplate.getRestTemplate().getInterceptors().add(basicAuthInterceptor);
            ResponseEntity<String> entity = this.restTemplate.exchange("/actuator/env", HttpMethod.GET,
                    new HttpEntity<Void>(headers), String.class);
            assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(entity.getBody()).contains("\"local.server.port\"");
        } finally {
            this.restTemplate.getRestTemplate().getInterceptors().remove(basicAuthInterceptor);
        }
    }
    @Test
    public void authenticated_access_to_eureka_should_return_applications_information() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        BasicAuthorizationInterceptor basicAuthInterceptor = new BasicAuthorizationInterceptor(
                "microservice", "pwd");
        try {
            this.restTemplate.getRestTemplate().getInterceptors().add(basicAuthInterceptor);
            ResponseEntity<String> entity = this.restTemplate.exchange("/eureka/apps", HttpMethod.GET,
                    new HttpEntity<Void>(headers), String.class);
            assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(entity.getBody()).contains("\"applications\"");
        } finally {
            this.restTemplate.getRestTemplate().getInterceptors().remove(basicAuthInterceptor);
        }
    }
}
