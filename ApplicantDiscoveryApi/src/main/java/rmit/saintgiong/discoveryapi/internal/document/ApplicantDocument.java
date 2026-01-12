package rmit.saintgiong.discoveryapi.internal.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Document(indexName = "applicants")
public record ApplicantDocument(
        @Id @Field(type = FieldType.Keyword) UUID applicantId,

        @Field(type = FieldType.Text) String firstName,

        @Field(type = FieldType.Text) String lastName,

        @Field(type = FieldType.Keyword) String phone,

        @Field(type = FieldType.Text) String address,

        @Field(type = FieldType.Keyword) String city,

        @Field(type = FieldType.Text) String biography,

        @Field(type = FieldType.Text) String aboutMe,

        @Field(type = FieldType.Keyword) String avatarUrl,

        @Field(type = FieldType.Keyword) String country,

        @Field(type = FieldType.Nested) List<Education> educations,

        @Field(type = FieldType.Nested) List<WorkExperience> workExperiences,

        @Field(type = FieldType.Long) List<Long> skillIds,

        @Field(type = FieldType.Text) List<String> skillNames,

        @Field(type = FieldType.Date, format = {}, pattern = "uuuu-MM-dd'T'HH:mm:ss") LocalDateTime createdAt,

        @Field(type = FieldType.Date, format = {}, pattern = "uuuu-MM-dd'T'HH:mm:ss") LocalDateTime updatedAt
) {
}