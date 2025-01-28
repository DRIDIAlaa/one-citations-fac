package org.gso.citations_api.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.gso.citations_api.dto.CitationDto;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Data
@Builder
@Document
@NoArgsConstructor
@AllArgsConstructor
public class CitationModel {
    @Id
    private String id;
    private String text;
    @DBRef
    private String writerName;
    @DBRef
    private String validatorName;
    @Field("validated")
    private boolean validated; // To track validation status
    @CreatedDate
    private LocalDateTime submissionDate;
    @CreatedDate
    private LocalDateTime modificationDate;

    public CitationDto toDto() {
        return CitationDto.builder()
                .id(this.id)
                .text(this.text)
                .submissionDate(this.submissionDate)
                .modificationDate(this.modificationDate)
                .validated(this.validated)
                .writerName(this.writerName)
                .validatorName(this.validatorName)
                .build();
    }
}

