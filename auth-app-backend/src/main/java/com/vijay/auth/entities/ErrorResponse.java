package com.vijay.auth.entities;

import org.springframework.http.HttpStatus;

public record ErrorResponse(String message, HttpStatus status,int statuscode) {
}
