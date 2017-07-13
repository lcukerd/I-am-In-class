package lcukerd.com.iaminclass;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class Main_Activity extends AppCompatActivity
{

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private DetectMode detector;
    private final String tag = "Main Activity";
    private static Context context;
    private DBinteract interact;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_tab);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        context = this;
        interact = new DBinteract(this);
        //interact.readfromDB(1,eventDBcontract.ListofItem.columnID + " DESC");
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }
    }

    protected void onDestroy()
    {
        super.onDestroy();
        //detector.Destroy();
        Log.d(tag,"Destroyer Called");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static class PlaceholderFragment extends Fragment
    {

        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment()
        {
        }
        public static PlaceholderFragment newInstance(int sectionNumber)
        {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
        {
            View rootView = null;
            if (getArguments().getInt(ARG_SECTION_NUMBER)==1)
            {
                rootView = inflater.inflate(R.layout.fragment_main_today, container, false);
                BarChart barChart = (BarChart) rootView.findViewById(R.id.barGraph);
                ArrayList<BarEntry> entries = new ArrayList<>();
                entries.add(new BarEntry(0, 21));
                entries.add(new BarEntry(1, 23));
                entries.add(new BarEntry(2, 22));
                ArrayList<BarEntry> entries2 = new ArrayList<>();
                entries2.add(new BarEntry(3, 24));
                entries2.add(new BarEntry(4, 25));
                entries2.add(new BarEntry(5, 26));
                BarDataSet dataset = new BarDataSet(entries, "Class pattern");
                BarDataSet dataset2 = new BarDataSet(entries2, "Class pattern");
                ArrayList labels = new ArrayList();
                labels.add("January");
                labels.add("February");
                labels.add("March");
                labels.add("April");
                labels.add("May");
                labels.add("June");
                barChart.animateY(1000);
                barChart.animateX(1000);
                dataset.setColors(ColorTemplate.MATERIAL_COLORS);
                BarData data = new BarData(dataset);
                data.addDataSet(dataset2);
                barChart.setData(data);

            }
            else if (getArguments().getInt(ARG_SECTION_NUMBER)==2)
            {
                //rootView =  inflater.inflate(R.layout.fragment_main_daily, container, false);
                rootView =  inflater.inflate(R.layout.tempfortest, container, false);
                Button start ,stop, send;
                start = (Button) rootView.findViewById(R.id.textView);
                stop = (Button) rootView.findViewById(R.id.textView6);
                send = (Button) rootView.findViewById(R.id.textView7);
                send.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        Intent email = new Intent(Intent.ACTION_SEND);
                        email.putExtra(Intent.EXTRA_EMAIL, new String[]{ "Harshit05081997@gmail.com"});
                        email.putExtra(Intent.EXTRA_SUBJECT, "TEST");
                        email.putExtra(Intent.EXTRA_TEXT, "Nothing");
                        email.setType("*/*");
                        File file = new File(Environment.getExternalStorageDirectory() + "/" +  "samplefile.txt");
                        Uri path = Uri.fromFile(file);
                        email.putExtra(Intent.EXTRA_STREAM,path);
                        startActivity(Intent.createChooser(email, "Choose an Email client :"));
                    }
                });
                start.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        context.startService(new Intent(context,DetectMode.class));
                    }
                });
                stop.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        context.stopService(new Intent(context,DetectMode.class));
                    }
                });

            }
            else if (getArguments().getInt(ARG_SECTION_NUMBER)==1)
            {
                rootView =  inflater.inflate(R.layout.fragment_main_monthly, container, false);
            }
            return rootView;
        }
    }
    public class SectionsPagerAdapter extends FragmentPagerAdapter
    {

        public SectionsPagerAdapter(FragmentManager fm)
        {
            super(fm);
        }

        @Override
        public Fragment getItem(int position)
        {
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount()
        {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position)
        {
            switch (position)
            {
                case 0:
                    return "Today";
                case 1:
                    return "Daily";
                case 2:
                    return "Monthly";
            }
            return null;
        }
    }
}
