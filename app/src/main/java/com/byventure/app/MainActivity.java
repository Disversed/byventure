package com.byventure.app;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.byventure.app.databinding.ActivityMainBinding;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private NavController navController;


    // Экраны, на которых скрыты нижнее меню и верхняя панель
    private final Set<Integer> hideBarsOn = new HashSet<>(Arrays.asList(
            R.id.authFragment,
            R.id.registerFragment,
            R.id.passwordFragment
    ));

    // Экраны, где скрыто только нижнее меню
    private final Set<Integer> hideBottomBarOn = new HashSet<>(Arrays.asList(
            R.id.newTripBudgetFragment,
            R.id.newTripTicketsAndBooksFragment
    ));


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // обработка клавиатуры (то что снизу поднимется)
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            Insets ime = insets.getInsets(WindowInsetsCompat.Type.ime());

            // большее из двух: навигационная панель или клавиатура
            v.setPadding(bars.left, bars.top, bars.right, Math.max(bars.bottom, ime.bottom));
            return WindowInsetsCompat.CONSUMED;
        });


        // нижнее меню
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.navHostFragment);
        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
        }
        BottomNavigationView bottomNavigationView = binding.navBottomMenu;
        NavigationUI.setupWithNavController(bottomNavigationView, navController);

        // скрытие обоих меню (верх и низ)
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            int visibility = hideBarsOn.contains(destination.getId()) ? View.GONE : View.VISIBLE;
            bottomNavigationView.setVisibility(visibility);
            binding.topBar.getRoot().setVisibility(visibility);
        });


        // Верхнее меню
        MaterialToolbar toolbar = binding.topBar.getRoot();
        // стрелка «назад» работает как системная кнопка
        toolbar.setNavigationOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            int id = destination.getId();
            boolean hideAll = hideBarsOn.contains(id);
            boolean hideBottom = hideAll || hideBottomBarOn.contains(id);

            bottomNavigationView.setVisibility(hideBottom ? View.GONE : View.VISIBLE);
            toolbar.setVisibility(hideAll ? View.GONE : View.VISIBLE);

            // «верхнеуровневый» экран = есть в нижнем меню
            boolean topLevel = bottomNavigationView.getMenu().findItem(id) != null;
            toolbar.setNavigationIcon(topLevel ? null : R.drawable.ic_arrow_back);

            MenuItem settings = toolbar.getMenu().findItem(R.id.action_right_icon);
            if (settings != null) settings.setVisible(topLevel);

            // заголовок из android:label в nav_graph
            binding.topBar.tvToolbarTitle.setText(destination.getLabel());
        });
    }
}