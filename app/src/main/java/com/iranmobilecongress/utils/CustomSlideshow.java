package com.iranmobilecongress.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;

import com.iranmobilecongress.R;

import java.util.Timer;
import java.util.TimerTask;

public class CustomSlideshow implements OnClickListener, OnItemSelectedListener {
    public static final int fakeImagesNum = 10000;
    int last = fakeImagesNum / 2;
    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.arg1) {
                case 0:
                    if (last < fakeImagesNum - 1) {
                        g.setSelection(last + 1, true);
                    } else
                        g.setSelection(last - 1, true);
                    break;
            }
            super.handleMessage(msg);
        }
    };
    public static final int realImagesNum = 5;
    ImageView back, next;
    Gallery g;
    ImageView r1, r2, r3, r4, r5;
    TextView title;
    int page = 0;
    Context context;

    public CustomSlideshow(Context c) {
        context = c;
        title = (TextView) ((Activity) context).findViewById(R.id.title);
        next = (ImageView) ((Activity) context).findViewById(R.id.rightArrow);
        back = (ImageView) ((Activity) context).findViewById(R.id.leftArrow);
        g = (Gallery) ((Activity) context).findViewById(R.id.gallerySlideshow);
        r1 = (ImageView) ((Activity) context).findViewById(R.id.radioButton1);
        r2 = (ImageView) ((Activity) context).findViewById(R.id.radioButton2);
        r3 = (ImageView) ((Activity) context).findViewById(R.id.radioButton3);
        r4 = (ImageView) ((Activity) context).findViewById(R.id.radioButton4);
        r5 = (ImageView) ((Activity) context).findViewById(R.id.radioButton5);

        next.setOnClickListener(this);
        back.setOnClickListener(this);
        g.setAdapter(new ImageAdapter(context, fakeImagesNum));
        g.setFadingEdgeLength(0);
        g.setHapticFeedbackEnabled(true);
        g.setSpacing(20);
        g.setSelection(5000);
        g.setOnItemSelectedListener(this);

        // Typeface
        // tf=Typeface.createFromAsset(context.getAssets(),"fonts/bzar.ttf" );
        // title.setTypeface(tf);

        r1.setBackgroundResource(R.drawable.ic_radio_selected);
        r2.setBackgroundResource(R.drawable.ic_radio);
        r3.setBackgroundResource(R.drawable.ic_radio);
        r4.setBackgroundResource(R.drawable.ic_radio);
        r5.setBackgroundResource(R.drawable.ic_radio);
        Timer timer = new Timer();
        last = fakeImagesNum / 2;
        TimerTask task = new TimerTask() {

            @Override
            public void run() {
                Message m;
                // if (m == null) {
                m = new Message();
                m.arg1 = 0;
                // }
                handler.sendMessage(m);
            }
        };
        timer.schedule(task, 6000, 6000);
    }

    public void setRadioSelected(int i) {
        r1.setBackgroundResource(R.drawable.ic_radio);
        r2.setBackgroundResource(R.drawable.ic_radio);
        r3.setBackgroundResource(R.drawable.ic_radio);
        r4.setBackgroundResource(R.drawable.ic_radio);
        r5.setBackgroundResource(R.drawable.ic_radio);
        switch (i) {
            case 0:
                r1.setBackgroundResource(R.drawable.ic_radio_selected);
                break;
            case 1:
                r2.setBackgroundResource(R.drawable.ic_radio_selected);
                break;
            case 2:
                r3.setBackgroundResource(R.drawable.ic_radio_selected);
                break;
            case 3:
                r4.setBackgroundResource(R.drawable.ic_radio_selected);
                break;
            case 4:
                r5.setBackgroundResource(R.drawable.ic_radio_selected);
                break;
        }
        title.setText(context.getResources().getStringArray(
                R.array.slideshowTitles)[page]);
    }

    @Override
    public void onClick(View arg0) {
        switch (arg0.getId()) {
            case R.id.leftArrow:
                if (last > 0) {
                    g.setSelection(last - 1, true);
                }
                break;
            case R.id.rightArrow:
                if (last < fakeImagesNum - 1) {
                    g.setSelection(last + 1, true);
                }
                break;
        }

    }

    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
                               long arg3) {
        page += arg2 - last;
        page %= realImagesNum;
        last = arg2;
        if (page < 0)
            page += realImagesNum;
        setRadioSelected(page);
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {

    }

    public class ImageAdapter extends BaseAdapter {

        int mGalleryItemBackground;
        int length;
        private Context mContext;

        public ImageAdapter(Context c, int length) {
            mContext = c;
            this.length = length;
        }

        public int getCount() {
            return length;
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            position = position % 5 + 1;
            ImageView i = new ImageView(mContext);
            i.setScaleType(ScaleType.CENTER_INSIDE);
            i.setBackgroundResource(getResourceIdByName(mContext, "slide"
                    + position, "drawable"));
            return i;
        }

        public int getResourceIdByName(Context ctx, String name, String category) {
            return ctx.getResources().getIdentifier(name, category,
                    ctx.getPackageName());
        }
    }
}
