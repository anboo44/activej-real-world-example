package com.uet.example.api.record.vo;

import com.uet.example.api.record.BaseM;
import com.uet.example.domain.user.UserEntity;
import com.uet.example.domain.user.UserId;

import java.util.ArrayList;
import java.util.List;

public class UserVO extends BaseM.VO {
    public Integer age;
    public String  name;

    public List<String> validate() {
        var errorLst = new ArrayList<String>();

        if (age == null) { errorLst.add("Field age is required"); }
        if (name == null) { errorLst.add("Field name is required"); }

        if (name != null) {
            if (name.isEmpty() || name.isBlank()) { errorLst.add("Field name cannot empty"); }
        }
        if (age != null) {
            if (age < 18) { errorLst.add("Field age must be less than 18"); }
        }

        return errorLst;
    }

    public UserEntity toEntity() {
        var entity = new UserEntity();

        entity.setAge(this.age);
        entity.setName(this.name);

        return entity;
    }
}
