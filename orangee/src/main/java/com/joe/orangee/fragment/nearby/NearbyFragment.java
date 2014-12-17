package com.joe.orangee.fragment.nearby;

import android.app.Fragment;
import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.joe.orangee.R;
import com.joe.orangee.activity.nearby.NearbyMapWeiboActivity;
import com.joe.orangee.adapter.WeiboStatusAdapter;
import com.joe.orangee.library.fadingactionbar.FadingActionBarHelper;
import com.joe.orangee.listener.OrangeeImageLoadingListener;
import com.joe.orangee.model.WeiboStatus;
import com.joe.orangee.net.WeiboDownloader;
import com.joe.orangee.util.Constants;
import com.joe.orangee.util.Utils;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;


public class NearbyFragment extends Fragment implements AMapLocationListener {

    private View footerView;
    private Context context;
    private int page=1;
    private List<WeiboStatus> weiboList;
    private WeiboStatusAdapter mAdapter;
    private ListView contentView;
    private LocationManagerProxy locationManager;
    private double latitude;
    private double longitude;
    private View headerView;
    private ImageView headerImg;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
        context=getActivity();

        headerView =View.inflate(getActivity(), R.layout.header_light, null);
        headerImg= (ImageView) headerView.findViewById(R.id.image_header);

        contentView = (ListView) View.inflate(getActivity(), R.layout.activity_listview, null);
        FadingActionBarHelper mFadingHelper = new FadingActionBarHelper()
                .actionBarBackground(R.drawable.orangee_color_solid)
                .headerView(headerView)
                .contentView(contentView)
                .lightActionBar(true);

        mFadingHelper.initActionBar((NearbyMapWeiboActivity)context);

        footerView = View.inflate(context, R.layout.footer_view, null);
        contentView.addFooterView(footerView);
        contentView.setOnScrollListener(onScrollListener);
        locationManager = LocationManagerProxy.getInstance(getActivity());
        locationManager.setGpsEnable(true);
        // API定位采用GPS定位方式，第一个参数是定位provider，第二个参数时间最短是2000毫秒，第三个参数距离间隔单位是米，第四个参数是定位监听者
        locationManager.requestLocationData(LocationProviderProxy.AMapNetwork, 1000000, 10, this);
        fillData();


		return mFadingHelper.createView(inflater);
	}

    private void fillData() {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                weiboList=new WeiboDownloader(context).getNearbyStatusList(latitude, longitude, 20, page);
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                if (weiboList.size()==0) {
                    footerView.setVisibility(View.GONE);
                }
                if (mAdapter==null) {
                    mAdapter=new WeiboStatusAdapter(weiboList, context);
                    contentView.setAdapter(mAdapter);
                }else {
                    mAdapter.addData(weiboList);
                    mAdapter.notifyDataSetChanged();
                }
                super.onPostExecute(result);
            }
        }.execute();

    }

    private AbsListView.OnScrollListener onScrollListener=new AbsListView.OnScrollListener() {

        private int lastItemIndex;//当前ListView中最后一个Item的索引
        //当ListView不在滚动，并且ListView的最后一项的索引等于adapter的项数减一时则自动加载（因为索引是从0开始的）
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE  && lastItemIndex > mAdapter.getCount() - 1-5) {
                page+=1;
                fillData();
            }
        }
        @Override
        public void onScroll(AbsListView view, int firstVisibleItem,
                             int visibleItemCount, int totalItemCount) {
            //ListView 的FooterView也会算到visibleItemCount中去，所以要再减去一
            lastItemIndex = firstVisibleItem + visibleItemCount - 1 -1;
        }
    };

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        latitude=aMapLocation.getLatitude();
        longitude=aMapLocation.getLongitude();
        String mapImageUrl= Constants.STATIC_MAP_URL+"&markers=mid,,:"+longitude+","+latitude;

        ImageLoader.getInstance().displayImage(mapImageUrl, headerImg, Utils.getNoDefaultImageOptions(), new OrangeeImageLoadingListener.LoadingListener());
        fillData();
    }

    @Override
    public void onLocationChanged(Location location) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onPause() {
        super.onPause();
        if (locationManager != null) {
            locationManager.removeUpdates(this);
            locationManager.destory();
        }
        locationManager = null;
    }

}
