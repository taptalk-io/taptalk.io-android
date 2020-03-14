package io.taptalk.TapTalk.Helper.CustomMaterialFilePicker.ui;

import android.content.Context;
import android.content.res.ColorStateList;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;
import java.util.List;

import io.taptalk.TapTalk.Helper.CustomMaterialFilePicker.utils.FileTypeUtils;
import io.taptalk.TapTalk.Helper.TAPUtils;
import io.taptalk.Taptalk.R;

/**
 * Created by Dimorinny on 24.10.15.
 */

public class DirectoryAdapter extends RecyclerView.Adapter<DirectoryAdapter.DirectoryViewHolder> {
    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public class DirectoryViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivFileImage;
        private ImageView ivDirectoryChevron;
        private TextView tvFileTitle;
        private TextView tvFileSubtitle;

        public DirectoryViewHolder(View itemView, final OnItemClickListener clickListener) {
            super(itemView);

            itemView.setOnClickListener(v -> clickListener.onItemClick(v, getAdapterPosition()));

            ivFileImage = itemView.findViewById(R.id.iv_item_file_image);
            ivDirectoryChevron = itemView.findViewById(R.id.iv_item_directory_chevron);
            tvFileTitle = itemView.findViewById(R.id.tv_item_file_title);
            tvFileSubtitle = itemView.findViewById(R.id.tv_item_file_subtitle);
        }
    }

    private List<File> mFiles;
    private Context mContext;
    private OnItemClickListener mOnItemClickListener;

    public DirectoryAdapter(Context context, List<File> files) {
        mContext = context;
        mFiles = files;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    @NonNull
    @Override
    public DirectoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new DirectoryViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.tap_item_file, parent, false), mOnItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull DirectoryViewHolder holder, int position) {
        File currentFile = mFiles.get(position);

        FileTypeUtils.FileType fileType = FileTypeUtils.getFileType(currentFile);
        if (FileTypeUtils.FileType.IMAGE != fileType && FileTypeUtils.FileType.VIDEO != fileType) {
            holder.ivFileImage.setImageResource(fileType.getIcon());
            ImageViewCompat.setImageTintList(holder.ivFileImage, ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.tapColorPrimary)));
            holder.ivFileImage.setAlpha(0.7f);
        } else {
            Glide.with(holder.itemView.getContext()).load(currentFile).apply(new RequestOptions().centerCrop()).into(holder.ivFileImage);
            ImageViewCompat.setImageTintList(holder.ivFileImage, null);
            holder.ivFileImage.setAlpha(1.0f);
        }
        if (FileTypeUtils.FileType.DIRECTORY != fileType) {
            String stringBld = TAPUtils.getStringSizeLengthFile(currentFile.length()) +
                    " - " +
                    fileType;
            holder.tvFileSubtitle.setText(stringBld);
            holder.ivDirectoryChevron.setVisibility(View.GONE);
        } else {
            holder.tvFileSubtitle.setText(fileType.getDescription());
            holder.ivDirectoryChevron.setVisibility(View.VISIBLE);
        }
        holder.tvFileTitle.setText(currentFile.getName());
    }

    @Override
    public int getItemCount() {
        return mFiles.size();
    }

    public File getModel(int index) {
        return mFiles.get(index);
    }
}
