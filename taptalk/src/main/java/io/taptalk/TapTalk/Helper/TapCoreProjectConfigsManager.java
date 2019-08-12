package io.taptalk.TapTalk.Helper;

import io.taptalk.TapTalk.API.View.TAPDefaultDataView;
import io.taptalk.TapTalk.API.View.TapProjectConfigsInterface;
import io.taptalk.TapTalk.Manager.TAPDataManager;
import io.taptalk.TapTalk.Model.TAPErrorModel;
import io.taptalk.TapTalk.Model.TapConfigs;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ApiErrorCode.OTHER_ERRORS;

public class TapCoreProjectConfigsManager {
    public static void getProjectConfigs(TapProjectConfigsInterface listener) {
        TAPDataManager.getInstance().getProjectConfig(new TAPDefaultDataView<TapConfigs>() {
            @Override
            public void onSuccess(TapConfigs response) {
                listener.onSuccess(response);
            }

            @Override
            public void onError(TAPErrorModel error) {
                listener.onError(error.getCode(), error.getMessage());
            }

            @Override
            public void onError(String errorMessage) {
                listener.onError(String.valueOf(OTHER_ERRORS), errorMessage);
            }
        });
    }
}
