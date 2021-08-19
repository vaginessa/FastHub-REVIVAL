package com.fastaccess.ui.modules.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import android.view.View;
import android.widget.ProgressBar;

import com.evernote.android.state.State;
import com.fastaccess.App;
import com.fastaccess.R;
import com.fastaccess.helper.ActivityHelper;
import com.fastaccess.helper.AnimHelper;
import com.fastaccess.helper.AppHelper;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.Bundler;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.ui.base.BaseActivity;
import com.fastaccess.ui.modules.login.chooser.LoginChooserActivity;
import com.fastaccess.ui.widgets.FontCheckbox;
import com.fastaccess.ui.widgets.dialog.MessageDialogView;

import butterknife.BindView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import es.dmoral.toasty.Toasty;
import io.reactivex.functions.Action;
import kotlin.NotImplementedError;

/**
 * Created by Kosh on 08 Feb 2017, 9:10 PM
 */

public class LoginActivity extends BaseActivity<LoginMvp.View, LoginPresenter> implements LoginMvp.View {

    @BindView(R.id.progress) ProgressBar progress;

    @State boolean isBasicAuth;

    public static void startOAuth(@NonNull Activity activity) {
        Intent intent = new Intent(activity, LoginActivity.class);
        intent.putExtras(Bundler.start()
                .put(BundleConstant.YES_NO_EXTRA, true)
                .put(BundleConstant.EXTRA_TWO, true)
                .end());
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
        activity.finish();
    }

    public static void start(@NonNull Activity activity, boolean isBasicAuth) {
        start(activity, isBasicAuth, false);
    }

    public static void start(@NonNull Activity activity, boolean isBasicAuth, boolean isEnterprise) {
        Intent intent = new Intent(activity, LoginActivity.class);
        intent.putExtras(Bundler.start()
                .put(BundleConstant.YES_NO_EXTRA, isBasicAuth)
                .put(BundleConstant.IS_ENTERPRISE, isEnterprise)
                .end());
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
        activity.finish();
    }

    @OnClick(R.id.browserLogin) void onOpenBrowser() {
        if (isEnterprise()) {
            MessageDialogView.newInstance(getString(R.string.warning), getString(R.string.github_enterprise_reply),
                    true, Bundler.start().put("hide_buttons", true).end())
                    .show(getSupportFragmentManager(), MessageDialogView.TAG);
            return;
        }
        ActivityHelper.startCustomTab(this, getPresenter().getAuthorizationUrl());
    }

    @Override protected int layout() {
        return R.layout.login_form_layout;
    }

    @Override protected boolean isTransparent() {
        return true;
    }

    @Override protected boolean canBack() {
        return false;
    }

    @Override protected boolean isSecured() {
        return true;
    }

    @NonNull @Override public LoginPresenter providePresenter() {
        return new LoginPresenter();
    }

    @Override
    public void onRequire2Fa() {
    }

    @Override public void onEmptyUserName(boolean isEmpty) {
    }

    @Override
    public void onEmptyPassword(boolean isEmpty) {

    }

    @Override
    public void onEmptyEndpoint(boolean isEmpty) {

    }

    @Override public void onSuccessfullyLoggedIn(boolean extraLogin) {
        hideProgress();
        onRestartApp();
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.LoginTheme);
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            if (getIntent() != null && getIntent().getExtras() != null) {
                isBasicAuth = getIntent().getExtras().getBoolean(BundleConstant.YES_NO_EXTRA);
                if (getIntent().getExtras().getBoolean(BundleConstant.EXTRA_TWO)) {
                    onOpenBrowser();
                }
            }
        }
    }

    @Override protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        getPresenter().onHandleAuthIntent(intent);
        setIntent(null);
    }

    @Override protected void onResume() {
        super.onResume();
        getPresenter().onHandleAuthIntent(getIntent());
        setIntent(null);
    }

    @Override public void showErrorMessage(@NonNull String msgRes) {
        hideProgress();
        super.showErrorMessage(msgRes);
    }

    @Override public void showMessage(@StringRes int titleRes, @StringRes int msgRes) {
        hideProgress();
        super.showMessage(titleRes, msgRes);
    }

    @Override public void showMessage(@NonNull String titleRes, @NonNull String msgRes) {
        hideProgress();
        super.showMessage(titleRes, msgRes);
    }

    @Override public void showProgress(@StringRes int resId) {
        AnimHelper.animateVisibility(progress, true);
    }

    @Override public void onBackPressed() {
        startActivity(new Intent(this, LoginChooserActivity.class));
        finish();
    }

    @Override public void hideProgress() {
        progress.setVisibility(View.GONE);
    }
}
