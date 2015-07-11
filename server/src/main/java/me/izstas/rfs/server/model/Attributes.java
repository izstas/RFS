package me.izstas.rfs.server.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({@JsonSubTypes.Type(PosixAttributes.class), @JsonSubTypes.Type(DosAttributes.class)})
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class Attributes {
}
