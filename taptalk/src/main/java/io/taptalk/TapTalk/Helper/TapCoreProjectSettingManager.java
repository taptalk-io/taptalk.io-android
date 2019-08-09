package io.taptalk.TapTalk.Helper;

import io.taptalk.TapTalk.API.View.TAPDefaultDataView;
import io.taptalk.TapTalk.API.View.TapProjectConfigInterface;
import io.taptalk.TapTalk.Manager.TAPDataManager;
import io.taptalk.TapTalk.Model.ResponseModel.TAPProjectConfigResponse;
import io.taptalk.TapTalk.Model.TAPErrorModel;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ApiErrorCode.OTHER_ERRORS;

public class TapCoreProjectSettingManager {
    private void getProjectConfig(TapProjectConfigInterface listener) {
        TAPDataManager.getInstance().getProjectConfig(new TAPDefaultDataView<TAPProjectConfigResponse>() {
            @Override
            public void onSuccess(TAPProjectConfigResponse response) {
                listener.onSuccess(response.getConfigs());
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
