package com.example.chatroom.Users.Chat;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;

import com.example.chatroom.R;

public class ViewPager extends AppCompatActivity {

    androidx.viewpager.widget.ViewPager viewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_pager);
        viewPager=findViewById(R.id.viewpager);
        viewPager.setAdapter(new CustomPageAdapter(this));
    }

    private class CustomPageAdapter extends PagerAdapter {
        private Context context;
        
        public CustomPageAdapter(ViewPager viewPager) {
        }

        @Override
        public int getCount() {
            return 0;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return false;
        }
    }
}
