package com.example.myfest;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import org.parceler.Parcels;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
		implements NavigationView.OnNavigationItemSelectedListener {

	private ArrayList<Fest> fests;

	@BindView(R.id.pager_fest)
	ViewPager pagerFest;

	@BindView(R.id.content_main)
	ViewPager contentMain;

	@BindView(R.id.tab_fests)
	TabLayout tabFests;

	private PagerFestAdapter pagerFestAdapter;
	private PagerFestEventsAdapter pagerFestEventsAdapter;

	private FirebaseAuth firebaseAuth;
	private FirebaseAuth.AuthStateListener authStateListener;

	ImageView userpic;
	TextView username;
	TextView useremail;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ButterKnife.bind(MainActivity.this);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
				this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
		drawer.setDrawerListener(toggle);
		toggle.syncState();

		final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
		navigationView.setNavigationItemSelectedListener(this);
		userpic = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.imageView);
		username = (TextView) navigationView.getHeaderView(0).findViewById(R.id.username);
		useremail = (TextView) navigationView.getHeaderView(0).findViewById(R.id.useremail);

		fests = new ArrayList<>();
		pagerFestAdapter = new PagerFestAdapter(getSupportFragmentManager(), fests);
		pagerFest.setAdapter(pagerFestAdapter);
		pagerFestEventsAdapter = new PagerFestEventsAdapter(getSupportFragmentManager(), fests);
		contentMain.setAdapter(pagerFestEventsAdapter);
		tabFests.setupWithViewPager(contentMain);

		FirebaseDatabase.getInstance().getReference().child("fests").addChildEventListener(new ChildEventListener() {
					@Override
					public void onChildAdded(DataSnapshot dataSnapshot, String s) {
						Fest fest = dataSnapshot.getValue(Fest.class);
						fests.add(fest);
						pagerFestAdapter.notifyDataSetChanged();
						pagerFestEventsAdapter.notifyDataSetChanged();
					}

					@Override
					public void onChildChanged(DataSnapshot dataSnapshot, String s) {
					}

					@Override
					public void onChildRemoved(DataSnapshot dataSnapshot) {
					}

					@Override
					public void onChildMoved(DataSnapshot dataSnapshot, String s) {
					}

					@Override
					public void onCancelled(DatabaseError databaseError) {
					}
				});

		firebaseAuth = FirebaseAuth.getInstance();
		authStateListener = new FirebaseAuth.AuthStateListener() {
			@Override
			public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
				FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
				if (firebaseUser != null) {
					new Fetch().execute(firebaseUser);
					username.setText(firebaseUser.getDisplayName());
					useremail.setText(firebaseUser.getEmail());
				} else {
					startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().setIsSmartLockEnabled(true).setProviders(AuthUI.GOOGLE_PROVIDER).build(), 1);
				}
			}
		};
	}

	private class Fetch extends AsyncTask<FirebaseUser, Void, Void> {
		@Override
		protected Void doInBackground(FirebaseUser... firebaseUsers) {
			if (firebaseUsers[0].getPhotoUrl() != null) {
				String imageUrl = firebaseUsers[0].getPhotoUrl().toString();
				FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl).getBytes(1024 * 1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
					@Override
					public void onSuccess(byte[] bytes) {
						userpic.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
					}
				});
			}
			return null;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 1) {
			if (resultCode == RESULT_CANCELED) {
				finish();
			}
		}
	}

	@Override
	public void onBackPressed() {
		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		if (drawer.isDrawerOpen(GravityCompat.START)) {
			drawer.closeDrawer(GravityCompat.START);
		} else {
			super.onBackPressed();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@SuppressWarnings("StatementWithEmptyBody")
	@Override
	public boolean onNavigationItemSelected(MenuItem item) {
		// Handle navigation view item clicks here.
		int id = item.getItemId();

		if (id == R.id.nav_camera) {
			// Handle the camera action
		} else if (id == R.id.nav_gallery) {

		} else if (id == R.id.nav_slideshow) {

		} else if (id == R.id.nav_manage) {

		} else if (id == R.id.nav_share) {

		} else if (id == R.id.nav_send) {

		}

		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		drawer.closeDrawer(GravityCompat.START);
		return true;
	}

	private class PagerFestAdapter extends FragmentPagerAdapter {
		ArrayList<Fest> fests;

		public PagerFestAdapter(FragmentManager fm, ArrayList<Fest> fests) {
			super(fm);
			this.fests = fests;
		}

		@Override
		public Fragment getItem(int position) {
			Fest fest = fests.get(position);
			ImageFragment imageFragment = new ImageFragment();
			Bundle bundle = new Bundle();
			bundle.putParcelable(ImageFragment.FEST, Parcels.wrap(fest));
			imageFragment.setArguments(bundle);
			return imageFragment;
		}

		@Override
		public int getCount() {
			return fests.size();
		}
	}

	private class PagerFestEventsAdapter extends FragmentPagerAdapter {
		ArrayList<Fest> fests;

		public PagerFestEventsAdapter(FragmentManager fm, ArrayList<Fest> fests) {
			super(fm);
			this.fests = fests;
		}

		@Override
		public Fragment getItem(int position) {
			Fest fest = fests.get(position);
			FestEventsFragment festEventsFragment = new FestEventsFragment();
			Bundle bundle = new Bundle();
			bundle.putParcelable(FestEventsFragment.FEST, Parcels.wrap(fest));
			festEventsFragment.setArguments(bundle);
			return festEventsFragment;
		}

		@Override
		public int getCount() {
			return fests.size();
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Fest fest = fests.get(position);
			return fest.getName();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		firebaseAuth.addAuthStateListener(authStateListener);
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (authStateListener != null) {
			firebaseAuth.removeAuthStateListener(authStateListener);
		}
	}
}
