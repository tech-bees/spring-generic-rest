package org.spring.generic.advice;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

@Data
@NoArgsConstructor
public class ErrorMessage {

    private Date timestamp;
    private String path;
    private Integer status;
    private String error;
    private List<String> details;

    public ErrorMessage(Date timestamp, String path, Integer status, String error, List<String> details) {
        this.timestamp = timestamp;
        this.path = path;
        this.status = status;
        this.error = error;
        this.details = details;
    }

    public ErrorMessage(Date timestamp, String path, Integer status, String error, String errors) {
        this.timestamp = timestamp;
        this.path = path;
        this.status = status;
        this.error = error;
        this.details = Collections.singletonList(errors);
    }

    public ErrorMessage(Date timestamp, String path, Integer status, String error, String... details) {
        this.timestamp = timestamp;
        this.path = path;
        this.status = status;
        this.error = error;
        this.details = Arrays.asList(details);
    }
}
