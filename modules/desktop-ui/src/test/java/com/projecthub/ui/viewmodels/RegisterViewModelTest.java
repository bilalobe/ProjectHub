package com.projecthub.ui.viewmodels;

import com.projecthub.ui.modules.login.RegisterViewModel;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RegisterViewModelTest {
    @Test
    public void testValidateForm() {
        RegisterViewModel viewModel = new RegisterViewModel();
        assertTrue(viewModel.validateForm("A", "Good job"));
        assertFalse(viewModel.validateForm("", "Good job"));
    }
}
