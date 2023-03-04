package com.uet.example.domain.group;

import com.uet.example.util.model.Identifier;
import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@Embeddable
public class GroupId implements Identifier<Integer> {
    private Integer id;

    @Override
    public Integer getValue() { return id; }
}
