package rmit.saintgiong.discoveryapi.internal.document;

import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;

public record Education(
        @Field(type = FieldType.Text)
        String institutionName,

        @Field(type = FieldType.Keyword)
        String degree,

        @Field(type = FieldType.Double)
        Double gpa,

        @Field(type = FieldType.Text)
        String description,

        @Field(type = FieldType.Date, format = {}, pattern = "uuuu-MM-dd'T'HH:mm:ss")
        LocalDateTime startDate,

        @Field(type = FieldType.Date, format = {}, pattern = "uuuu-MM-dd'T'HH:mm:ss")
        LocalDateTime endDate,

        @Field(type = FieldType.Boolean)
        Boolean isCurrent
) {}