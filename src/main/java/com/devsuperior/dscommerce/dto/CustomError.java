package com.devsuperior.dscommerce.dto;

import java.time.Instant;

public class CustomError {

    private Instant timestamp;
    private Integer Status;
    private String error;
    private String path;

    public CustomError(Instant timestamp, Integer status, String error, String path) {
        this.timestamp = timestamp;
        Status = status;
        this.error = error;
        this.path = path;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public Integer getStatus() {
        return Status;
    }

    public String getError() {
        return error;
    }

    public String getPath() {
        return path;
    }
}
