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
    public String serialize(final Object dataFetcherResult, final GraphQLContext context, final Locale locale)
        throws CoercingSerializeException {
        if (dataFetcherResult instanceof final LocalDate localDate) {
            return localDate.format(DateScalar.formatter);
        }
        throw new CoercingSerializeException("Expected LocalDate object");
    }

    @Override
    public LocalDate parseValue(final Object input, final GraphQLContext context, final Locale locale)
        throws CoercingParseValueException {
        try {
            return LocalDate.parse(input.toString(), DateScalar.formatter);
        } catch (final Exception e) {
            throw new CoercingParseValueException("Invalid date format");
        }
    }

    @Override
    public LocalDate parseLiteral(final Value<?> input, final CoercedVariables variables, final GraphQLContext context, final Locale locale)
        throws CoercingParseLiteralException {
        if (!(input instanceof StringValue stringValue)) {
            throw new CoercingParseLiteralException("Expected StringValue");
        } else {

            try {
                return LocalDate.parse(((StringValue) input).getValue(), DateScalar.formatter);
            } catch (final Exception e) {
                throw new CoercingParseLiteralException("Invalid date format");
            }
        }
    }
}
