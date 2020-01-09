package sun.sundy.sundygithubtest.design;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * @author Sundy
 * create at 2019-08-29 16:40
 */
public class ViewPagerAdapter extends FragmentPagerAdapter {

    private List<Fragment> lists;

    public ViewPagerAdapter(List<Fragment> lists ,FragmentManager fm) {
        super(fm);
        this.lists = lists;
    }

    @Override
    public Fragment getItem(int i) {
        return lists.get(i);
    }

    @Override
    public int getCount() {
        return lists.size();
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        return super.instantiateItem(container, position);
    }


    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return super.isViewFromObject(view, object);
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        super.destroyItem(container, position, object);
    }
}
