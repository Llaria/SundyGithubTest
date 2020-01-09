package sun.sundy.sundygithubtest.design;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;

import java.util.ArrayList;
import java.util.List;

import sun.sundy.sundygithubtest.R;

public class DesignActivity extends AppCompatActivity {
    private ViewPager viewPager;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    viewPager.setCurrentItem(0);
                    return true;
                case R.id.navigation_dashboard:
                    viewPager.setCurrentItem(1);
                    return true;
                case R.id.navigation_notifications:
                    viewPager.setCurrentItem(2);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_design);
        viewPager = findViewById(R.id.view_pager);
        final BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        List<Fragment> lists = new ArrayList<>();
        lists.add(new OneFragment());
        lists.add(new TwoFragment());
        lists.add(new ThreeFragment());
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(lists,getSupportFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
                System.out.println("onPageScrolled  i=" + i + " v=" + v + " i1=" + i1);
            }

            @Override
            public void onPageSelected(int i) {
                navView.getMenu().getItem(i).setChecked(true);
            }

            @Override
            public void onPageScrollStateChanged(int i) {
                System.out.println("onPageScrollStateChanged  i=" + i);
            }
        });


        viewPager.setPageTransformer(false, new ViewPager.PageTransformer() {
            @Override
            public void transformPage(@NonNull View view, float v) {
                Animation scaleAnimation= new ScaleAnimation(0,2,0,2,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
                // 1. fromX ：动画在水平方向X的结束缩放倍数
                // 2. toX ：动画在水平方向X的结束缩放倍数
                // 3. fromY ：动画开始前在竖直方向Y的起始缩放倍数
                // 4. toY：动画在竖直方向Y的结束缩放倍数
                // 5. pivotXType:缩放轴点的x坐标的模式
                // 6. pivotXValue:缩放轴点x坐标的相对值
                // 7. pivotYType:缩放轴点的y坐标的模式
                // 8. pivotYValue:缩放轴点y坐标的相对值
                // pivotXType = Animation.ABSOLUTE:缩放轴点的x坐标 =  View左上角的原点 在x方向 加上 pivotXValue数值的点(y方向同理)
                // pivotXType = Animation.RELATIVE_TO_SELF:缩放轴点的x坐标 = View左上角的原点 在x方向 加上 自身宽度乘上pivotXValue数值的值(y方向同理)
                // pivotXType = Animation.RELATIVE_TO_PARENT:缩放轴点的x坐标 = View左上角的原点 在x方向 加上 父控件宽度乘上pivotXValue数值的值 (y方向同理)
                scaleAnimation.setDuration(3000);
                // 使用
                view.startAnimation(scaleAnimation);
            }
        });

    }

}
