package com.api.stuv.global.util.common;

import com.querydsl.core.types.dsl.DateTimePath;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringTemplate;

import java.time.LocalDateTime;

public class TemplateUtils {
    public static StringTemplate getImageUrl(String imageUrl, NumberPath<Long> id) {
        return Expressions.stringTemplate("CONCAT({0}, {1})", imageUrl, id);
    }

    public static StringTemplate timeFormater(DateTimePath<LocalDateTime> dateTimePath) {
        return Expressions.stringTemplate("DATE_FORMAT({0}, '%Y-%m-%d %H:%i:%s')", dateTimePath);
    }
}
