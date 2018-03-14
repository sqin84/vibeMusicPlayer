package com.example.ajcin.flashbackmusicteam16;


import android.support.test.espresso.DataInteraction;
import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class user_set_time_test {

    @Rule
    public ActivityTestRule<Main_Activity> mActivityTestRule = new ActivityTestRule<>(Main_Activity.class);

    @Test
    public void user_set_time_test() {
        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(60000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction oj = onView(
                allOf(withText("Sign in with Google"),
                        childAtPosition(
                                allOf(withId(R.id.main_googlesigninbtn),
                                        childAtPosition(
                                                withClassName(is("android.widget.FrameLayout")),
                                                0)),
                                0),
                        isDisplayed()));
        oj.perform(click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(3598672);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction bottomNavigationItemView = onView(
                allOf(withId(R.id.navigation_nowPlaying),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.navigation),
                                        0),
                                2),
                        isDisplayed()));
        bottomNavigationItemView.perform(click());

        ViewInteraction textView = onView(
                allOf(withId(R.id.artistName), withText("No Song Playing"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.musicItems),
                                        0),
                                3),
                        isDisplayed()));
        textView.check(matches(withText("No Song Playing")));

        ViewInteraction editText = onView(
                allOf(withId(R.id.editText), withText("year,month,day,hour,minute"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.musicItems),
                                        0),
                                5),
                        isDisplayed()));
        editText.check(matches(withText("year,month,day,hour,minute")));

        ViewInteraction button = onView(
                allOf(withId(R.id.setTime),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.musicItems),
                                        0),
                                6),
                        isDisplayed()));
        button.check(matches(isDisplayed()));

        ViewInteraction button2 = onView(
                allOf(withId(R.id.setTime),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.musicItems),
                                        0),
                                6),
                        isDisplayed()));
        button2.check(matches(isDisplayed()));

        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.editText), withText("year,month,day,hour,minute"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.musicItems),
                                        0),
                                10),
                        isDisplayed()));
        appCompatEditText.perform(click());

        ViewInteraction appCompatEditText2 = onView(
                allOf(withId(R.id.editText), withText("year,month,day,hour,minute"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.musicItems),
                                        0),
                                10),
                        isDisplayed()));
        appCompatEditText2.perform(replaceText(",month,day,hour,minute"));

        ViewInteraction appCompatEditText3 = onView(
                allOf(withId(R.id.editText), withText(",month,day,hour,minute"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.musicItems),
                                        0),
                                10),
                        isDisplayed()));
        appCompatEditText3.perform(closeSoftKeyboard());

        ViewInteraction appCompatEditText4 = onView(
                allOf(withId(R.id.editText), withText(",month,day,hour,minute"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.musicItems),
                                        0),
                                10),
                        isDisplayed()));
        appCompatEditText4.perform(click());

        ViewInteraction appCompatEditText5 = onView(
                allOf(withId(R.id.editText), withText(",month,day,hour,minute"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.musicItems),
                                        0),
                                10),
                        isDisplayed()));
        appCompatEditText5.perform(replaceText("2018,3,8,3,30"));

        ViewInteraction appCompatEditText6 = onView(
                allOf(withId(R.id.editText), withText("2018,3,8,3,30"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.musicItems),
                                        0),
                                10),
                        isDisplayed()));
        appCompatEditText6.perform(closeSoftKeyboard());

        ViewInteraction editText2 = onView(
                allOf(withId(R.id.editText), withText("2018,3,8,3,30"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.musicItems),
                                        0),
                                5),
                        isDisplayed()));
        editText2.check(matches(withText("2018,3,8,3,30")));

        ViewInteraction editText3 = onView(
                allOf(withId(R.id.editText), withText("2018,3,8,3,30"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.musicItems),
                                        0),
                                5),
                        isDisplayed()));
        editText3.check(matches(withText("2018,3,8,3,30")));

        pressBack();

        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.setTime), withText("Set Time"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.musicItems),
                                        0),
                                11),
                        isDisplayed()));
        appCompatButton.perform(click());

        ViewInteraction bottomNavigationItemView2 = onView(
                allOf(withId(R.id.navigation_songs),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.navigation),
                                        0),
                                1),
                        isDisplayed()));
        bottomNavigationItemView2.perform(click());

        DataInteraction appCompatTextView = onData(anything())
                .inAdapterView(allOf(withId(android.R.id.list),
                        childAtPosition(
                                withClassName(is("android.widget.FrameLayout")),
                                0)))
                .atPosition(8);
        appCompatTextView.perform(click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(3348102);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        DataInteraction appCompatTextView2 = onData(anything())
                .inAdapterView(allOf(withId(android.R.id.list),
                        childAtPosition(
                                withClassName(is("android.widget.FrameLayout")),
                                0)))
                .atPosition(8);
        appCompatTextView2.perform(click());

        ViewInteraction bottomNavigationItemView3 = onView(
                allOf(withId(R.id.navigation_songs),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.navigation),
                                        0),
                                1),
                        isDisplayed()));
        bottomNavigationItemView3.perform(click());

        DataInteraction appCompatTextView3 = onData(anything())
                .inAdapterView(allOf(withId(android.R.id.list),
                        childAtPosition(
                                withClassName(is("android.widget.FrameLayout")),
                                0)))
                .atPosition(8);
        appCompatTextView3.perform(click());

        ViewInteraction textView2 = onView(
                allOf(withId(R.id.time), withText("Last Played: 3:30\n Day of the week: THURSDAY"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.musicItems),
                                        0),
                                1),
                        isDisplayed()));
        textView2.check(matches(withText("Last Played: 3:30  Day of the week: THURSDAY")));

    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
