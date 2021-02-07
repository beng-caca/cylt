//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package springfox.documentation.spring.web.readers.parameter;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.members.ResolvedField;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.Strings;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.schema.Collections;
import springfox.documentation.schema.Maps;
import springfox.documentation.schema.Types;
import springfox.documentation.schema.property.field.FieldProvider;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.schema.AlternateTypeProvider;
import springfox.documentation.spi.service.contexts.DocumentationContext;
import springfox.documentation.spi.service.contexts.ParameterExpansionContext;
import springfox.documentation.spring.web.plugins.DocumentationPluginsManager;

/**
 * 劫持spring ioc注入(因为项目中ioc的优先级比jar包的高)
 * class:springfox.documentation.spring.web.readers.parameter.ModelAttributeParameterExpander
 */
@Component
public class ModelAttributeParameterExpander {
    private static final Logger LOG = LoggerFactory.getLogger(ModelAttributeParameterExpander.class);
    private final FieldProvider fieldProvider;
    @Autowired
    protected DocumentationPluginsManager pluginsManager;

    public ModelAttributeParameterExpander(FieldProvider fields) {
        this.fieldProvider = fields;
    }


    public List<Parameter> expand(String parentName, ResolvedType paramType, DocumentationContext documentationContext) {
        List<Parameter> parameters = Lists.newArrayList();
        // 先判断是否死循环调用
        String[] parents = parentName.split("\\.");
        if (parents.length > 2) {
            for (int i = 1; i < parents.length;i++) {
                if(parents[i].equals(parents[0])) {
                    return FluentIterable.from(parameters).filter(Predicates.not(this.hiddenParameters())).toList();
                }
            }
        }
        Set<String> beanPropNames = this.getBeanPropertyNames(paramType.getErasedType());
        Iterable<ResolvedField> fields = FluentIterable.from(this.fieldProvider.in(paramType)).filter(this.onlyBeanProperties(beanPropNames));
        LOG.debug("Expanding parameter type: {}", paramType);
        AlternateTypeProvider alternateTypeProvider = documentationContext.getAlternateTypeProvider();
        FluentIterable<ModelAttributeField> modelAttributes = FluentIterable.from(fields).transform(this.toModelAttributeField(alternateTypeProvider));
        FluentIterable<ModelAttributeField> expendables = modelAttributes.filter(Predicates.not(this.simpleType())).filter(Predicates.not(this.recursiveType(paramType)));
        Iterator var10 = expendables.iterator();

        while (var10.hasNext()) {
            ModelAttributeField each = (ModelAttributeField) var10.next();
            LOG.debug("Attempting to expand expandable field: {}", each.getField());
            parameters.addAll(this.expand(this.nestedParentName(parentName, each.getField()), each.getFieldType(), documentationContext));
        }

        FluentIterable<ModelAttributeField> collectionTypes = modelAttributes.filter(Predicates.and(this.isCollection(), Predicates.not(this.recursiveCollectionItemType(paramType))));
        Iterator var15 = collectionTypes.iterator();

        while (var15.hasNext()) {
            ModelAttributeField each = (ModelAttributeField) var15.next();
            LOG.debug("Attempting to expand collection/array field: {}", each.getField());
            ResolvedType itemType = Collections.collectionElementType(each.getFieldType());
            if (Types.isBaseType(itemType)) {
                parameters.add(this.simpleFields(parentName, documentationContext, each));
            } else {
                parameters.addAll(this.expand(this.nestedParentName(parentName, each.getField()), itemType, documentationContext));
            }
        }

        FluentIterable<ModelAttributeField> simpleFields = modelAttributes.filter(this.simpleType());
        Iterator var17 = simpleFields.iterator();

        while (var17.hasNext()) {
            ModelAttributeField each = (ModelAttributeField) var17.next();
            parameters.add(this.simpleFields(parentName, documentationContext, each));
        }

        return FluentIterable.from(parameters).filter(Predicates.not(this.hiddenParameters())).toList();
    }

    private Predicate<ModelAttributeField> recursiveCollectionItemType(final ResolvedType paramType) {
        return new Predicate<ModelAttributeField>() {
            public boolean apply(ModelAttributeField input) {
                return Objects.equal(Collections.collectionElementType(input.getFieldType()), paramType);
            }
        };
    }

    private Predicate<Parameter> hiddenParameters() {
        return new Predicate<Parameter>() {
            public boolean apply(Parameter input) {
                return input.isHidden();
            }
        };
    }

    private Parameter simpleFields(String parentName, DocumentationContext documentationContext, ModelAttributeField each) {
        LOG.debug("Attempting to expand field: {}", each);
        String dataTypeName = (String) Optional.fromNullable(Types.typeNameFor(each.getFieldType().getErasedType())).or(each.getFieldType().getErasedType().getSimpleName());
        LOG.debug("Building parameter for field: {}, with type: ", each, each.getFieldType());
        ParameterExpansionContext parameterExpansionContext = new ParameterExpansionContext(dataTypeName, parentName, each.getField(), documentationContext.getDocumentationType(), new ParameterBuilder());
        return this.pluginsManager.expandParameter(parameterExpansionContext);
    }

    private Predicate<ModelAttributeField> recursiveType(final ResolvedType paramType) {
        return new Predicate<ModelAttributeField>() {
            public boolean apply(ModelAttributeField input) {
                return Objects.equal(input.getFieldType(), paramType);
            }
        };
    }

    private Predicate<ModelAttributeField> simpleType() {
        return Predicates.and(new Predicate[]{Predicates.not(this.isCollection()), Predicates.not(this.isMap()), Predicates.or(new Predicate[]{this.belongsToJavaPackage(), this.isBaseType(), this.isEnum()})});
    }

    private Predicate<ModelAttributeField> isCollection() {
        return new Predicate<ModelAttributeField>() {
            public boolean apply(ModelAttributeField input) {
                return Collections.isContainerType(input.getFieldType());
            }
        };
    }

    private Predicate<ModelAttributeField> isMap() {
        return new Predicate<ModelAttributeField>() {
            public boolean apply(ModelAttributeField input) {
                return Maps.isMapType(input.getFieldType());
            }
        };
    }

    private Predicate<ModelAttributeField> isEnum() {
        return new Predicate<ModelAttributeField>() {
            public boolean apply(ModelAttributeField input) {
                return input.getFieldType().getErasedType().isEnum();
            }
        };
    }

    private Predicate<ModelAttributeField> belongsToJavaPackage() {
        return new Predicate<ModelAttributeField>() {
            public boolean apply(ModelAttributeField input) {
                return packageName(input.getFieldType().getErasedType()).startsWith("java.lang");
            }
        };
    }

    private Predicate<ModelAttributeField> isBaseType() {
        return new Predicate<ModelAttributeField>() {
            public boolean apply(ModelAttributeField input) {
                return Types.isBaseType(input.getFieldType()) || input.getField().getType().isPrimitive();
            }
        };
    }

    private Function<ResolvedField, ModelAttributeField> toModelAttributeField(final AlternateTypeProvider alternateTypeProvider) {
        return new Function<ResolvedField, ModelAttributeField>() {
            public ModelAttributeField apply(ResolvedField input) {
                return new ModelAttributeField(fieldType(alternateTypeProvider, input), input);
            }
        };
    }

    private Predicate<ResolvedField> onlyBeanProperties(final Set<String> beanPropNames) {
        return new Predicate<ResolvedField>() {
            public boolean apply(ResolvedField input) {
                return beanPropNames.contains(input.getName());
            }
        };
    }

    private String nestedParentName(String parentName, ResolvedField field) {
        String name = field.getName();
        ResolvedType fieldType = field.getType();
        if (Collections.isContainerType(fieldType) && !Types.isBaseType(Collections.collectionElementType(fieldType))) {
            name = name + "[0]";
        }

        return Strings.isNullOrEmpty(parentName) ? name : String.format("%s.%s", parentName, name);
    }

    private ResolvedType fieldType(AlternateTypeProvider alternateTypeProvider, ResolvedField field) {
        return alternateTypeProvider.alternateFor(field.getType());
    }

    private String packageName(Class<?> type) {
        return (String) Optional.fromNullable(type.getPackage()).transform(this.toPackageName()).or("java");
    }

    private Function<Package, String> toPackageName() {
        return new Function<Package, String>() {
            public String apply(Package input) {
                return input.getName();
            }
        };
    }

    private Set<String> getBeanPropertyNames(Class<?> clazz) {
        try {
            Set<String> beanProps = new HashSet();
            PropertyDescriptor[] propDescriptors = this.getBeanInfo(clazz).getPropertyDescriptors();
            PropertyDescriptor[] var4 = propDescriptors;
            int var5 = propDescriptors.length;

            for (int var6 = 0; var6 < var5; ++var6) {
                PropertyDescriptor propDescriptor = var4[var6];
                if (propDescriptor.getReadMethod() != null && propDescriptor.getWriteMethod() != null) {
                    beanProps.add(propDescriptor.getName());
                }
            }

            return beanProps;
        } catch (IntrospectionException var8) {
            LOG.warn(String.format("Failed to get bean properties on (%s)", clazz), var8);
            return Sets.newHashSet();
        }
    }

    @VisibleForTesting
    BeanInfo getBeanInfo(Class<?> clazz) throws IntrospectionException {
        return Introspector.getBeanInfo(clazz);
    }
}

