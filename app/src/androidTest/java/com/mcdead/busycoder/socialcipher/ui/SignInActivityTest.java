package com.mcdead.busycoder.socialcipher.ui;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.mcdead.busycoder.socialcipher.R;
import com.mcdead.busycoder.socialcipher.client.activity.signin.SignInActivity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class SignInActivityTest {
    private ActivityScenarioRule<SignInActivity> m_activityRule =
            new ActivityScenarioRule<>(SignInActivity.class);

    @Before
    public void setUp() {

    }

    @Test
    public void signInWithLoginPasswordDataMatrix() {
        m_activityRule.getScenario().onActivity(new ActivityScenario.ActivityAction<SignInActivity>() {
            @Override
            public void perform(SignInActivity activity) {
                // performing some actions during the activity's execution..

                //Espresso.onView(ViewMatchers.withId(R.id.signin_web_view)).
            }
        });
    }


}
