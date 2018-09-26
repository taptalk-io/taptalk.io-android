package com.moselo.HomingPigeon.View.Adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

    private enum CellProductItemType {
        EXPERT, USER
    }

    public ProductListAdapter(MessageModel messageModel, UserModel myUserModel) {
        setItems(Utils.getInstance().fromJSON(
                new TypeReference<List<ProductModel>>() {
                },
                messageModel.getMessage()), false);
        this.messageModel = messageModel;
        this.myUserModel = myUserModel;
    }

    @Override
    public BaseViewHolder<ProductModel> onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ProductVH(viewGroup, R.layout.cell_chat_product_item, CellProductItemType.values()[i]);
    }

    @Override
    public int getItemViewType(int position) {
        if (messageModel.getUser().getUserID().equals(myUserModel.getUserID()))
            return CellProductItemType.EXPERT.ordinal();
        else return CellProductItemType.USER.ordinal();
    }

    public class ProductVH extends BaseViewHolder<ProductModel> {

        View vBtnSeparator;
        TextView tvBtnDetails;
        TextView tvBtnOrder;

        protected ProductVH(ViewGroup parent, int itemLayoutId, CellProductItemType viewType) {
            super(parent, itemLayoutId);
            vBtnSeparator = itemView.findViewById(R.id.v_btn_separator);
            tvBtnDetails = itemView.findViewById(R.id.tv_btn_details);
            tvBtnOrder = itemView.findViewById(R.id.tv_btn_order);

            if (CellProductItemType.USER == viewType) {
                vBtnSeparator.setVisibility(View.VISIBLE);
                tvBtnOrder.setVisibility(View.VISIBLE);
            } else {
                vBtnSeparator.setVisibility(View.GONE);
                tvBtnOrder.setVisibility(View.GONE);
            }
        }

        @Override
        protected void onBind(ProductModel item, int position) {

        }
    }
}
