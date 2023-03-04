package com.uet.example.domain.user;

import com.uet.example.util.model.Identifier;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * 1. Can use @Embeddable with @EmbeddedId to custom id
 * 2. @IdClass(UserId.class) with @Id (Integer id)
 * 3.
 */

@ToString
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public final class UserId implements Identifier<Integer>, Serializable {
    private Integer id;

    @Override
    public Integer getValue() { return id; }
}
