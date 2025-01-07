package dev.ervin.online_quiz.models.temporary;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MockUser {
    private Long id;
    private String username;
    private int role;
    private List<String> permissions;

    public MockUser(Long id, String username, int role) {
        this(id, username, role, Arrays.asList("VIEW_TESTS"));
    }

    public boolean hasPermission(String permission) {
        return permissions.contains(permission);
    }

    public boolean hasRole(int role) {
        return this.role == role;
    }
}
