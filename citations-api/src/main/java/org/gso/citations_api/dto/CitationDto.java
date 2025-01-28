package org.gso.citations_api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.gso.citations_api.model.CitationModel;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class CitationDto {
    private String id;
    private String text;
    private String writerName;
    private String validatorName;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "YYYY-MM-DD HH:mm:ss")
    private LocalDateTime submissionDate;
    @CreatedDate
    private LocalDateTime modificationDate;
    private boolean validated; // To track validation status
    public CitationModel toModel() {
        return  CitationModel.builder()
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
