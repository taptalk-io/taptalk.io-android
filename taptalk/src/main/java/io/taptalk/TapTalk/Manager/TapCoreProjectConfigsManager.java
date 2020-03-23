package io.taptalk.TapTalk.Manager;

import androidx.annotation.Keep;

import io.taptalk.TapTalk.API.View.TAPDefaultDataView;
import io.taptalk.TapTalk.Listener.TapCoreProjectConfigsListener;
import io.taptalk.TapTalk.Model.TAPErrorModel;
import io.taptalk.TapTalk.Model.TapConfigs;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientErrorCodes.ERROR_CODE_OTHERS;

@Keep
public class TapCoreProjectConfigsManager {

    private static TapCoreProjectConfigsManager instance;

    public static TapCoreProjectConfigsManager getInstance() {
        return null == instance ? instance = new TapCoreProjectConfigsManager() : instance;
    }

    public void getProjectConfigs(TapCoreProjectConfigsListener listener) {
        TAPDataManager.getInstance().getProjectConfig(new TAPDefaultDataView<TapConfigs>() {
            @Override
            public void onSuccess(TapConfigs response) {
                if (null != listener) {
                    listener.onSuccess(response);
                }
            }

            @Override
            public void onError(TAPErrorModel error) {
                if (null != listener) {
                    listener.onError(error.getCode(), error.getMessage());
                }
            }

            @Override
            public void onError(String errorMessage) {
                if (null != listener) {
                    listener.onError(ERROR_CODE_OTHERS, errorMessage);
                }
            }
        });
    }
}
