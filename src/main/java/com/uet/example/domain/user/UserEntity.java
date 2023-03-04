package com.uet.example.domain.user;

import com.uet.example.domain.group.GroupEntity;
import com.uet.example.util.model.IEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.NaturalId;

/**
 * @NaturalId sử dụng để định danh đối tượng đó một cách duy nhất trong Hibernate, tương tự như khóa chính (primary key)
 * ~> chỉ có tác dụng khi hibernate gen bảng trong DB, nếu sử dụng schema để gen thì không có tác dụng
 *
 * private @Version Timestamp version; ~> optimistic locking
 *
 * @Filter thêm điều kiện lọc (filtering) vào các truy vấn Hibernate 1 cách tự động
 *
 * @@SecondaryTable for multi-table
 */
@Data
@Entity
@ToString
@DynamicInsert // Generate SQL dynamically with only needed columns
@DynamicUpdate // Generate SQL dynamically with only needed columns ~> Nó sẽ get record từ DB lên và so sánh
@Table(name = "user")
public class UserEntity implements IEntity<UserId> {
    //-------------/ AREA: Declaration of fields /-----------------//
    @EmbeddedId
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UserId id;

    @NaturalId
    @Column(name = "name", nullable = false, unique = true)
    private String name;

    private Integer age;

    @ManyToOne
    @JoinColumn(name = "group_id")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private GroupEntity group;

    @PrePersist
    public void onCreate() {
        if (this.id == null) {
            this.id = new UserId(0);
            System.out.println("Set ID successfully");
        }
    }
}
