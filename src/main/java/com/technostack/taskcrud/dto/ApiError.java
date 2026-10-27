package com.technostack.taskcrud.dto;

public record ApiError(int status, String error, String message) {
}
