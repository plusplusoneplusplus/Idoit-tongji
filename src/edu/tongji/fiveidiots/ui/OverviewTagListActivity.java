package edu.tongji.fiveidiots.ui;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SlidingDrawer;
import android.widget.TextView;
import edu.tongji.fiveidiots.R;
import edu.tongji.fiveidiots.util.TestingHelper;

/**
 * 抽离出的侧滑taglist，解耦
 * 派生关系：GDActivity->OverviewActionBarActivity->OverviewTagListActivity->OverviewTaskListActivity
 * 或者：	 GDActivity->OverviewActionBarActivity->OverviewTaskListActivity
 * 即：OverviewTaskListActivity必须实现ActionBar，可选择性增加滑动taglist
 * @author IRainbow5
 *
 */
		
public abstract class OverviewTagListActivity extends OverviewActionBarActivity {

	private ListView mTagListView;
	private SlidingDrawer mSD;
	private List<String> mTags = new ArrayList<String>();
	private TagListAdapter mTagListAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);

		mSD = (SlidingDrawer) findViewById(R.id.sliding_drawer);

		mTagListAdapter = new TagListAdapter();
		mTags = TestingHelper.getRandomTagList();
		mTagListView = (ListView) findViewById(R.id.tagListView);
		mTagListView.setAdapter(mTagListAdapter);
		mTagListView.setOnItemClickListener(mTagListAdapter);
	}
	
	private class TagListAdapter extends BaseAdapter implements OnItemClickListener{

		@Override
		public int getCount() {
			return mTags.size();
		}

		@Override
		public Object getItem(int position) {
			return mTags.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			//优化加载
			if(convertView == null){
				convertView = LayoutInflater.from(OverviewTagListActivity.this).inflate(R.layout.taglist_item, null);
				ItemCache ic = new ItemCache();
				ic.mTextView = (TextView)convertView.findViewById(R.id.TL_tagNameTextView);
				
				convertView.setTag(ic);
			}
			
			ItemCache cache = (ItemCache)convertView.getTag();
			cache.mTextView.setText(mTags.get(position));

			return convertView;
		}

		/**
		 * 选中一个标签后把taglist关闭
		 */
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			mSD.animateClose();
		}
		
	}

	//Item缓冲类
	private static class ItemCache{
		public TextView mTextView;
	}
}
