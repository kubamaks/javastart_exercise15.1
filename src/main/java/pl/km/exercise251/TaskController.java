package pl.km.exercise251;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
public class TaskController {
    TaskRepository taskRepository;

    public TaskController(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @GetMapping("/")
    public String home(Model model) {
        Boolean isDone = false;
        List<Task> taskList = taskRepository.findAllByDoneOrderByDeadlineAsc(isDone);
        model.addAttribute("taskList", taskList);
        model.addAttribute("compareDate", LocalDate.now());
        model.addAttribute("isDone", isDone);
        model.addAttribute("heading", ApplicationStatics.HOME_PAGE_HEADING);
        return "index";
    }

    @GetMapping("/archiwum")
    public String archive(Model model) {
        Boolean isDone = true;
        List<Task> taskList = taskRepository.findAllByDone(isDone);
        model.addAttribute("taskList", taskList);
        model.addAttribute("compareDate", LocalDate.now().minusYears(100L));
        model.addAttribute("isDone", isDone);
        model.addAttribute("heading", ApplicationStatics.ARCHIVE_PAGE_HEADING);
        return "index";
    }

    @GetMapping("/zadanie/{id}/zmienstatus")
    public String changeStatus(@PathVariable Long id) {
        Task task = taskRepository.findById(id).orElseThrow();
        if (task.isDone()) {
            task.setDone(false);
            task.setExecutionDate(null);
            taskRepository.save(task);
            return "redirect:/";
        } else {
            task.setDone(true);
            task.setExecutionDate(LocalDate.now());
            taskRepository.save(task);
            return "redirect:/archiwum";
        }
    }

    @GetMapping("/nowe")
    public String newTask(Model model) {
        model.addAttribute("task", new Task());
        return "form";
    }

    @GetMapping("/zadanie/{id}/edytuj")
    public String editTask(@PathVariable Long id, Model model) {
        Optional<Task> taskOptional = taskRepository.findById(id);
        if (taskOptional.isPresent()) {
            Task task = taskOptional.get();
            model.addAttribute("task", task);
            return "form";
        } else {
            return "redirect:/";
        }
    }

    @PostMapping("/zapisz")
    public String saveTask(Task task) {
        if (task.getId() == null) {
            task.setCreationDate(LocalDate.now());
            taskRepository.save(task);
        } else {
            Task taskToChange = taskRepository.findById(task.getId()).orElseThrow();
            taskToChange.setName(task.getName());
            taskToChange.setCreationDate(task.getCreationDate());
            taskToChange.setDeadline(task.getDeadline());
            taskToChange.setDone(task.isDone());
            if (task.isDone()) {
                taskToChange.setExecutionDate(task.getExecutionDate());
            }
            taskRepository.saveAndFlush(taskToChange);
        }
        return "redirect:/";
    }
}
