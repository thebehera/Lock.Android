/*
 * PanelHolder.java
 *
 * Copyright (c) 2016 Auth0 (http://auth0.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.auth0.android.lock.views;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.auth0.android.lock.Configuration;
import com.auth0.android.lock.R;
import com.auth0.android.lock.events.DatabaseLoginEvent;
import com.auth0.android.lock.events.DatabaseSignUpEvent;
import com.auth0.android.lock.events.FetchApplicationEvent;
import com.auth0.android.lock.events.HeaderSizeChangeEvent;
import com.auth0.android.lock.events.SocialConnectionEvent;
import com.auth0.android.lock.views.interfaces.LockWidget;
import com.auth0.android.lock.views.interfaces.LockWidgetEnterprise;
import com.auth0.android.lock.views.interfaces.LockWidgetForm;
import com.squareup.otto.Bus;

public class ClassicPanelHolder extends RelativeLayout implements View.OnClickListener, ModeSelectionView.ModeSelectedListener, LockWidget, LockWidgetForm, LockWidgetEnterprise {

    private static final String TAG = ClassicPanelHolder.class.getSimpleName();
    private final Bus bus;
    private Configuration configuration;
    private FormLayout formLayout;
    private ModeSelectionView modeSelectionView;
    private FormView subForm;
    private ActionButton actionButton;
    private LayoutParams termsParams;
    private LayoutParams ssoParams;
    private View ssoLayout;
    private ProgressBar loadingProgressBar;
    @ModeSelectionView.Mode
    private int currentDatabaseMode;
    private boolean keyboardIsOpen;
    private boolean ssoMessageShown;

    public ClassicPanelHolder(Context context) {
        super(context);
        bus = null;
        configuration = null;
    }

    public ClassicPanelHolder(Context context, Bus lockBus) {
        super(context);
        this.bus = lockBus;
        this.configuration = null;
        showWaitForConfigurationLayout();
    }

    private void init() {
        if (configuration == null) {
            Log.w(TAG, "Configuration is missing, the panel won't init.");
            showConfigurationMissingLayout();
        } else {
            showPanelLayout();
        }
    }

    private void showWaitForConfigurationLayout() {
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(CENTER_IN_PARENT, TRUE);
        loadingProgressBar = new ProgressBar(getContext());
        loadingProgressBar.setIndeterminate(true);
        addView(loadingProgressBar, params);
    }

    private void showPanelLayout() {
        int verticalMargin = (int) getResources().getDimension(R.dimen.com_auth0_lock_widget_vertical_margin_field);
        int horizontalMargin = (int) getResources().getDimension(R.dimen.com_auth0_lock_widget_horizontal_margin);

        ssoParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
        ssoParams.addRule(ALIGN_PARENT_TOP, TRUE);

        boolean showModeSelection = configuration.getDefaultDatabaseConnection() != null && configuration.isSignUpEnabled();
        if (showModeSelection) {
            Log.v(TAG, "SignUp enabled. Adding the Login/SignUp Mode Switcher");
            RelativeLayout.LayoutParams switcherParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            switcherParams.addRule(ALIGN_PARENT_TOP, TRUE);
            switcherParams.setMargins(horizontalMargin, 0, horizontalMargin, 0);
            modeSelectionView = new ModeSelectionView(getContext(), this);
            modeSelectionView.setId(R.id.com_auth0_lock_form_selector);
            addView(modeSelectionView, switcherParams);
            ssoParams.addRule(BELOW, R.id.com_auth0_lock_form_selector);
        }

        ssoLayout = inflate(getContext(), R.layout.com_auth0_lock_sso_layout, null);
        ssoLayout.setId(R.id.com_auth0_lock_sso_layout);
        addView(ssoLayout, ssoParams);

        formLayout = new FormLayout(this);
        formLayout.setId(R.id.com_auth0_lock_form_layout);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.alignWithParent = true;
        params.setMargins(horizontalMargin, verticalMargin, horizontalMargin, verticalMargin);
        params.addRule(BELOW, R.id.com_auth0_lock_form_selector);
        params.addRule(ABOVE, R.id.com_auth0_lock_terms_layout);
        params.addRule(CENTER_IN_PARENT, TRUE);
        addView(formLayout, params);

        boolean showDatabase = configuration.getDefaultDatabaseConnection() != null;
        boolean showEnterprise = !configuration.getEnterpriseStrategies().isEmpty();
        if (showDatabase || showEnterprise) {
            RelativeLayout.LayoutParams actionParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            actionParams.addRule(ALIGN_PARENT_BOTTOM, TRUE);
            actionButton = new ActionButton(getContext());
            actionButton.setId(R.id.com_auth0_lock_action_button);
            actionButton.setOnClickListener(this);
            addView(actionButton, actionParams);
        }

        termsParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        termsParams.addRule(ABOVE, R.id.com_auth0_lock_action_button);
        termsParams.alignWithParent = true;
        View termsLayout = inflate(getContext(), R.layout.com_auth0_lock_terms_layout, null);
        termsLayout.setId(R.id.com_auth0_lock_terms_layout);
        addView(termsLayout, termsParams);

        onModeSelected(ModeSelectionView.Mode.LOG_IN);
    }

    /**
     * Setup the panel to show the correct forms by reading the Auth0 Configuration.
     *
     * @param configuration the configuration to use on this panel, or null if it is missing.
     */
    public void configurePanel(@Nullable Configuration configuration) {
        removeView(loadingProgressBar);
        loadingProgressBar = null;
        this.configuration = configuration;
        if (configuration != null) {
            init();
            return;
        }
        showConfigurationMissingLayout();
    }

    private void showConfigurationMissingLayout() {
        int horizontalMargin = (int) getResources().getDimension(R.dimen.com_auth0_lock_widget_horizontal_margin);
        final LinearLayout errorLayout = new LinearLayout(getContext());
        errorLayout.setOrientation(LinearLayout.VERTICAL);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(horizontalMargin, 0, horizontalMargin, 0);
        params.addRule(CENTER_IN_PARENT, TRUE);

        TextView errorText = new TextView(getContext());
        errorText.setText(R.string.com_auth0_lock_configuration_retrieving_error);
        errorText.setGravity(CENTER_IN_PARENT);

        Button retryButton = new Button(getContext());
        retryButton.setText(R.string.com_auth0_lock_action_retry);
        retryButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                bus.post(new FetchApplicationEvent());
                removeView(errorLayout);
                showWaitForConfigurationLayout();
            }
        });
        LinearLayout.LayoutParams childParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        childParams.gravity = Gravity.CENTER;
        errorLayout.addView(errorText, childParams);
        errorLayout.addView(retryButton, childParams);
        addView(errorLayout, params);
    }

    @Override
    public void showChangePasswordForm(boolean show) {
        if (show) {
            addSubForm(new ChangePasswordFormView(this));
        } else {
            removeSubForm();
        }
    }

    private void addSubForm(@NonNull FormView form) {
        formLayout.setVisibility(GONE);
        if (modeSelectionView != null) {
            modeSelectionView.setVisibility(GONE);
        }
        showSignUpTerms(false);

        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.addRule(BELOW, R.id.com_auth0_lock_form_selector);
        params.addRule(ABOVE, R.id.com_auth0_lock_terms_layout);
        params.addRule(CENTER_IN_PARENT, TRUE);
        addView(form, params);
        this.subForm = form;
    }

    private void removeSubForm() {
        formLayout.setVisibility(VISIBLE);
        if (modeSelectionView != null) {
            modeSelectionView.setVisibility(VISIBLE);
        }

        showSignUpTerms(!keyboardIsOpen && currentDatabaseMode == ModeSelectionView.Mode.SIGN_UP);
        if (this.subForm != null) {
            removeView(this.subForm);
            this.subForm = null;
        }
    }

    /**
     * Triggers the back action on the form.
     *
     * @return true if it was handled, false otherwise
     */
    public boolean onBackPressed() {
        if (subForm != null) {
            removeSubForm();
            bus.post(new HeaderSizeChangeEvent(false));
            return true;
        }

        boolean handled = formLayout != null && formLayout.onBackPressed();
        if (handled && modeSelectionView != null) {
            modeSelectionView.setVisibility(ssoLayout.getVisibility() == VISIBLE ? GONE : VISIBLE);
        }
        return handled;
    }

    /**
     * Displays a progress bar on top of the action button. This will also
     * enable or disable the action button.
     *
     * @param show whether to show or hide the action bar.
     */
    public void showProgress(boolean show) {
        if (actionButton != null) {
            actionButton.showProgress(show);
        }
        if (modeSelectionView != null) {
            modeSelectionView.setEnabled(!show);
        }
    }

    @Override
    public void onModeSelected(@ModeSelectionView.Mode int mode) {
        Log.d(TAG, "Mode changed to " + mode);
        currentDatabaseMode = mode;
        formLayout.changeFormMode(mode);
        showSignUpTerms(mode == ModeSelectionView.Mode.SIGN_UP);
    }

    private void showSignUpTerms(boolean show) {
        int height = (int) getResources().getDimension(R.dimen.com_auth0_lock_terms_height);
        termsParams.height = show ? height : 0;
    }

    @Override
    public void showSSOEnabledMessage(boolean show) {
        ssoMessageShown = show;
        int height = (int) getResources().getDimension(R.dimen.com_auth0_lock_sso_height);
        ssoParams.height = show ? height : 0;
        ssoLayout.setLayoutParams(ssoParams);
        if (formLayout != null && !keyboardIsOpen) {
            formLayout.showOnlyEnterprise(show);
        }
        if (modeSelectionView != null && !keyboardIsOpen) {
            modeSelectionView.setVisibility(show ? GONE : VISIBLE);
        }
    }

    @Override
    public Configuration getConfiguration() {
        return configuration;
    }

    @Override
    public void onFormSubmit() {
        actionButton.callOnClick();
    }

    @Override
    public void onClick(View v) {
        Object event = subForm != null ? subForm.submitForm() : formLayout.onActionPressed();
        if (event != null) {
            bus.post(event);
        }
    }

    @Override
    public void showCustomFieldsForm(DatabaseSignUpEvent event) {
        addSubForm(new CustomFieldsFormView(this, event.getEmail(), event.getUsername(), event.getPassword()));
        bus.post(new HeaderSizeChangeEvent(true));
    }

    public void showMFACodeForm(DatabaseLoginEvent event) {
        addSubForm(new MFACodeFormView(this, event.getUsernameOrEmail(), event.getPassword()));
    }

    @Override
    public void onSocialLogin(SocialConnectionEvent event) {
        Log.d(TAG, "Social login triggered for connection " + event.getConnectionName());
        bus.post(event);
    }

    /**
     * Notifies this forms and its child views that the keyboard state changed, so that
     * it can change the layout in order to fit all the fields.
     *
     * @param isOpen whether the keyboard is open or close.
     */
    public void onKeyboardStateChanged(boolean isOpen) {
        keyboardIsOpen = isOpen;

        if (subForm != null) {
            subForm.onKeyboardStateChanged(isOpen);
        } else if (modeSelectionView != null && !ssoMessageShown) {
            modeSelectionView.setVisibility(isOpen ? GONE : VISIBLE);
        }
        if (actionButton != null) {
            actionButton.setVisibility(isOpen ? GONE : VISIBLE);
        }
        formLayout.onKeyboardStateChanged(isOpen);

        showSignUpTerms(!isOpen && currentDatabaseMode == ModeSelectionView.Mode.SIGN_UP && subForm == null);
    }
}
