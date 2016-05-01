package com.dreamworks.musicwanted.ui;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.widget.ViewDragHelper;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.dreamworks.musicwanted.R;
import com.dreamworks.musicwanted.view.HomePlayView;
import com.dreamworks.musicwanted.view.HomePlayView.HomePlayScrollListener;
import com.dreamworks.musicwanted.view.HomePlayView.HomePlayScrollStateChangedListener;

public class PlayActivity extends Activity {

	private com.dreamworks.musicwanted.view.HomePlayView homeplayview;
	private LinearLayout layout_left;
	private LinearLayout layout_home;
	private LinearLayout layout_right;
	private TextView lrc_textview;
	private TextView current_time;
	private SeekBar seekbar;
	private TextView total_time;

	private ArrayList<ImageView> imageviews = new ArrayList<ImageView>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_play);

		initView();
		initData();

	}

	private void initView() {
		// TODO Auto-generated method stub
		homeplayview = (com.dreamworks.musicwanted.view.HomePlayView) findViewById(R.id.activity_play_homeplayview);
		layout_left = (LinearLayout) homeplayview.getLeftLayout();
		layout_home = (LinearLayout) homeplayview.getHomeLayout();
		layout_right = (LinearLayout) homeplayview.getRightLayout();
		imageviews.add((ImageView) findViewById(R.id.activity_play_leftlayout_image));
		imageviews.add((ImageView) findViewById(R.id.activity_play_homelayout_image));
		imageviews.add((ImageView) findViewById(R.id.activity_play_rightlayout_image));
		lrc_textview = (TextView) findViewById(R.id.activity_play_lrc_textview);
		current_time = (TextView) findViewById(R.id.activity_play_current_time);
		seekbar = (SeekBar) findViewById(R.id.activity_play_seekbar);
		total_time = (TextView) findViewById(R.id.activity_play_total_time);

		homeplayview.setOnHomePlayScrollListener(new HomePlayScrollListener() {

			@Override
			public void onLayoutScroll(View homeLayout, View scrollView, float offest) {
				layout_home.setAlpha(1.0f - offest);
			}
		});

		homeplayview.setOnHomePlayScrollStateChangedListener(new HomePlayScrollStateChangedListener() {

			@Override
			public void onLayoutScrollStateChanged(View layout, int state) {
				if (state == ViewDragHelper.STATE_IDLE) {
					setLayoutImage();
				}
			}
		});

		setLayoutImage();
		homeplayview.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				System.out.println("setOnClickListener");
			}
		});

	}

	private void initData() {
		// TODO Auto-generated method stub

	}

	private void setLayoutImage() {
		for (int i = 0; i < imageviews.size(); i++) {
			imageviews.get(i).setBackground(getResources().getDrawable(R.drawable.shape_oval_888888_10));
		}
		int homePlayViewState = homeplayview.getHomePlayViewState();
		switch (homePlayViewState) {
		case HomePlayView.STATE_LEFT:
			imageviews.get(0).setBackground(getResources().getDrawable(R.drawable.shape_oval_ffffff_10));
			break;
		case HomePlayView.STATE_HOME:
			imageviews.get(1).setBackground(getResources().getDrawable(R.drawable.shape_oval_ffffff_10));
			break;
		case HomePlayView.STATE_RIGHT:
			imageviews.get(2).setBackground(getResources().getDrawable(R.drawable.shape_oval_ffffff_10));
			break;
		default:
			break;
		}
	}

}
