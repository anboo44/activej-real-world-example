package com.uet.example.domain.user;

import com.uet.example.domain.group.GroupEntity;
import com.uet.example.util.model.IEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Data
@Entity
@ToString
@Table(name = "user_tbl")
public class UserEntity implements IEntity<UserId> {
    //-------------/ AREA: Declaration of fields /-----------------//
    @EmbeddedId
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UserId id;

    private String name;

    private Integer age;

    @ManyToOne
    @Fetch(value = FetchMode.SELECT)
    @JoinColumn(name = "group_id")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private GroupEntity group;
}
