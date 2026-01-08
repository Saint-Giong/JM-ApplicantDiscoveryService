package rmit.saintgiong.discoveryapi.internal.document;

import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;
import java.util.UUID;

public record WorkExperience(
        @Field(type = FieldType.Keyword)
        UUID experienceId,

        @Field(type = FieldType.Keyword)
        UUID applicantId,

        @Field(type = FieldType.Text)
        String companyName,

        @Field(type = FieldType.Text)
        String position,

        @Field(type = FieldType.Text)
        String description,

        @Field(type = FieldType.Keyword)
        String country,

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
