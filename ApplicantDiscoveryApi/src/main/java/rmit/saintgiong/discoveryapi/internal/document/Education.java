package rmit.saintgiong.discoveryapi.internal.document;

import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import rmit.saintgiong.discoveryapi.internal.common.types.type.DegreeType;

import java.time.LocalDateTime;
import java.util.UUID;

public record Education(
        @Field(type = FieldType.Keyword)
        UUID educationId,

        @Field(type = FieldType.Keyword)
        UUID applicantId,

        @Field(type = FieldType.Text)
        String institutionName,

        @Field(type = FieldType.Keyword)
        DegreeType degree,

        @Field(type = FieldType.Double)
        Double gpa,

        @Field(type = FieldType.Text)
        String description,

        @Field(type = FieldType.Date, format = {}, pattern = "uuuu-MM-dd'T'HH:mm:ss")
        LocalDateTime startDate,

        @Field(type = FieldType.Date, format = {}, pattern = "uuuu-MM-dd'T'HH:mm:ss")
        LocalDateTime endDate,

        @Field(type = FieldType.Boolean)
        Boolean isCurrent,

        @Field(type = FieldType.Date, format = {}, pattern = "uuuu-MM-dd'T'HH:mm:ss")
        LocalDateTime createdAt,

        @Field(type = FieldType.Date, format = {}, pattern = "uuuu-MM-dd'T'HH:mm:ss")
        LocalDateTime updatedAt
) {}
