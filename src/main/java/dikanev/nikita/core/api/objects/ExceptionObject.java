package dikanev.nikita.core.api.objects;

import dikanev.nikita.core.api.exceptions.ApiException;

public class ExceptionObject extends ApiObject {

    private String description;

    private String message;

    private Integer code;

    public ExceptionObject(ApiException ex) {
        super("error");

        this.code = ex.getCode();
        this.message = ex.getMessage();
        this.description = ex.getDescription();
    }
}
