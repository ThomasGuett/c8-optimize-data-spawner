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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@SpringBootApplication
@RestController
@Configuration
@EnableAutoConfiguration
public class FlowControlApp {
	private String zeebeAddress = System.getenv("ZEEBE_ADDRESS");
	private String oauthUrl = System.getenv("CAMUNDA_OAUTH_URL");
	private String clientId = System.getenv("ZEEBE_CLIENT_ID");
	private String clientSecret = System.getenv("ZEEBE_CLIENT_SECRET");
	private String jobType = System.getenv("ZEEBE_JOB_TYPE");
	OAuthCredentialsProvider credentialsProvider =
			new OAuthCredentialsProviderBuilder()
					.authorizationServerUrl(oauthUrl)
					.audience(zeebeAddress)
					.clientId(clientId)
					.clientSecret(clientSecret)
					.build();

	public ZeebeClient client = ZeebeClient.newClientBuilder()
			.gatewayAddress(zeebeAddress)
			.credentialsProvider(credentialsProvider)
			.build();

	public void createFlow() {
		// create process variables
		final String processID = "";
		final Integer spawnerInstanceNumber = ThreadLocalRandom.current().nextInt(10000, 100000);
		final String MESSAGE_ENDE = "MESSAGE_ENDE";
		final String MESSAGE_STORNO = "MESSAGE_STORNO";
		final String MESSAGE_STORNO_RETRY = "MESSAGE_STORNO_RETRY";
		final String MESSAGE_ABGELTUNGSSTEUER = "MESSAGE_ABGELTUNGSSTEUER";
		final String MESSAGE_RETRY_SENDEN = "MESSAGE_RETRY_SENDEN";
		final String MESSAGE_KORREKTUR = "MESSAGE_KORREKTUR";
		final String MESSAGE_FREIGABE = "MESSAGE_FREIGABE";
		final String MESSAGE_ERGAENZUNG = "MESSAGE_ERGAENZUNG";
		Map<String,Object> processVariables = new HashMap();
		processVariables.put("gvStatus", "ENDE");  // alternative SRST
		processVariables.put("abgeltungssteuerRelevant", true);
		processVariables.put("manuelleFreigabe", false);
		processVariables.put("failJobs", "serviceTaskFailChance");
		processVariables.put("runNumber", 0);

		processVariables.put("spawnerInstanceNumber", spawnerInstanceNumber);

		client.newCreateInstanceCommand().bpmnProcessId(processID).latestVersion().variables(processVariables).send().join();

		// send required messages to instance
		client.newPublishMessageCommand().messageName().correlationKey(spawnerInstanceNumber).send().join();
	}

	public static void main(String[] args) {
		SpringApplication.run(FlowControlApp.class, args);
	}

}
