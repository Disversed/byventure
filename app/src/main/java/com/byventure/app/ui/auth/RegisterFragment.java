package com.byventure.app.ui.auth;

import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.text.HtmlCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.byventure.app.R;
import com.byventure.app.databinding.FragmentRegisterBinding;
import com.byventure.app.ui.auth.logic.AuthViewModel;
import com.byventure.app.ui.auth.logic.SimpleTextWatcher;
import com.google.android.material.textfield.TextInputEditText;

public class RegisterFragment extends Fragment {

    private FragmentRegisterBinding binding;
    private AuthViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentRegisterBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);

        binding.btnBack.setOnClickListener(v ->
                NavHostFragment.findNavController(this).navigateUp());


        // TODO: Ссылки в тексте (заменить URL в strings.xml на нужные)
        binding.tvTerms.setText(HtmlCompat.fromHtml(
                getString(R.string.register_terms), HtmlCompat.FROM_HTML_MODE_LEGACY));
        binding.tvTerms.setMovementMethod(LinkMovementMethod.getInstance());

        // Восстановление данных из ViewModel (до подключения слушателей)
        binding.etContact.setText(viewModel.getContact());
        binding.etUsername.setText(viewModel.getUsername());

        binding.etContact.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            protected void onChanged(String text) {
                viewModel.setContact(text.trim());
                binding.tilContact.setError(null);
                updateButtonState();
            }
        });
        binding.etUsername.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            protected void onChanged(String text) {
                viewModel.setUsername(text.trim());
                binding.tilUsername.setError(null);
                updateButtonState();
            }
        });

        binding.etUsername.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE && binding.btnNext.isEnabled()) {
                binding.btnNext.performClick();
                return true;
            }
            return false;
        });

        binding.btnNext.setOnClickListener(v -> onNextClicked());
        updateButtonState();
    }

    private void onNextClicked() {
        String contact = textOf(binding.etContact);
        String username = textOf(binding.etUsername);
        boolean valid = true;

        // Телефон / email
        if (!isEmailOrPhone(contact)) {
            binding.tilContact.setError(getString(R.string.register_error_contact_format));
            valid = false;
        } else if (isContactTaken(contact)) {
            binding.tilContact.setError(getString(R.string.register_error_contact_taken));
            valid = false;
        }

        // Имя пользователя
        if (username.length() < 3) {
            binding.tilUsername.setError(getString(R.string.register_error_username_short));
            valid = false;
        } else if (isUsernameTaken(username)) {
            binding.tilUsername.setError(getString(R.string.register_error_username_taken));
            valid = false;
        }

        if (!valid) return;

        // Данные уже во ViewModel, переход к вводу пароля
        NavHostFragment.findNavController(this)
                .navigate(R.id.action_registerFragment_to_passwordFragment);
    }

    // TODO: это заглушка. Заменить на проверку на сервере
    private boolean isContactTaken(String contact) {
        return "test@test.com".equalsIgnoreCase(contact);
    }

    // TODO: это заглушка. Заменить на проверку на сервере
    private boolean isUsernameTaken(String username) {
        return "Дима".equalsIgnoreCase(username);
    }

    private boolean isEmailOrPhone(String value) {
        if (Patterns.EMAIL_ADDRESS.matcher(value).matches()) return true;
        boolean phoneChars = value.matches("^\\+?[0-9\\s\\-()]+$");
        int digits = value.replaceAll("[^0-9]", "").length();
        return phoneChars && digits >= 10 && digits <= 15;
    }

    private void updateButtonState() {
        binding.btnNext.setEnabled(
                !textOf(binding.etContact).isEmpty() && !textOf(binding.etUsername).isEmpty());
    }

    private String textOf(TextInputEditText editText) {
        return editText.getText() == null ? "" : editText.getText().toString().trim();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}