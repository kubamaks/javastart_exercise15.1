package pl.km.exercise251;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findAllByDone(boolean done);

    List<Task> findAllByDoneOrderByDeadlineAsc(boolean done);
}
