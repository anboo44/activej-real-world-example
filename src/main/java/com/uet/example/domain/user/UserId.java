package com.uet.example.domain.user;

import com.uet.example.util.model.Identifier;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public final class UserId implements Identifier<UserEntity> {
    private Integer id;

    @Override
    public Integer getValue() { return id; }
}
