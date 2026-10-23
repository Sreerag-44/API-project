package com.example.demo.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Map;

public class SubmissionRequest {

    @NotBlank(message = "form_id is required")
    private String form_id;

    @NotBlank(message = "idempotency_key is required")
    private String idempotency_key;

    @NotBlank(message = "email is required")
    @Email(message = "invalid email format")
    private String email;

    @NotNull(message = "data object is required")
    private Map<String, Object> data;

    // Getters and Setters
    public String getForm_id() { return form_id; }
    public void setForm_id(String form_id) { this.form_id = form_id; }
    public String getIdempotency_key() { return idempotency_key; }
    public void setIdempotency_key(String idempotency_key) { this.idempotency_key = idempotency_key; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Map<String, Object> getData() { return data; }
    public void setData(Map<String, Object> data) { this.data = data; }
}
