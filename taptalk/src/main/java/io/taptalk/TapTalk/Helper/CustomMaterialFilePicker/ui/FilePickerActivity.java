package io.taptalk.TapTalk.Helper.CustomMaterialFilePicker.ui;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileFilter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Pattern;

import io.taptalk.TapTalk.Helper.CustomMaterialFilePicker.filter.CompositeFilter;
import io.taptalk.TapTalk.Helper.CustomMaterialFilePicker.filter.HiddenFilter;
import io.taptalk.TapTalk.Helper.CustomMaterialFilePicker.filter.PatternFilter;
import io.taptalk.TapTalk.Helper.CustomMaterialFilePicker.utils.FileUtils;
import io.taptalk.Taptalk.R;

/**
 * Created by Dimorinny on 24.10.15.
 */
public class FilePickerActivity extends AppCompatActivity implements DirectoryFragment.FileClickListener {
    public static final String ARG_START_PATH = "tap_arg_start_path";
    public static final String ARG_CURRENT_PATH = "tap_arg_current_path";

    public static final String ARG_FILTER = "tap_arg_filter";
    public static final String ARG_HIDDEN = "tap_arg_hidden";
    public static final String ARG_CLOSEABLE = "tap_arg_closeable";
    public static final String ARG_TITLE = "tap_arg_title";

    public static final String STATE_START_PATH = "tap_state_start_path";
    private static final String STATE_CURRENT_PATH = "tap_state_current_path";

    public static final String RESULT_FILE_PATH = "tap_result_file_path";
    private static final int HANDLE_CLICK_DELAY = 150;

    private ConstraintLayout clActionBar;
    private ImageView ivButtonClose;
    private TextView tvTitle;
    private String mStartPath = Environment.getExternalStorageDirectory().getAbsolutePath();
    private String mCurrentPath = mStartPath;
    private CharSequence mTitle;

    private boolean mCloseable;

    private CompositeFilter mFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tap_activity_file_picker);

        initArguments(savedInstanceState);
        initViews();
        initBackStackState();
        initFragment();
    }

    @SuppressWarnings("unchecked")
    private void initArguments(Bundle savedInstanceState) {
        ArrayList<FileFilter> filters = new ArrayList<>();
        if (getIntent().hasExtra(ARG_HIDDEN)) {
            filters.add(new HiddenFilter());
        }

        if (getIntent().hasExtra(ARG_FILTER)) {
            Serializable filter = getIntent().getSerializableExtra(ARG_FILTER);
            if (filter instanceof Pattern) {
                filters.add(new PatternFilter((Pattern) filter, false));
                mFilter = new CompositeFilter(filters);
            } else {
                mFilter = (CompositeFilter) filter;
            }
        }

        if (null == mFilter)
            mFilter = new CompositeFilter(filters);

        if (savedInstanceState != null) {
            mStartPath = savedInstanceState.getString(STATE_START_PATH);
            mCurrentPath = savedInstanceState.getString(STATE_CURRENT_PATH);
            updateTitle();
        } else {
            if (getIntent().hasExtra(ARG_START_PATH)) {
                mStartPath = getIntent().getStringExtra(ARG_START_PATH);
                mCurrentPath = mStartPath;
            }

            if (getIntent().hasExtra(ARG_CURRENT_PATH)) {
                String currentPath = getIntent().getStringExtra(ARG_CURRENT_PATH);

                if (currentPath.startsWith(mStartPath)) {
                    mCurrentPath = currentPath;
                }
            }
        }

        if (getIntent().hasExtra(ARG_TITLE)) {
            mTitle = getIntent().getCharSequenceExtra(ARG_TITLE);
        }

        if (getIntent().hasExtra(ARG_CLOSEABLE)) {
            mCloseable = getIntent().getBooleanExtra(ARG_CLOSEABLE, true);
        }
    }

    private void initViews() {
        getWindow().setBackgroundDrawable(null);

        clActionBar = findViewById(R.id.cl_action_bar);
        ivButtonClose = findViewById(R.id.iv_button_close);
        tvTitle = findViewById(R.id.tv_title);

        if (!TextUtils.isEmpty(mTitle)) {
            setTitle(mTitle);
        }

        updateTitle();

        ivButtonClose.setOnClickListener(v -> onBackPressed());
    }

    private void initFragment() {
        getFragmentManager().beginTransaction()
                .replace(R.id.container, DirectoryFragment.getInstance(
                        mCurrentPath, mFilter))
                .addToBackStack(null)
                .commit();
    }

    private void initBackStackState() {
        String pathToAdd = mCurrentPath;
        ArrayList<String> separatedPaths = new ArrayList<>();

        while (!pathToAdd.equals(mStartPath)) {
            pathToAdd = FileUtils.cutLastSegmentOfPath(pathToAdd);
            separatedPaths.add(pathToAdd);
        }

        Collections.reverse(separatedPaths);

        for (String path : separatedPaths) {
            addFragmentToBackStack(path);
        }
    }

    private void updateTitle() {
        String titlePath = mCurrentPath.isEmpty() ? "Documents" : mCurrentPath.replace("/storage/emulated/0", "Documents");
        tvTitle.setText(titlePath);
        if (mCurrentPath.equals(mStartPath)) {
            ivButtonClose.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.tap_ic_close_grey));
        } else {
            ivButtonClose.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.tap_ic_chevron_left_white));
        }
    }

    private void addFragmentToBackStack(String path) {
        getFragmentManager().beginTransaction()
//                .setCustomAnimations(R.animator.tap_slide_left_fragment, R.animator.tap_fade_out_fragment, R.animator.tap_fade_in_fragment, R.animator.tap_slide_right_fragment)
                .replace(R.id.container, DirectoryFragment.getInstance(path, mFilter))
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = getFragmentManager();

        if (!mCurrentPath.equals(mStartPath)) {
            fm.popBackStack();
            mCurrentPath = FileUtils.cutLastSegmentOfPath(mCurrentPath);
            updateTitle();
        } else {
            setResult(RESULT_CANCELED);
            finishWithAnimation();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(STATE_CURRENT_PATH, mCurrentPath);
        outState.putString(STATE_START_PATH, mStartPath);
    }

    @Override
    public void onFileClicked(final File clickedFile) {
        new Handler().postDelayed(() -> handleFileClicked(clickedFile), HANDLE_CLICK_DELAY);
    }

    private void handleFileClicked(final File clickedFile) {
        if (clickedFile.isDirectory()) {
            mCurrentPath = clickedFile.getPath();
            // If the user wanna go to the emulated directory, he will be taken to the
            // corresponding user emulated folder.
            if (mCurrentPath.equals("/storage/emulated")) {
                mCurrentPath = Environment.getExternalStorageDirectory().getAbsolutePath();
            }
            addFragmentToBackStack(mCurrentPath);
            updateTitle();
        } else {
            setResultAndFinish(clickedFile.getPath());
        }
    }

    private void setResultAndFinish(String filePath) {
        Intent data = new Intent();
        data.putExtra(RESULT_FILE_PATH, filePath);
        setResult(RESULT_OK, data);
        finishWithAnimation();
    }

    private void finishWithAnimation() {
        finish();
        overridePendingTransition(R.anim.tap_stay, R.anim.tap_slide_down);
    }
}
