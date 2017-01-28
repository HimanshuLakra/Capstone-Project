package com.example.myfest;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseIndexRecyclerAdapter;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FestEventsFragment extends Fragment {
	public static final String FEST = "fest";

	@BindView(R.id.fest_events)
	RecyclerView festEvents;
	private Fest fest;
	private FirebaseIndexRecyclerAdapter<Event, EventViewHolder> festEventsRecyclerAdapter;

	public FestEventsFragment() {
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_fest_events, container, false);
		ButterKnife.bind(FestEventsFragment.this, view);
		fest = Parcels.unwrap(getArguments().getParcelable(FEST));
		FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
		DatabaseReference festDatabaseReference = mFirebaseDatabase.getReference().child("fests").child(fest.getName()).child("events");
		DatabaseReference eventsDatabaseReference = mFirebaseDatabase.getReference().child("events");
		festEvents.setLayoutManager(new GridLayoutManager(getContext(), getResources().getInteger(R.integer.span)));
		festEventsRecyclerAdapter = new FirebaseIndexRecyclerAdapter<Event, EventViewHolder>(Event.class, R.layout.fragment_image, EventViewHolder.class, festDatabaseReference, eventsDatabaseReference) {
			@Override
			protected void populateViewHolder(EventViewHolder viewHolder, Event model, int position) {
				viewHolder.setPoster(model);
			}
		};
		festEvents.setAdapter(festEventsRecyclerAdapter);
		return view;
	}

	public static class EventViewHolder extends RecyclerView.ViewHolder {
		private final ImageView image;

		public EventViewHolder(View categoryPosterView) {
			super(categoryPosterView);
			this.image =
					(ImageView) categoryPosterView.findViewById(R.id.image);
			image.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {

				}
			});
		}

		public void setPoster(Event event) {
			Glide.with(image.getContext())
					.using(new FirebaseImageLoader())
					.load(FirebaseStorage.getInstance().getReferenceFromUrl(event.getImageUrl()))
					.into(image);
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		festEventsRecyclerAdapter.cleanup();
	}
}
