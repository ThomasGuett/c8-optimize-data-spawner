package io.camunda.app.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimulationRun {
    private List<Long> createdProcessInstances;
    private List<Integer> createdCorrelationKeys;
    private Map<String,Object> processVariables;
    private Integer runNumber;
    private String processId;
    private Integer instanceCount;
    private Integer instanceDelay;
    private Integer spawnerInstanceNumber;

    public List<Long> getCreatedProcessInstances() {
        return null != createdProcessInstances ? createdProcessInstances : new ArrayList<>();
    }

    public void setCreatedProcessInstances(List<Long> createdProcessInstances) {
        this.createdProcessInstances = createdProcessInstances;
    }

    public Integer getRunNumber() {
        return null != runNumber ? runNumber : 0;
    }

    public void setRunNumber(Integer runNumber) {
        this.runNumber = runNumber;
    }

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public Integer getInstanceCount() {
        return null != instanceCount && 0 < instanceCount ? instanceCount : 1;
    }

    public void setInstanceCount(Integer instanceCount) {
        this.instanceCount = instanceCount;
    }

    public Map<String, Object> getProcessVariables() {
        return null != processVariables ? processVariables : new HashMap<>();
    }

    public void setProcessVariables(Map<String, Object> processVariables) {
        this.processVariables = processVariables;
    }

    public Integer getInstanceDelay() {
        return null != instanceDelay && 0 < instanceDelay ? instanceDelay : 1000;
    }

    public void setInstanceDelay(Integer instanceDelay) {
        this.instanceDelay = instanceDelay;
    }

    public Integer getSpawnerInstanceNumber() {
        return spawnerInstanceNumber;
    }

    public void setSpawnerInstanceNumber(Integer spawnerInstanceNumber) {
        this.spawnerInstanceNumber = spawnerInstanceNumber;
    }

    public List<Integer> getCreatedCorrelationKeys() {
        return null != createdCorrelationKeys ? createdCorrelationKeys : new ArrayList<>();
    }

    public void setCreatedCorrelationKeys(List<Integer> createdCorrelationKeys) {
        this.createdCorrelationKeys = createdCorrelationKeys;
    }
}
