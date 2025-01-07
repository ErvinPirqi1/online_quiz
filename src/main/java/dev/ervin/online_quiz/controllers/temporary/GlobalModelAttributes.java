package dev.ervin.online_quiz.controllers.temporary;

import dev.ervin.online_quiz.models.temporary.MockUser;
import dev.ervin.online_quiz.services.temporary.MockUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelAttributes {

    @Autowired
    private MockUserService mockUserService;

    @ModelAttribute("mockUser")
    public MockUser addMockUser() {
        return mockUserService.getCurrentUser();
    }
}
