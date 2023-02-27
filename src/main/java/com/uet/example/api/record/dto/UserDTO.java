package com.uet.example.api.record.dto;

import com.dslplatform.json.CompiledJson;
import com.uet.example.api.record.BaseM;
import com.uet.example.domain.user.UserEntity;
import lombok.NoArgsConstructor;

@CompiledJson
@NoArgsConstructor
public class UserDTO extends BaseM.DTO {
    public int    id;
    public int    age;
    public String name;

    public static UserDTO apply(UserEntity entity) {
        UserDTO dto = new UserDTO();

        dto.id   = entity.getId().getValue();
        dto.age  = entity.getAge();
        dto.name = entity.getName();

        return dto;
    }
}
