package com.example.lab1.annotation.mvc;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.ServletWebRequest;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CurrentUsernameArgumentResolverTest {

    private final CurrentUsernameArgumentResolver resolver =
            new CurrentUsernameArgumentResolver();

    @Test
    @DisplayName("AC6: supports parameter with CurrentUsername annotation")
    void supportsParameter_returnsTrueForAnnotatedParameter()
            throws Exception {

        // Arrange
        MethodParameter parameter =
                getParameter("annotatedParameter");

        // Act
        boolean result = resolver.supportsParameter(parameter);

        // Assert
        assertTrue(result);
    }

    @Test
    @DisplayName("AC6: rejects parameter without CurrentUsername annotation")
    void supportsParameter_returnsFalseForRegularParameter()
            throws Exception {

        // Arrange
        MethodParameter parameter =
                getParameter("regularParameter");

        // Act
        boolean result = resolver.supportsParameter(parameter);

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("AC7: resolves username from request principal")
    void resolveArgument_returnsCurrentUsername()
            throws Exception {

        // Arrange
        MethodParameter parameter =
                getParameter("annotatedParameter");

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        request.setUserPrincipal(() -> "user");

        ServletWebRequest webRequest =
                new ServletWebRequest(request);

        // Act
        Object result = resolver.resolveArgument(
                parameter,
                null,
                webRequest,
                null
        );

        // Assert
        assertInstanceOf(String.class, result);
        assertEquals("user", result);
    }

    private MethodParameter getParameter(String methodName)
            throws Exception {

        Method method = TestController.class.getDeclaredMethod(
                methodName,
                String.class
        );

        return new MethodParameter(method, 0);
    }

    private static class TestController {

        void annotatedParameter(
                @CurrentUsername String username
        ) {
        }

        void regularParameter(String username) {
        }
    }
}