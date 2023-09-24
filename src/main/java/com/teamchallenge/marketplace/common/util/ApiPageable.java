package com.teamchallenge.marketplace.common.util;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Parameter(name = "page", in = ParameterIn.QUERY, schema = @Schema(defaultValue = "0"))
@Parameter(name = "size", in = ParameterIn.QUERY, schema = @Schema(defaultValue = "20"))
@Parameter(name = "sort", in = ParameterIn.QUERY)
public @interface ApiPageable {

}
