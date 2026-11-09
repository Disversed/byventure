package com.byventure.app.ui.auth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.byventure.app.R;
import com.byventure.app.databinding.FragmentPasswordBinding;
import com.byventure.app.ui.auth.logic.AuthViewModel;
import com.byventure.app.ui.auth.logic.SimpleTextWatcher;

public class PasswordFragment extends Fragment {

    private FragmentPasswordBinding binding;
    private AuthViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentPasswordBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);

        binding.btnBack.setOnClickListener(v ->
                NavHostFragment.findNavController(this).navigateUp());

        // Восстановление пароля из ViewModel
        binding.etPassword.setText(viewModel.getPassword());

        binding.etPassword.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            protected void onChanged(String text) {
                viewModel.setPassword(text);
                validatePassword(text);
            }
        });

        binding.etPassword.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_GO && binding.btnNext.isEnabled()) {
                binding.btnNext.performClick();
                return true;
            }
            return false;
        });

        binding.btnNext.setOnClickListener(v -> onNextClicked());

        validatePassword(viewModel.getPassword());
    }

    /** Проверка на каждый введённый символ. */
    private void validatePassword(String password) {
        boolean valid = AuthViewModel.isPasswordValid(password);
        binding.btnNext.setEnabled(valid);

        if (password.isEmpty() || valid) {
            // Ошибки нет, снова helper text со списком требований
            binding.tilPassword.setError(null);
        } else {
            binding.tilPassword.setError(buildErrorText(password));
        }
    }

    /** Показываю только те требования, которые ещё не выполнены */
    private String buildErrorText(String password) {
        StringBuilder sb = new StringBuilder(getString(R.string.password_error_header));
        if (!AuthViewModel.hasMinLength(password)) {
            sb.append("\n• ").append(getString(R.string.password_req_length));
        }
        if (!AuthViewModel.hasLatinLetters(password)) {
            sb.append("\n• ").append(getString(R.string.password_req_latin));
        }
        if (!AuthViewModel.hasDigits(password)) {
            sb.append("\n• ").append(getString(R.string.password_req_digits));
        }
        if (!AuthViewModel.hasSpecialSymbols(password)) {
            sb.append("\n• ").append(getString(R.string.password_req_special));
        }
        return sb.toString();
    }

    private void onNextClicked() {
        // Все данные регистрации лежат в ViewModel:
        // viewModel.getContact(), viewModel.getUsername(), viewModel.getPassword()

        // TODO: пароль подходит. Здесь логика: отправить данные в базу/на сервер,
        //  затем viewModel.clear() и переход на главный экран, например:
        //  NavHostFragment.findNavController(this).navigate(R.id.action_passwordFragment_to_homeFragment);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}