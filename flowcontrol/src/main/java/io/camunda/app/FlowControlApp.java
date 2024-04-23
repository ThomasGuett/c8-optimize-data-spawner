package io.camunda.app;

import io.camunda.app.model.SimulationMessage;
import io.camunda.app.model.SimulationRun;
import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;
import io.camunda.zeebe.client.api.response.ProcessInstanceResult;
import io.camunda.zeebe.client.impl.oauth.OAuthCredentialsProvider;
import io.camunda.zeebe.client.impl.oauth.OAuthCredentialsProviderBuilder;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
@RestController
@Configuration
@EnableAutoConfiguration
public class FlowControlApp {
	private String zeebeAddress = System.getenv("ZEEBE_ADDRESS");
	private String oauthUrl = System.getenv("CAMUNDA_OAUTH_URL");
	private String clientId = System.getenv("ZEEBE_CLIENT_ID");
	private String clientSecret = System.getenv("ZEEBE_CLIENT_SECRET");
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

	@PostMapping(value = "/sendMessages",
	consumes = MediaType.APPLICATION_JSON_VALUE,
	produces = MediaType.APPLICATION_JSON_VALUE)
	public SimulationMessage sendMessage(@RequestBody SimulationMessage simulationMessage, HttpServletRequest request) {
		System.out.println("received request to send messages to process engine");
		if(null != simulationMessage.getMessageName()) {
			simulationMessage.getCorrelationKeys().forEach(key -> {
				try {
					TimeUnit.MILLISECONDS.sleep(simulationMessage.getMessageDelay());
				} catch(InterruptedException e) {
					System.out.println("ERROR: interrupt exception: " + e.getMessage());
				}
				client.newPublishMessageCommand().messageName(simulationMessage.getMessageName()).correlationKey(key).send().join();
			});
		}
		return simulationMessage;
	}

	@PostMapping(path = "/createFlow",
	consumes = MediaType.APPLICATION_JSON_VALUE,
	produces = MediaType.APPLICATION_JSON_VALUE)
	public SimulationRun createFlow(@RequestBody SimulationRun simulationRun, HttpServletRequest request) {
		// create process variables
		System.out.println("received request to start new flow");
		final String processID = simulationRun.getProcessId();
		if(null != processID && !processID.isEmpty()) {
			System.out.println("creating instance for: " + processID);
			// new logic using rest request data
			final Integer iterations = simulationRun.getInstanceCount();
			final Integer instanceDelay = simulationRun.getInstanceDelay();

			// track created instances
			List<Long> createdInstances = new ArrayList<>();
			List<Integer> createdCorrelationKeys = new ArrayList<>();

			for (int i = 1; i <= iterations; i++) {
				Map<String,Object> processVariables = simulationRun.getProcessVariables();
				Integer spawnerInstanceNumber = ThreadLocalRandom.current().nextInt(10000, 100000);
				simulationRun.setSpawnerInstanceNumber(spawnerInstanceNumber);
				processVariables.put("spawnerInstanceNumber", spawnerInstanceNumber);
				createdCorrelationKeys.add(spawnerInstanceNumber);
				System.out.println("simulation run: " + simulationRun.getRunNumber() + " instance: " + i + " / " + iterations);
				ProcessInstanceEvent processInstance = client.newCreateInstanceCommand().bpmnProcessId(processID).latestVersion().variables(processVariables).send().join();
				createdInstances.add(processInstance.getProcessInstanceKey());
				try {
					TimeUnit.MILLISECONDS.sleep(instanceDelay);
				} catch (InterruptedException e) {
					System.out.println("ERROR: interrupt exception for instance spawn delay");
				}
			}

			//client.newCreateInstanceCommand().bpmnProcessId(processID).latestVersion().variables(simulationRun.getProcessVariables()).send().join();


			// send required messages to instance
			//client.newPublishMessageCommand().messageName().correlationKey(spawnerInstanceNumber).send().join();
			simulationRun.setCreatedProcessInstances(createdInstances);
			simulationRun.setCreatedCorrelationKeys(createdCorrelationKeys);
		}
		return simulationRun;
	}

	public static void main(String[] args) {
		SpringApplication.run(FlowControlApp.class, args);
	}

}
