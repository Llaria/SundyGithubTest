package sun.sundy.sundygithubtest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.List;

import sun.sundy.sundygithubtest.sql.entity.CodeEntity;
import sun.sundy.sundygithubtest.sql.entity.UserEntity;
import sun.sundy.sundygithubtest.sql.gen.CodeEntityDao;
import sun.sundy.sundygithubtest.sql.gen.UserEntityDao;
import sun.sundy.sundygithubtest.sql.utils.BizDaoManager;

public class SqlActivity extends AppCompatActivity {

    private UserEntityDao userEntityDao;
    private CodeEntityDao codeEntityDao;
    private long time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sql);
        userEntityDao = BizDaoManager.getInstance().getDaoSession().getUserEntityDao();
        codeEntityDao = BizDaoManager.getInstance().getDaoSession().getCodeEntityDao();
    }

    public void start(View view) {
        time = System.currentTimeMillis();
        select();
        System.out.println("查询时间==========>>>>>>>>" + (System.currentTimeMillis() -  time) + "ms");
    }

    private void select() {
        QueryBuilder<UserEntity> queryBuilder = userEntityDao.queryBuilder();
        queryBuilder.where(UserEntityDao.Properties.Name.eq("Sundy33"),UserEntityDao.Properties.Age.eq(33));
        List<UserEntity> list = queryBuilder.build().list();
        System.out.println("查询结果==========>>>>>>>>一共" + list.size() + "条，结果：" + list.toString());
    }

    public void delete(View view) {
        userEntityDao.deleteAll();
    }

    public void insert(View view) {
        time = System.currentTimeMillis();
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<UserEntity> datas = new ArrayList<>();
                for (int i = 0; i < 10000; i++) {
                    UserEntity userEntity = new UserEntity();
                    userEntity.setAge(i);
                    userEntity.setName("Sundy" + i);
                    datas.add(userEntity);
                    userEntityDao.insert(userEntity);
                }
//                userEntityDao.insertInTx(datas);
                System.out.println("插入时间==========>>>>>>>>" + (System.currentTimeMillis() -  time) / 1000 + "s");
            }
        }).start();
    }

    public void insert_single(View view) {
        UserEntity userEntity = new UserEntity();
        userEntity.setName("Sundy33");
        userEntity.setAge(33);
        userEntityDao.insert(userEntity);

        UserEntity userEntity2 = new UserEntity();
        userEntity2.setName("llaria");
        userEntity2.setAge(1);
        userEntityDao.insert(userEntity2);

        CodeEntity codeEntity = new CodeEntity();
        codeEntity.setIndex(1);
        codeEntity.setIndexName("一");
        codeEntityDao.insert(codeEntity);

    }

    public void query_all(View view) {
        System.out.println("全部结果=======>>>>>>>>>>" + userEntityDao.loadAll().toString());
        System.out.println("全部结果=======>>>>>>>>>>" + codeEntityDao.loadAll().toString());
    }
}
