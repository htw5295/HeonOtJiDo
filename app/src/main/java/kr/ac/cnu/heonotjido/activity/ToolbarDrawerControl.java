package kr.ac.cnu.heonotjido.activity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kr.ac.cnu.heonotjido.R;

public class ToolbarDrawerControl {
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.drawer)
    View drawerView;
    @BindView(R.id.drawer_nameContent_textView)
    TextView userIdContentTextView;
    @BindView(R.id.drawer_roleContent_textView)
    TextView roleContentTextView;

    private Activity activity;
    private int statusBarHeight = 0;

    private SharedPreferences user;
    private SharedPreferences.Editor editor;

    public ToolbarDrawerControl(Activity activity) {
        this.activity = activity;
        ButterKnife.bind(this, activity);

        user = activity.getSharedPreferences("user", Context.MODE_PRIVATE);
        editor = user.edit();

        int resId = activity.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resId > 0) {
            statusBarHeight = activity.getResources().getDimensionPixelSize(resId);
            DrawerLayout.LayoutParams param = (DrawerLayout.LayoutParams) drawerView.getLayoutParams();
            param.topMargin = statusBarHeight;
            drawerView.setLayoutParams(param);
        }

        userIdContentTextView.setText(user.getString("userId", "-"));
        roleContentTextView.setText(user.getString("roleId", "-"));
    }

    @OnClick(R.id.toolbar_menu_imageButton)
    void onToolbarMenuButtonClick() {
        drawerLayout.openDrawer(drawerView);
    }

    @OnClick(R.id.toolbar_menu_imageButton)
    void onDrawerCloseButtonClick() {
        drawerLayout.closeDrawers();
    }

    @OnClick(R.id.drawer_logout_button)
    void onDrawerLogoutButtonClick() {
        FirebaseAuth.getInstance().signOut();

        editor.clear();
        editor.commit();
    }
}
