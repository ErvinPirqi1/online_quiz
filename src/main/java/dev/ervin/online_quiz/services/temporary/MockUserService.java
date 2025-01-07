package dev.ervin.online_quiz.services.temporary;

import dev.ervin.online_quiz.models.temporary.MockUser;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class MockUserService {
    private MockUser currentUser;

    public MockUserService() {
        this.currentUser = new MockUser(3L, "Guest", 3, Arrays.asList("VIEW_TESTS"));
    }

    public MockUser getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(MockUser user) {
        this.currentUser = user;
    }

    public MockUser getAdmin() {
        return new MockUser(0L, "Admin", 0, Arrays.asList("CREATE_TEST", "VIEW_TESTS", "MANAGE_USERS"));
    }

    public MockUser getTeacher() {
        return new MockUser(1L, "Teacher", 1, Arrays.asList("CREATE_TEST", "VIEW_TESTS"));
    }

    public MockUser getStudent() {
        return new MockUser(2L, "Student", 2, Arrays.asList("VIEW_TESTS"));
    }

    public MockUser getGuest() {
        return new MockUser(3L, "Guest", 3, Arrays.asList("VIEW_TESTS"));
    }
}
