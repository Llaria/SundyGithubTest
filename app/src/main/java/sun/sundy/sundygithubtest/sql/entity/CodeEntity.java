package sun.sundy.sundygithubtest.sql.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Keep;

@Entity
public class CodeEntity {
    @Id(autoincrement = true)
    private Long id;

    private String indexName;
    private int index;



    @Generated(hash = 644455028)
    public CodeEntity(Long id, String indexName, int index) {
        this.id = id;
        this.indexName = indexName;
        this.index = index;
    }
    @Generated(hash = 1097874036)
    public CodeEntity() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getIndexName() {
        return this.indexName;
    }
    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }
    public int getIndex() {
        return this.index;
    }
    public void setIndex(int index) {
        this.index = index;
    }


    @Keep
    @Override
    public String toString() {
        return "CodeEntity{" +
                "id=" + id +
                ", indexName='" + indexName + '\'' +
                ", index=" + index +
                '}';
    }
}
