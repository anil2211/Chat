package com.example.anil.chat;

/**
 * Created by anil3 on 12-04-2018.
 */

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;


/**
 * Created by anil3 on 05-04-2018.
 */

class TabsPagerAdapter extends FragmentPagerAdapter {

    public TabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch(position)
        {
            case 0:
                RequestsFragment requestFragment=new RequestsFragment();
                return requestFragment;


            case 1:
                ChatsFragment chatFragment=new ChatsFragment();
                return chatFragment;

            case 2:
                FriendsFragment friendsFragment=new FriendsFragment();
                return friendsFragment;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        //for three fragment
        return 3;
    }

    // title to load fragment

    public CharSequence getPageTitle (int position)
    {
        switch (position)
        {
            case 0:
                return "Request";

            case 1:
                return "Chats";

            case 2:
                return "Friends";

            default:
                return null;
        }
    }
}


