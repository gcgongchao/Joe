package com.joe.orangee.fragment.drawer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.HeaderViewListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.joe.orangee.R;
import com.joe.orangee.activity.my.MyCommentActivity;
import com.joe.orangee.activity.my.MyMentionActivity;
import com.joe.orangee.activity.nearby.NearbyMapWeiboActivity;
import com.joe.orangee.activity.person.PersonPageActivity;
import com.joe.orangee.activity.pictures.PicturesCollectionActivity;
import com.joe.orangee.activity.search.SearchActivity;
import com.joe.orangee.activity.settings.SettingsActivity;
import com.joe.orangee.activity.weibo.WeiboCollectionActivity;
import com.joe.orangee.library.ViewExpandAnimation;
import com.joe.orangee.listener.OrangeeImageLoadingListener;
import com.joe.orangee.model.User;
import com.joe.orangee.model.WeiboStatus;
import com.joe.orangee.net.Downloader.PersonDownloader;
import com.joe.orangee.util.PreferencesKeeper;
import com.joe.orangee.util.Utils;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("ValidFragment")
public class NavigationDrawerFragment extends Fragment {

	private User person;
	private ActionBarDrawerToggle mDrawerToggle;
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerListView;
	private ImageView ivAvatar;
	private TextView tvName;
	private TextView tvFollow;
	private TextView tvFollower;
	private TextView tvStatus;
	private Activity activity;

    public NavigationDrawerFragment(){}
	public NavigationDrawerFragment(Activity activity) {
		this.activity=activity;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		// Indicate that this fragment would like to influence the set of
		// actions in the action bar.
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mDrawerListView = (ListView) inflater.inflate(
				R.layout.fragment_navigation_drawer, container, false);
//		Utils.setTopPadding(getActivity(), mDrawerListView);
        View headerView=View.inflate(getActivity(), R.layout.drawer_left_header, null);
        ivAvatar = (ImageView) headerView.findViewById(R.id.user_avatar);
        tvName = (TextView) headerView.findViewById(R.id.user_name);
        tvFollow = (TextView) headerView.findViewById(R.id.user_follow);
        tvFollower = (TextView) headerView.findViewById(R.id.user_follower);
        tvStatus = (TextView) headerView.findViewById(R.id.user_status);

        fillLeftDrawer();
        mDrawerListView.addHeaderView(headerView);
//		mDrawerListView.setAdapter(new ArrayAdapter<String>(getActivity(),
//				android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.left_drawer_list)));

        mDrawerListView.setAdapter(new CategoryAdapter());
        mDrawerListView
                .setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        selectItem(position);
                    }
                });


		return mDrawerListView;
	}

	private void fillLeftDrawer() {
		if (PreferencesKeeper.readUserInfo(getActivity())!=null) {
			fillViews(PreferencesKeeper.readUserInfo(getActivity()));
			return;
		}
		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				person = new PersonDownloader(getActivity()).getPersonInfo(null, PreferencesKeeper.readAccessToken(getActivity()).getUid());
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				if (person!=null) {
					fillViews(person);
					PreferencesKeeper.writeUserInfo(getActivity(), person);
				}
				super.onPostExecute(result);
			}

		}.execute();
		
	}
	private void fillViews(User person) {
		OrangeeImageLoadingListener.LoadingListener mListener= new OrangeeImageLoadingListener.LoadingListener();
		ImageLoader.getInstance().displayImage(person.getAvatar(), ivAvatar, Utils.getRoundedPicDisplayImageOptions(), mListener);
		tvName.setText(person.getName());
		Resources res=getActivity().getResources();
		tvFollow.setText(res.getString(R.string.person_follow)+""+person.getFriends_count());
		tvFollower.setText(res.getString(R.string.person_follower)+""+person.getFollowers_count()+"");
		tvStatus.setText(res.getString(R.string.person_status)+""+person.getStatuses_count()+"");
	}

	/**
	 * Users of this fragment must call this method to set up the navigation
	 * drawer interactions.
	 * 
	 *            The android:id of this fragment in its activity's layout.
	 * @param drawerLayout
	 *            The DrawerLayout containing this fragment's UI.
	 */
	public void setUp(DrawerLayout drawerLayout, Toolbar toolbar) {
//		mDrawerView = getActivity().findViewById(fragmentId);
		mDrawerLayout = drawerLayout;

		// set a custom shadow that overlays the main content when the drawer
		// opens
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);

		mDrawerToggle = new ActionBarDrawerToggle(activity,
		mDrawerLayout,
		toolbar,
		R.string.navigation_drawer_open,
		R.string.navigation_drawer_close
		) {
			@Override
			public void onDrawerClosed(View drawerView) {
				super.onDrawerClosed(drawerView);
				if (!isAdded()) {
					return;
				}

				getActivity().invalidateOptionsMenu(); // calls
														// onPrepareOptionsMenu()
			}

			@Override
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
				if (!isAdded()) {
					return;
				}

				getActivity().invalidateOptionsMenu(); // calls
														// onPrepareOptionsMenu()
			}
		};

		// Defer code dependent on restoration of previous instance state.
		mDrawerLayout.post(new Runnable() {
			@Override
			public void run() {
				mDrawerToggle.syncState();
			}
		});
		mDrawerToggle.setDrawerIndicatorEnabled(true);
		mDrawerLayout.setDrawerListener(mDrawerToggle);
	}

	private void selectItem(final int position) {
        switch (position) {
            case 0:
                if (PreferencesKeeper.readUserInfo(getActivity())!=null) {
                    Intent intent=new Intent(getActivity(), PersonPageActivity.class);
                    intent.putExtra("User", PreferencesKeeper.readUserInfo(getActivity()));
                    intent.setExtrasClassLoader(WeiboStatus.class.getClassLoader());
                    getActivity().startActivity(intent);
                }

                break;
            case 1:
                HeaderViewListAdapter ha = (HeaderViewListAdapter) mDrawerListView.getAdapter();
                View expandView=((CategoryAdapter)ha.getWrappedAdapter()).getExpandView(0);
                ImageView arrowView=((CategoryAdapter)ha.getWrappedAdapter()).getArrowView(0);
                ViewPropertyAnimator propertyAnimator = null;
                if (expandView.getVisibility()==View.VISIBLE) {
                    propertyAnimator = arrowView.animate().rotation(0.0f).setDuration(300);
                }else if (expandView.getVisibility()==View.GONE) {
                    propertyAnimator = arrowView.animate().rotation(90.0f).setDuration(300);
                }
                propertyAnimator.start();
                expandView.startAnimation(new ViewExpandAnimation(expandView));
                break;
            case 2:
                closeDrawerDelayed();

                startActivity(new Intent(getActivity(), NearbyMapWeiboActivity.class));
                break;
            case 3:
                closeDrawerDelayed();
                startActivity(new Intent(getActivity(), MyCommentActivity.class));
                break;
            case 4:
                closeDrawerDelayed();
                startActivity(new Intent(getActivity(), MyMentionActivity.class));
                break;
            case 5:
                closeDrawerDelayed();
                startActivity(new Intent(getActivity(), SearchActivity.class));
                break;
            case 6:
                closeDrawerDelayed();
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                break;
            default:
                break;
        }
	}

/*	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mCallbacks = (NavigationDrawerCallbacks) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(
					"Activity must implement NavigationDrawerCallbacks.");
		}
	}*/

	/*@Override
	public void onDetach() {
		super.onDetach();
//		mCallbacks = null;
	}*/

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Forward the new configuration the drawer toggle component.
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

    private class CategoryAdapter extends BaseAdapter implements View.OnClickListener {

        List<View> expandViews=new ArrayList<View>();
        List<ImageView> arrows=new ArrayList<ImageView>();
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int mLcdWidth = dm.widthPixels;
        float mDensity = dm.density;
        String[] categoryArray=getResources().getStringArray(R.array.left_drawer_list);
        String[] collectionArray={"收藏的微博", "收藏的图片"};

        public View getExpandView(int position){
            return expandViews.get(position);
        }

        public ImageView getArrowView(int position){
            return arrows.get(position);
        }

        @Override
        public int getCount() {
            return categoryArray.length;
        }

        @Override
        public Object getItem(int position) {
            return categoryArray[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view=View.inflate(getActivity(), R.layout.item_drawer, null);
            TextView tvCategory= (TextView) view.findViewById(R.id.category);
            ImageView ivArrow= (ImageView) view.findViewById(R.id.category_arrow);
            tvCategory.setText(categoryArray[position]);
            LinearLayout expandLayout= (LinearLayout) view.findViewById(R.id.drawer_expand);
            if (position == 0) {
                for (int i = 0; i < collectionArray.length; i++) {
                    TextView tv = new TextView(getActivity());
                    tv.setText(collectionArray[i]);
                    tv.setPadding((int) (20 * mDensity), (int) (10 * mDensity), (int) (20 * mDensity), (int) (10 * mDensity));
                    tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                    tv.setClickable(true);
                    tv.setBackgroundResource(R.drawable.dark_color_ripple_background);
                    tv.setOnClickListener(this);
                    expandLayout.addView(tv);
                }
                ivArrow.setVisibility(View.VISIBLE);
            } else {
                ivArrow.setVisibility(View.INVISIBLE);
            }


            expandLayout.setVisibility(View.GONE);
            if (expandViews.size()< getCount()){

                expandViews.add(expandLayout);
                arrows.add(ivArrow);

            }
            int widthSpec = View.MeasureSpec.makeMeasureSpec((int) (mLcdWidth - 10 * mDensity), View.MeasureSpec.EXACTLY);
            expandLayout.measure(widthSpec, 0);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) expandLayout.getLayoutParams();
            params.bottomMargin = -expandLayout.getMeasuredHeight();
            expandLayout.setVisibility(View.GONE);

            tvCategory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectItem(position+1);
                }
            });

            return view;
        }

        @Override
        public void onClick(View v) {
            String text=((TextView)v).getText().toString();
            if (text.equals(collectionArray[0])){
                closeDrawerDelayed();
                startActivity(new Intent(getActivity(), WeiboCollectionActivity.class));
            }else if (text.equals(collectionArray[1])) {
                closeDrawerDelayed();
                startActivity(new Intent(getActivity(), PicturesCollectionActivity.class));
            }
            HeaderViewListAdapter ha = (HeaderViewListAdapter) mDrawerListView.getAdapter();
            View expandView=((CategoryAdapter)ha.getWrappedAdapter()).getExpandView(0);
            expandView.startAnimation(new ViewExpandAnimation(expandView));
            ((CategoryAdapter)ha.getWrappedAdapter()).getArrowView(0).setRotation(0.0f);
        }
    }

    private void closeDrawerDelayed() {
        mDrawerLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                mDrawerLayout.closeDrawer(Gravity.START);
            }
        }, 300);
    }

}