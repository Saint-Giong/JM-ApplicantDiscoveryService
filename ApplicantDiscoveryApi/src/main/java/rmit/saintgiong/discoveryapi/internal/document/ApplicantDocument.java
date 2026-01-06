package rmit.saintgiong.discoveryapi.internal.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;
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

        @Field(type = FieldType.Keyword) String avatarUrl,

        @Field(type = FieldType.Keyword) Country country,

        @Field(type = FieldType.Date, format = {}, pattern = "uuuu-MM-dd'T'HH:mm:ss") LocalDateTime createdAt,

        @Field(type = FieldType.Date, format = {}, pattern = "uuuu-MM-dd'T'HH:mm:ss") LocalDateTime updatedAt){
}

