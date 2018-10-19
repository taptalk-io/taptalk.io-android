package com.moselo.HomingPigeon.View.Activity;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.CardView;
import android.view.View;

import com.moselo.HomingPigeon.R;
import com.moselo.HomingPigeon.ViewModel.HpScanResultViewModel;

public class HpScanResultActivity extends HpBaseActivity {

    private HpScanResultViewModel mViewModel;

    private CardView cvResult;
    private ConstraintLayout clContainer;

    public static HpScanResultActivity newInstance() {
        return new HpScanResultActivity();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hp_scan_result_activity);
        mViewModel = ViewModelProviders.of(this).get(HpScanResultViewModel.class);
        initView();
    }

    public void initView() {
        cvResult = findViewById(R.id.cv_result);
        clContainer = findViewById(R.id.cl_container);
        clContainer.setBackgroundResource(R.drawable.hp_bg_purplish_pink_medium_purple);
        cvResult.animate()
                .alpha(1f).withEndAction(() -> cvResult.animate().scaleY(1.5f).start()).start();
    }
}
