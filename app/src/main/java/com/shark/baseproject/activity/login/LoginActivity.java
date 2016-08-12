package com.shark.baseproject.activity.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.shark.base.util.StringUtil;
import com.shark.base.webservice.WebServiceErrorType;
import com.shark.baseproject.R;
import com.shark.baseproject.activity.BaseDemoActivity;
import com.shark.baseproject.activity.home.HomeActivity;
import com.shark.baseproject.manager.LoginManager;
import com.shark.baseproject.webservice.task.login.LoginTask;
import com.shark.baseproject.webservice.task.login.entity.LoginInputEntity;
import com.shark.baseproject.webservice.task.login.entity.LoginResultEntity;

public class LoginActivity extends BaseDemoActivity implements View.OnClickListener, TextView.OnEditorActionListener, LoginTask.LoginResponseListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        bindContentView();
        //TODO If LoginManager has account email and password, application manager can help user auto login - Shark.M.Lin
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_skip, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_skip:
                onActionSkipItemSelected();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onActionSkipItemSelected() {
        startHomeActivity();
    }

    private void bindContentView() {
        bindEmailEditText();
        bindPasswordEditText();
        bindLoginButton();
    }

    private void bindEmailEditText() {
        EditText editText = (EditText) findViewById(R.id.activityLogin_emailEditText);
        editText.setText(LoginManager.getInstance().getEmail());
    }

    private void bindPasswordEditText() {
        EditText editText = (EditText) findViewById(R.id.activityLogin_passwordEditText);
        editText.setOnEditorActionListener(this);
    }

    private void bindLoginButton() {
        findViewById(R.id.activityLogin_loginButton).setOnClickListener(this);
    }

    @Override
    public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
        if (actionId == EditorInfo.IME_ACTION_SEND) {
            hideKeyboardView();
            onLoginButtonClick();
        }
        return false;
    }

    @Override
    public void onClick(View view) {
        onLoginButtonClick();
    }

    private void onLoginButtonClick() {
        //Check email and password. - Shark.M.Lin
        EditText emailEditText = (EditText) findViewById(R.id.activityLogin_emailEditText);
        String email = emailEditText.getText().toString();
        if(StringUtil.isEmpty(email)) {
            emailEditText.setError("Please input email account");
            return;
        }

        if(!StringUtil.isEMail(email)) {
            emailEditText.setError("Please input email");
            return;
        }

        EditText passwordEditText = (EditText) findViewById(R.id.activityLogin_passwordEditText);
        String password = passwordEditText.getText().toString();
        if(StringUtil.isEmpty(password)) {
            passwordEditText.setError("Please input password");
            return;
        }
        //Save email and password to LoginManager. - Shark.M.Lin
        LoginManager.getInstance().setEmail(email);
        LoginManager.getInstance().setPassword(password);
        //Request web service - Shark.M.Lin
        showLoadingDialog("Login", "Please Wait", true);
        requestLoginWebService(email, password);
    }

    private void requestLoginWebService(String email, String password) {
        LoginInputEntity loginInputEntity = new LoginInputEntity();
        loginInputEntity.setAccount(email);
        loginInputEntity.setPassword(password);
        LoginTask task = new LoginTask(this, loginInputEntity);
        startWebServiceTask(task);
    }

    @Override
    public void onLoginResponseSuccess(LoginResultEntity result) {
        LoginManager.getInstance().setLoginResult(result);
        hideLoadingDialog();
        startHomeActivity();
    }

    @Override
    public void onLoginResponseError(int resultCode, String description) {
        hideLoadingDialog();
        Toast.makeText(this, description, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLoginNetworkError(WebServiceErrorType errorType) {
        hideLoadingDialog();
        String description = generateNetworkErrorDescription(errorType);
        Toast.makeText(this, description, Toast.LENGTH_SHORT).show();
    }

    private void startHomeActivity() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }
}