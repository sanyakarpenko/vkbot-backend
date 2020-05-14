package com.sanyakarpenko.vkbot.services.impl;

import com.sanyakarpenko.vkbot.entities.User;
import com.sanyakarpenko.vkbot.types.TaskStatus;
import com.sanyakarpenko.vkbot.utils.Helper;
import com.sanyakarpenko.vkbot.entities.Task;
import com.sanyakarpenko.vkbot.repositories.TaskRepository;
import com.sanyakarpenko.vkbot.repositories.UserRepository;
import com.sanyakarpenko.vkbot.services.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TaskServiceImpl implements TaskService {
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;

    public TaskServiceImpl(UserRepository userRepository, TaskRepository taskRepository) {
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
    }

    @Override
    public Task findTaskById(Long id) {
        Optional<Task> taskOptional = taskRepository.findById(id);

        if(taskOptional.isPresent()) {
            Task task = taskOptional.get();

            if(task.getStatus() == TaskStatus.DELETED) {
                return null;
            }

            log.info("IN findTaskById - {} task found by id: {}", task, id);
            return task;
        }

        log.warn("IN findTaskById - no task found by id: {}", id);
        return null;
    }

    @Override
    public List<Task> findTasksByUsername(String username) {
        List<Task> tasks = taskRepository.findAllByUserUsername(username)
                .stream()
                .filter(task -> task.getStatus() != TaskStatus.DELETED)
                .collect(Collectors.toList());

        log.info("IN findTasksByUsername - {} tasks found", tasks.size());
        return tasks;
    }

    @Override
    public List<Task> findTasksByCurrentUser() {
        List<Task> tasks = findTasksByUsername(Helper.getUsername())
                .stream()
                .filter(task -> task.getStatus() != TaskStatus.DELETED)
                .collect(Collectors.toList());

        log.info("IN findTasksByCurrentUser - {} tasks found", tasks.size());
        return tasks;
    }

    @Override
    public Task saveTask(Task task) {
        Task savedTask = taskRepository.save(task);
        log.info("IN saveTask - task : {} successfully saved", savedTask);

        return savedTask;
    }

    @Override
    public Task addTask(Task task) {
        User user = userRepository.findByUsername(Helper.getUsername());

        task.setUser(user);

        Task addedTask = taskRepository.save(task);
        log.info("IN addTask - task : {} successfully added", addedTask);

        return addedTask;
    }
}
