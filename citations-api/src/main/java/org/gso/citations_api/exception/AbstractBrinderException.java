package org.gso.citations_api.exception;

import lombok.Getter;
import org.gso.citations_api.dto.ErrorMessage;
import org.springframework.http.HttpStatus;

@Getter
public abstract class AbstractBrinderException extends RuntimeException{
    private final transient ErrorMessage errorMessage;
    private final HttpStatus httpStatus;



    public AbstractBrinderException(HttpStatus httpStatus, ErrorMessage errorMessage) {
        super(errorMessage.message());
        this.errorMessage = errorMessage;
        this.httpStatus = httpStatus;
    }

    public AbstractBrinderException(ErrorMessage errorMessage) {
        this(HttpStatus.INTERNAL_SERVER_ERROR, errorMessage);
    }

}
