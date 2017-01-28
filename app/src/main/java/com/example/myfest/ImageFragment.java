package com.example.myfest;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;

import org.parceler.Parcels;

import java.io.IOException;
import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ImageFragment extends Fragment implements LoaderManager.LoaderCallbacks<Bitmap> {
	public static final String FEST = "fest";

	@BindView(R.id.image)
	ImageView image;
	private Fest fest;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_image, container, false);
		ButterKnife.bind(ImageFragment.this, view);
		fest = Parcels.unwrap(getArguments().getParcelable(FEST));
		Glide.with(getContext()).using(new FirebaseImageLoader()).load(FirebaseStorage.getInstance().getReferenceFromUrl(fest.getImageUrl())).into(image);
		getActivity().getSupportLoaderManager().initLoader(0, null, this).forceLoad();
		return view;
	}

	@Override
	public Loader<Bitmap> onCreateLoader(int id, Bundle args) {
		return new AsyncTaskLoader<Bitmap>(getContext()) {
			@Override
			public Bitmap loadInBackground() {
				try {
					return BitmapFactory.decodeStream(new URL(fest.getImageUrl()).openConnection().getInputStream());
				} catch (IOException e) {
					e.printStackTrace();
					return null;
				}
			}
		};
	}

	@Override
	public void onLoadFinished(Loader<Bitmap> loader, Bitmap data) {
		if (data != null) {
			image.setImageBitmap(data);
		}
		Glide.with(getContext()).using(new FirebaseImageLoader()).load(FirebaseStorage.getInstance().getReferenceFromUrl(fest.getImageUrl())).into(image);
	}

	@Override
	public void onLoaderReset(Loader<Bitmap> loader) {
	}
}
