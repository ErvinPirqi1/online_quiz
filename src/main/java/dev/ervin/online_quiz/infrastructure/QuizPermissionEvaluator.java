package dev.ervin.online_quiz.infrastructure;

import dev.ervin.online_quiz.models.Quiz;
import dev.ervin.online_quiz.repositories.QuizRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Optional;

@Component
public class QuizPermissionEvaluator implements PermissionEvaluator {

    @Autowired
    private QuizRepository quizRepository;

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        if (authentication == null || !(targetDomainObject instanceof Long) || !(permission instanceof String)) {
            return false;
        }

        Long quizId = (Long) targetDomainObject;
        String perm = (String) permission;

        if (quizId == null) { // Handle null quizId
            return false;
        }

        Optional<Quiz> quiz = quizRepository.findById(quizId);
        if (!quiz.isPresent()) {
            return false; // Quiz not found
        }

        if (perm.equals("UPDATE") || perm.equals("TOGGLE_VISIBILITY")) {  //Define Permissions
            return quiz.get().getCreatedBy().getUsername().equals(authentication.getName());
        }

        return false; // Unknown permission
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        throw new UnsupportedOperationException("Not implemented");
    }
}