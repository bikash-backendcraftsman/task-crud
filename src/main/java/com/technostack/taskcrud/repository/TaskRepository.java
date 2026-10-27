package com.technostack.taskcrud.repository;

import com.technostack.taskcrud.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {
}
