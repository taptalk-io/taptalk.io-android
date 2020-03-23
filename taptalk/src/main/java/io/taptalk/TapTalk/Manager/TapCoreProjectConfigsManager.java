package io.taptalk.TapTalk.Manager;

import androidx.annotation.Keep;

import java.util.HashMap;

import io.taptalk.TapTalk.API.View.TAPDefaultDataView;
import io.taptalk.TapTalk.Listener.TapCoreProjectConfigsListener;
import io.taptalk.TapTalk.Model.TAPErrorModel;
import io.taptalk.TapTalk.Model.TapConfigs;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientErrorCodes.ERROR_CODE_OTHERS;

@Keep
public class TapCoreProjectConfigsManager {

    private static HashMap<String, TapCoreProjectConfigsManager> instances;

    private String instanceKey = "";

    public TapCoreProjectConfigsManager(String instanceKey) {
        this.instanceKey = instanceKey;
    }

    public static TapCoreProjectConfigsManager getInstance() {
        return getInstance("");
    }

    public static TapCoreProjectConfigsManager getInstance(String instanceKey) {
        if (!getInstances().containsKey(instanceKey)) {
            TapCoreProjectConfigsManager instance = new TapCoreProjectConfigsManager(instanceKey);
            getInstances().put(instanceKey, instance);
        }
        return getInstances().get(instanceKey);
    }

    private static HashMap<String, TapCoreProjectConfigsManager> getInstances() {
        return null == instances ? instances = new HashMap<>() : instances;
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
