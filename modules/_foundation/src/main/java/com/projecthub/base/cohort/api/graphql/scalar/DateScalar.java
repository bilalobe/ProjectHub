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

    public DateScalar() {
    }

    /**
     * @param dataFetcherResult is never null
     * @param context    the graphql context in place
     * @param locale            the locale to use
     *
     * @return
     * @throws CoercingSerializeException
     */
    @Override
    public String serialize(final Object dataFetcherResult, final GraphQLContext context, final Locale locale) {
        if (dataFetcherResult instanceof final LocalDate localDate) {
            return localDate.format(DateScalar.formatter);
        }
        throw new CoercingSerializeException("Expected LocalDate object");
    }

    /**
     * @param input          is never null
     * @param context the graphql context in place
     * @param locale         the locale to use
     *
     * @return
     * @throws CoercingParseValueException
     */
    @Override
    public LocalDate parseValue(final Object input, final GraphQLContext context, final Locale locale) {
        try {
            return LocalDate.parse(input.toString(), DateScalar.formatter);
        } catch (final RuntimeException e) {
            throw new CoercingParseValueException("Invalid date format");
        }
    }

    /**
     * @param input          is never null
     * @param variables      the resolved variables passed to the query
     * @param context the graphql context in place
     * @param locale         the locale to use
     *
     * @return
     * @throws CoercingParseLiteralException
     */
    @Override
    public LocalDate parseLiteral(final Value<?> input, final CoercedVariables variables, final GraphQLContext context, final Locale locale) {
        if (!(input instanceof StringValue stringValue)) {
            throw new CoercingParseLiteralException("Expected StringValue");
        } else {

            try {
                return LocalDate.parse(((StringValue) input).getValue(), DateScalar.formatter);
            } catch (final RuntimeException e) {
                throw new CoercingParseLiteralException("Invalid date format");
            }
        }
    }
}
