package dev.ervin.online_quiz.controllers.temporary;

import dev.ervin.online_quiz.dtos.UserDto;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelAttributes {

    @ModelAttribute("user")
    public UserDto getUserSession(HttpSession session) {
        return (UserDto) session.getAttribute("user");
    }

}
