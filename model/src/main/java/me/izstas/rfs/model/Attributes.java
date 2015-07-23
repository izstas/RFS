package me.izstas.rfs.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Represents file attributes.
 * @see PosixAttributes
 * @see DosAttributes
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({@JsonSubTypes.Type(PosixAttributes.class), @JsonSubTypes.Type(DosAttributes.class)})
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class Attributes {
}
