package com.uet.example.domain.user;

import com.uet.example.domain.group.GroupEntity;
import com.uet.example.util.model.IEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@Entity
@ToString
@Table(name = "user")
public class UserEntity implements IEntity<UserId> {
    //-------------/ AREA: Declaration of fields /-----------------//
    @EmbeddedId
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UserId id;

    private String name;

    private Integer age;

    @ManyToOne
    @JoinColumn(name = "group_id")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private GroupEntity group;
}
