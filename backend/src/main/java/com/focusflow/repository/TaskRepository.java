package com.focusflow.repository;

import com.focusflow.entity.Task;
import com.focusflow.entity.Task.TaskStatus;
import com.focusflow.entity.Task.Priority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByStatusOrderByPriorityDescCreatedAtAsc(TaskStatus status);

    List<Task> findByPriorityOrderByCreatedAtAsc(Priority priority);

    List<Task> findByStatusInOrderByPriorityDescCreatedAtAsc(List<TaskStatus> statuses);

    long countByStatus(TaskStatus status);

    @Query("SELECT t FROM Task t WHERE t.status = 'COMPLETED' AND t.completedAt BETWEEN :start AND :end ORDER BY t.completedAt DESC")
    List<Task> findCompletedBetween(
        @Param("start") LocalDateTime start,
        @Param("end") LocalDateTime end
    );

    @Query("SELECT DATE(t.completedAt) as day, COUNT(t) as total, SUM(t.pomodoroCount) as pomodoros " +
           "FROM Task t WHERE t.status = 'COMPLETED' AND t.completedAt BETWEEN :start AND :end " +
           "GROUP BY DATE(t.completedAt) ORDER BY day")
    List<Object[]> getWeeklyStats(
        @Param("start") LocalDateTime start,
        @Param("end") LocalDateTime end
    );

    @Query("SELECT t FROM Task t WHERE LOWER(t.title) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(t.description) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "ORDER BY t.createdAt DESC")
    List<Task> searchTasks(@Param("query") String query);
}