package com.uet.example.api.record.dto;

import com.dslplatform.json.CompiledJson;
import com.uet.example.api.record.BaseM;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;

@CompiledJson
@NoArgsConstructor
@AllArgsConstructor
public class ErrorDTO extends BaseM.DTO {
    public List<String> errors;
}
