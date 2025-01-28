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
    public CitationModel toModel() {
        return  CitationModel.builder()
                .id(this.id)
                .text(this.text)
                .build();
    }


}
