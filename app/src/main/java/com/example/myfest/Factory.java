package com.example.myfest;

import android.content.Context;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Factory implements RemoteViewsService.RemoteViewsFactory {
	Context context;
	ArrayList<String> events;
	public Factory(Context context) {
		this.context = context;
		events = new ArrayList<>();
		FirebaseDatabase.getInstance().getReference().child("events").addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot eventsSnapshot) {
				for (DataSnapshot event: eventsSnapshot.getChildren()) {
					events.add(event.getValue(Event.class).getName());
				}
			}

			@Override
			public void onCancelled(DatabaseError databaseError) {
			}
		});
	}

	@Override
	public void onCreate() {

	}

	@Override
	public void onDataSetChanged() {

	}

	@Override
	public void onDestroy() {

	}

	@Override
	public int getCount() {
		return events.size();
	}

	@Override
	public RemoteViews getViewAt(int position) {
		RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.text);
		remoteViews.setTextViewText(R.id.text, events.get(position));
		return remoteViews;
	}

	@Override
	public RemoteViews getLoadingView() {
		return null;
	}

	@Override
	public int getViewTypeCount() {
		return 1;
	}

	@Override
	public long getItemId(int i) {
		return 0;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}
}
