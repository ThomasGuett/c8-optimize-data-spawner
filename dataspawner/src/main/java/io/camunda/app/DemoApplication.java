package io.camunda.app;

import io.camunda.app.service.Dataspawner;
import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.worker.JobWorker;
import io.camunda.zeebe.client.impl.oauth.OAuthCredentialsProvider;
import io.camunda.zeebe.client.impl.oauth.OAuthCredentialsProviderBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;
import java.net.URI;
import java.net.URISyntaxException;

@SpringBootApplication
@RestController
@Configuration
@EnableAutoConfiguration
public class DemoApplication {
	private String zeebeAddressString = System.getenv("ZEEBE_ADDRESS");
	private URI zeebeGrpcAddress = new URI(System.getenv("ZEEBE_GRPC_ADDRESS"));
	private String oauthUrl = System.getenv("CAMUNDA_OAUTH_URL");
	private String clientId = System.getenv("ZEEBE_CLIENT_ID");
	private String clientSecret = System.getenv("ZEEBE_CLIENT_SECRET");
	private String jobType = System.getenv("ZEEBE_JOB_TYPE");
	OAuthCredentialsProvider credentialsProvider =
			new OAuthCredentialsProviderBuilder()
					.authorizationServerUrl(oauthUrl)
					.audience(zeebeAddressString)
					.clientId(clientId)
					.clientSecret(clientSecret)
					.build();

	public ZeebeClient client = ZeebeClient.newClientBuilder()
			.grpcAddress(zeebeGrpcAddress)
			.credentialsProvider(credentialsProvider)
			.build();

	JobWorker dataspawner = client.newWorker()
			.jobType(jobType)
			.handler(new Dataspawner())
			.timeout(100000L)
			.open();

    public DemoApplication() throws URISyntaxException {
    }

    public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

}
