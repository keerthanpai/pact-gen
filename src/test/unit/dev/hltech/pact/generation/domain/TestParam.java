package dev.hltech.pact.generation.domain;

public class TestParam {
    private String testField;

    public void setTestField(String testField) {
        this.testField = testField;
    }

    public String getTestField() {
        return testField;
    }

    @Override
    public String toString() {
        return testField;
    }
}
