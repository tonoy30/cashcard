package me.tonoy.cashcard.common;

import org.apache.commons.lang3.math.NumberUtils;
import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.internal.util.config.ConfigurationHelper;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.Type;

import java.util.Optional;
import java.util.Properties;


public class RandomIdGenerator implements IdentifierGenerator {
    public static final String ID_GENERATOR_NAME = "randomIdGenerator";
    public static final String VALUE_PREFIX_PARAMETER = "valuePrefix";
    public static final String VALUE_PREFIX_DEFAULT = "";
    private String valuePrefix;

    public static final String LENGTH = "";
    public static final String DEFAULT_LENGTH = "12";
    private String idLength;

    @Override
    public void configure(Type type, Properties parameters, ServiceRegistry serviceRegistry) throws MappingException {
        valuePrefix = ConfigurationHelper.getString(VALUE_PREFIX_PARAMETER, parameters, VALUE_PREFIX_DEFAULT);
        idLength = ConfigurationHelper.getString(LENGTH, parameters, DEFAULT_LENGTH);
    }

    @Override
    public Object generate(SharedSessionContractImplementor session, Object object) throws HibernateException {
        return RandomString.next(valuePrefix, Optional.of(NumberUtils.toInt(idLength)));
    }
}
