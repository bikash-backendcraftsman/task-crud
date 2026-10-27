package com.technostack.taskcrud.dto;

import com.technostack.taskcrud.model.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TaskRequest(
		@NotBlank @Size(max = 120) String title,
		@Size(max = 1000) String description,
		TaskStatus status
) {
}
