package com.joe.orangee.activity.my;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.joe.orangee.R;
import com.joe.orangee.adapter.MyMentionFragmentPagerAdapter;
import com.joe.orangee.library.slidingtab.SlidingTabLayout;
import com.joe.orangee.util.Utils;

public class MyMentionActivity extends ActionBarActivity {

	private ViewPager vpMyComment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_comment_activity);
		View contentView=findViewById(R.id.content_layout);
		Utils.setTopPadding(this, contentView);
		Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);  
		setSupportActionBar(toolbar);
        Utils.setActionBarStyle(getSupportActionBar(), R.string.my_mention);
		vpMyComment = (ViewPager) findViewById(R.id.my_comment_vp);
		vpMyComment.setAdapter(new MyMentionFragmentPagerAdapter(getSupportFragmentManager(),
            getResources().getStringArray(R.array.my_mention)));
        SlidingTabLayout mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setViewPager(vpMyComment);
        mSlidingTabLayout.setSelectedIndicatorColors(Color.parseColor("#128d4a"), Color.parseColor("#ffff0000"));
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
}
