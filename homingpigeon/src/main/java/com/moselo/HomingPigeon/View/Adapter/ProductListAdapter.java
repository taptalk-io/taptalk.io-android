package com.moselo.HomingPigeon.View.Adapter;

import android.view.ViewGroup;

import com.fasterxml.jackson.core.type.TypeReference;
import com.moselo.HomingPigeon.Helper.BaseViewHolder;
import com.moselo.HomingPigeon.Helper.Utils;
import com.moselo.HomingPigeon.Model.MessageModel;
import com.moselo.HomingPigeon.Model.ProductModel;
import com.moselo.HomingPigeon.Model.UserModel;
import com.moselo.HomingPigeon.R;

import java.util.ArrayList;
import java.util.List;

public class ProductListAdapter extends BaseAdapter<ProductModel, BaseViewHolder<ProductModel>> {

    private List<ProductModel> items = new ArrayList<>();
    private MessageModel messageModel;
    private UserModel myUserModel;
    private int youID = -1;
    private String otherName;

    public ProductListAdapter(MessageModel messageModel, UserModel myUserModel, int youID, String otherName) {
        setItems(Utils.getInstance().fromJSON(
                new TypeReference<List<ProductModel>>() {},
                messageModel.getMessage()),false);
        this.messageModel = messageModel;
        this.myUserModel = myUserModel;
        this.youID = youID;
        this.otherName = otherName;
    }

    @Override
    public BaseViewHolder<ProductModel> onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ProductVH(viewGroup, messageModel.getUser().getUserID().equals(myUserModel.getUserID())
        ? R.layout.cell_chat_product_item_expert : R.layout.cell_chat_product_item_user);
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    public class ProductVH extends BaseViewHolder<ProductModel> {

        protected ProductVH(ViewGroup parent, int itemLayoutId) {
            super(parent, itemLayoutId);
        }

        @Override
        protected void onBind(ProductModel item, int position) {

        }
    }
}
