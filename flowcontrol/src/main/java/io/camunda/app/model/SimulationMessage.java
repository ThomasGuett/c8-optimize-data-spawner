package io.camunda.app.model;

import java.util.ArrayList;
import java.util.List;

public class SimulationMessage {
    private Integer messageDelay;
    private List<String> correlationKeys;
    private String messageName;

    public Integer getMessageDelay() {
        return null != messageDelay && 0<= messageDelay ? messageDelay : 0;
    }

    public void setMessageDelay(Integer messageDelay) {
        this.messageDelay = messageDelay;
    }

    public List<String> getCorrelationKeys() {
        return null != correlationKeys ? correlationKeys : new ArrayList<>();
    }

    public void setCorrelationKeys(List<String> correlationKeys) {
        this.correlationKeys = correlationKeys;
    }

    public String getMessageName() {
        return messageName;
    }

    public void setMessageName(String messageName) {
        this.messageName = messageName;
    }
}
