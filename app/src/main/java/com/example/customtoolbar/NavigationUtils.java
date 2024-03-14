package com.example.customtoolbar;

import android.content.Context;
import android.content.Intent;

public class NavigationUtils {

    public static Intent openProfileFragment(Context context) {
        Intent intent = new Intent (context, MainActivity.class);
        intent.putExtra("open_profile_fragment", true);
        context.startActivity(intent);
        return intent;
    }
}
