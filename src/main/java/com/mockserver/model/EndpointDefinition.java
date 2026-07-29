package com.mockserver.model;

import java.util.Objects;

public class EndpointDefinition {

    private String method;

    private String path;

    public EndpointDefinition() {
    }

    public EndpointDefinition(String method, String path) {
        this.method = method;
        this.path = path;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EndpointDefinition other)) {
            return false;
        }
        return Objects.equals(method, other.method)
                && Objects.equals(path, other.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(method, path);
    }

    @Override
    public String toString() {
        return method + " " + path;
    }
}