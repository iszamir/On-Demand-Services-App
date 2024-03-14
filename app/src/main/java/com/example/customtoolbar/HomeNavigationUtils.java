package com.example.customtoolbar;

import android.content.Context;
import android.content.Intent;

public class HomeNavigationUtils {

    public static Intent openHomeFragment(Context context) {
        Intent intent = new Intent (context, MainActivity.class);
        intent.putExtra("open_home_fragment", true);
        context.startActivity(intent);
        return intent;
    }
}
