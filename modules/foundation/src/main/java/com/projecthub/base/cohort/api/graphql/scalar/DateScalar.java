package com.projecthub.base.cohort.api.graphql.scalar;

import com.netflix.graphql.dgs.DgsScalar;
import graphql.GraphQLContext;
import graphql.execution.CoercedVariables;
import graphql.language.StringValue;
import graphql.language.Value;
import graphql.schema.Coercing;
import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingParseValueException;
import graphql.schema.CoercingSerializeException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@DgsScalar(name = "Date")
public class DateScalar implements Coercing<LocalDate, String> {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;

    @Override
    public String serialize(Object dataFetcherResult, GraphQLContext context, Locale locale)
        throws CoercingSerializeException {
        if (dataFetcherResult instanceof LocalDate localDate) {
            return localDate.format(formatter);
        }
        throw new CoercingSerializeException("Expected LocalDate object");
    }

    @Override
    public LocalDate parseValue(Object input, GraphQLContext context, Locale locale)
        throws CoercingParseValueException {
        try {
            return LocalDate.parse(input.toString(), formatter);
        } catch (Exception e) {
            throw new CoercingParseValueException("Invalid date format");
        }
    }

    @Override
    public LocalDate parseLiteral(Value<?> input, CoercedVariables variables, GraphQLContext context, Locale locale)
        throws CoercingParseLiteralException {
        if (!(input instanceof StringValue)) {
            throw new CoercingParseLiteralException("Expected StringValue");
        } else {
            StringValue stringValue = (StringValue) input;

            try {
                return LocalDate.parse(((StringValue) input).getValue(), formatter);
            } catch (Exception e) {
                throw new CoercingParseLiteralException("Invalid date format");
            }
        }
    }
}
