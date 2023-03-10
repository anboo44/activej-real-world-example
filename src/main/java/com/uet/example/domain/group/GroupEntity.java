package com.uet.example.domain.group;

import com.uet.example.domain.user.UserEntity;
import com.uet.example.util.exception.BusinessException;
import com.uet.example.util.model.IEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.SneakyThrows;
import lombok.ToString;

import java.util.List;

import static jakarta.persistence.EnumType.STRING;

@Data
@Entity
@ToString
@Table(name = "user_group_tbl")
public class GroupEntity implements IEntity<GroupId> {
    //-------------/ AREA: Declaration of fields /-----------------//
    @EmbeddedId
    @GeneratedValue(strategy = GenerationType.AUTO)
    private GroupId id;

    private String name;

    @Enumerated(STRING)
    @Column(name = "group_type")
    private GroupType groupType;

    @OneToMany(mappedBy = "group")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private List<UserEntity> users;

    //-------------/ AREA: Business logic /-----------------//
    @Transient
    public static final int MAX_SIZE = 20;

    @SneakyThrows
    public void addUser(UserEntity user) {
        if (this.users.size() == MAX_SIZE) { throw new BusinessException("It is over size"); }

        var isInGroup = this.users.stream().anyMatch(u -> u.getId().equals(user.getId()));
        if (isInGroup) { throw new BusinessException("User existed in group"); }

        this.users.add(user);
    }
}
