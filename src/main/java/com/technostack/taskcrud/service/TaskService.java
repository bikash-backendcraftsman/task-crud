package com.technostack.taskcrud.service;

import com.technostack.taskcrud.dto.TaskRequest;
import com.technostack.taskcrud.dto.TaskResponse;
import com.technostack.taskcrud.exception.TaskNotFoundException;
import com.technostack.taskcrud.model.Task;
import com.technostack.taskcrud.model.TaskStatus;
import com.technostack.taskcrud.repository.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TaskService {

	private final TaskRepository taskRepository;

	public TaskService(TaskRepository taskRepository) {
		this.taskRepository = taskRepository;
	}

	public TaskResponse create(TaskRequest request) {
		Task task = new Task();
		apply(task, request, true);
		return TaskResponse.from(taskRepository.save(task));
	}

	@Transactional(readOnly = true)
	public List<TaskResponse> findAll() {
		return taskRepository.findAll().stream().map(TaskResponse::from).toList();
	}

	@Transactional(readOnly = true)
	public TaskResponse findById(Long id) {
		return TaskResponse.from(getTask(id));
	}

	public TaskResponse update(Long id, TaskRequest request) {
		Task task = getTask(id);
		apply(task, request, false);
		return TaskResponse.from(taskRepository.save(task));
	}

	public void delete(Long id) {
		if (!taskRepository.existsById(id)) {
			throw new TaskNotFoundException(id);
		}
		taskRepository.deleteById(id);
	}

	private Task getTask(Long id) {
		return taskRepository.findById(id).orElseThrow(() -> new TaskNotFoundException(id));
	}

	private void apply(Task task, TaskRequest request, boolean creating) {
		task.setTitle(request.title().trim());
		task.setDescription(request.description());
		if (request.status() != null) {
			task.setStatus(request.status());
		} else if (creating) {
			task.setStatus(TaskStatus.TODO);
		}
	}
}
