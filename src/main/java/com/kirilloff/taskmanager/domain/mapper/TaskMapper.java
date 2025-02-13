package com.kirilloff.taskmanager.domain.mapper;

import com.kirilloff.taskmanager.domain.entity.Task;
import com.kirilloff.taskmanager.domain.request.TaskRequestDTO;
import com.kirilloff.taskmanager.domain.response.TaskResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface TaskMapper {

  @Mapping(target = "id", ignore = true)
  void updateTaskFromDto(TaskRequestDTO dto, @MappingTarget Task task);

  TaskResponseDTO toDto(Task task);
}
