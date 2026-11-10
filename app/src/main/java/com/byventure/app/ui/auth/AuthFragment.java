package com.byventure.app.ui.auth;

import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
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
import com.byventure.app.databinding.FragmentAuthBinding;
import com.byventure.app.ui.auth.logic.AuthViewModel;
import com.byventure.app.ui.auth.logic.SimpleTextWatcher;
import com.google.android.material.color.MaterialColors;
import com.google.android.material.textfield.TextInputEditText;

public class AuthFragment extends Fragment {

    // TODO: пока заглушка с моим именем, потом будет проверка на сервере
    private static final String STUB_USERNAME = "Дима";

    private FragmentAuthBinding binding;
    private AuthViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentAuthBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);

        // Восстановление данных из ViewModel (до подключения слушателей)
        binding.etUsername.setText(viewModel.getLoginUsername());
        binding.etPassword.setText(viewModel.getLoginPassword());

        binding.etUsername.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            protected void onChanged(String text) {
                viewModel.setLoginUsername(text);
                binding.tilUsername.setError(null);
                updateButtonState();
            }
        });
        binding.etPassword.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            protected void onChanged(String text) {
                viewModel.setLoginPassword(text);
                binding.tilUsername.setError(null);
                updateButtonState();
            }
        });

        binding.etPassword.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE && binding.btnLogin.isEnabled()) {
                binding.btnLogin.performClick();
                return true;
            }
            return false;
        });

        binding.btnLogin.setOnClickListener(v -> attemptLogin());

        setupRegisterLink();
        updateButtonState();
    }

    private void attemptLogin() {
        String username = textOf(binding.etUsername);
        // String password = textOf(binding.etPassword);

        // TODO: Заменить на реальную проверку запросом на сервер
        if (!STUB_USERNAME.equals(username)) {
            binding.tilUsername.setError(getString(R.string.auth_error_invalid_credentials));
            return;
        }

        binding.tilUsername.setError(null);
        // TODO: вход выполнен успешно. сохранить сессию/токен,
        //  затем viewModel.clear() и переход на главный экран
        //  NavHostFragment.findNavController(this).navigate(R.id.action_authFragment_to_homeFragment);
    }

    private void updateButtonState() {
        binding.btnLogin.setEnabled(
                !textOf(binding.etUsername).isEmpty() && !textOf(binding.etPassword).isEmpty());
    }

    private void setupRegisterLink() {
        String prefix = getString(R.string.auth_no_account);
        String link = getString(R.string.auth_register_link);
        int linkColor = MaterialColors.getColor(binding.tvRegister, androidx.appcompat.R.attr.colorPrimary);

        SpannableString text = new SpannableString(prefix + " " + link);
        text.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                NavHostFragment.findNavController(AuthFragment.this)
                        .navigate(R.id.action_authFragment_to_registerFragment);
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                ds.setColor(linkColor);
                ds.setUnderlineText(false);
            }
        }, prefix.length(), text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        binding.tvRegister.setText(text);
        binding.tvRegister.setMovementMethod(LinkMovementMethod.getInstance());
        binding.tvRegister.setHighlightColor(Color.TRANSPARENT);
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