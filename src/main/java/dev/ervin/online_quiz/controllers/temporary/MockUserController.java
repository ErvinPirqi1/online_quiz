package dev.ervin.online_quiz.controllers.temporary;

import dev.ervin.online_quiz.models.temporary.MockUser;
import dev.ervin.online_quiz.services.temporary.MockUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MockUserController {



    @Autowired
    private MockUserService mockUserService;

    @PostMapping("/mock-user/switch")
    public String switchUser(@RequestParam String role, Model model) {
        MockUser updatedUser = null;

        switch (role) {
            case "1":
                updatedUser = mockUserService.getTeacher();
                break;
            case "2":
                updatedUser = mockUserService.getStudent();
                break;
            case "0":
                updatedUser = mockUserService.getAdmin();
                break;
            case "3":
                updatedUser = mockUserService.getGuest();
                break;
            default:
                throw new IllegalArgumentException("Invalid role: " + role);
        }

        mockUserService.setCurrentUser(updatedUser);

        model.addAttribute("mockUser", updatedUser);
        return "redirect:/"; // Redirect to home page or the current page
    }
}

