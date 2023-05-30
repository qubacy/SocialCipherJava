package com.mcdead.busycoder.socialcipher.client.activity.attachmentpicker.fragment.picker.images.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mcdead.busycoder.socialcipher.R;
import com.mcdead.busycoder.socialcipher.client.activity.error.data.Error;
import com.mcdead.busycoder.socialcipher.client.processor.filesystem.image.loader.ImageLoader;
import com.mcdead.busycoder.socialcipher.client.processor.filesystem.image.loader.ImageLoaderCallback;

public class AttachmentPickerImageViewHolder extends RecyclerView.ViewHolder
    implements ImageLoaderCallback
{
    private static final int C_THUMBNAIL_SIZE = 256;

    private boolean m_isChosen = false;

    private Context m_context = null;

    private ImageView m_image = null;
    private ImageView m_choosingIndicationIcon = null;

    private AttachmentPickerImageViewHolderCallback m_callback = null;

    public AttachmentPickerImageViewHolder(
            @NonNull View itemView,
            @NonNull AttachmentPickerImageViewHolderCallback callback,
            @NonNull Context context)
    {
        super(itemView);

        m_image = itemView.findViewById(R.id.attachment_image_view_holder_image);
        m_choosingIndicationIcon = itemView.findViewById(R.id.attachment_image_view_holder_choosing_indication_icon);

        m_callback = callback;

        m_context = context;
    }

    public boolean setData(
            final Uri imageUri,
            final boolean isChosen)
    {
        if (imageUri == null) return false;

        m_image.setImageResource(R.drawable.ic_hourglass_24);
        m_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onImageClicked();
            }
        });

        changeItemChosenState(isChosen);
        loadImageThumbnail(imageUri);

        return true;
    }

    private void onImageClicked() {
        changeItemChosenState(!m_isChosen);

        m_callback.onImageClicked((int) getItemId());
    }

    private void loadImageThumbnail(final Uri imageUri) {
        new ImageLoader(imageUri, m_context, this).execute();
        //new LoadImage().execute(imageUri);
    }

    private void setImageBitmap(final Bitmap image) {
        m_image.setImageBitmap(image);
    }

    private void changeItemChosenState(final boolean isChosen) {
        if (!isChosen)
            m_choosingIndicationIcon.setVisibility(View.INVISIBLE);
        else
            m_choosingIndicationIcon.setVisibility(View.VISIBLE);

        m_isChosen = isChosen;
    }

    @Override
    public void onImagesLoaded(final Uri imageUri) {
        m_image.setImageURI(imageUri);
    }

    @Override
    public void onImagesLoadingError(final Error error) {
        m_callback.onViewHolderErrorOccurred(error);
    }

//    private class LoadImage extends AsyncTask<Uri, Void, Bitmap> {
//        @Override
//        protected Bitmap doInBackground(final Uri... imageUriList) {
//            if (imageUriList.length <= 0) return null;
//
//            Uri imageUri = imageUriList[0];
//            Bitmap thumbnail = null;
//
//            try (InputStream imageStream = m_contentResolver.openInputStream(imageUri)) {
//                thumbnail = ThumbnailUtils.extractThumbnail(
//                        BitmapFactory.decodeStream(imageStream),
//                        C_THUMBNAIL_SIZE, C_THUMBNAIL_SIZE);
//
//            } catch (IOException e) {
//                e.printStackTrace();
//
//                return null;
//            }
//
//            return thumbnail;
//        }
//
//        @Override
//        protected void onPostExecute(final Bitmap bitmap) {
//            if (bitmap == null) return;
//
//            setImageBitmap(bitmap);
//        }
//    }


}
