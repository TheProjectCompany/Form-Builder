package com.tpc.form_builder.repository;

import com.tpc.form_builder.models.FieldData;
import com.tpc.form_builder.models.Submission;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SubmissionSpecificationBuilder {

    public static Specification<Submission> build(
            UUID formId,
            List<SubmissionFilter> filters) {

        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            // always filter by form
            predicates.add(cb.equal(root.get("formId"), formId));

            for (SubmissionFilter filter : filters) {

                Subquery<Long> sq = query.subquery(Long.class);
                Root<FieldData> fd = sq.from(FieldData.class);

                Predicate link =
                        cb.equal(fd.get("submission"), root);

                Predicate fieldMatch =
                        cb.equal(fd.get("fieldId"), filter.getFieldId());

                Predicate valueMatch =
                        buildValuePredicate(cb, fd, filter);

                sq.select(cb.literal(1L))
                        .where(cb.and(link, fieldMatch, valueMatch));

                predicates.add(cb.exists(sq));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private static Predicate buildValuePredicate(
            CriteriaBuilder cb,
            Root<FieldData> fd,
            SubmissionFilter f) {

        Path<?> path = switch (f.getFieldType()) {
            case TEXT -> fd.get("textValue");
            case NUMBER -> fd.get("numberValue");
            case CHECKBOX -> fd.get("booleanValue");
            case DATE -> fd.get("dateValue");
            case DATE_TIME -> fd.get("dateTimeValue");
            default ->  null;
        };

        Object v = f.getValue();

        return switch (f.getOperator()) {
            case EQ -> cb.equal(path, v);
            case NE -> cb.notEqual(path, v);
            case GT -> cb.greaterThan(path.as(Comparable.class), (Comparable) v);
            case GTE -> cb.greaterThanOrEqualTo(path.as(Comparable.class), (Comparable) v);
            case LT -> cb.lessThan(path.as(Comparable.class), (Comparable) v);
            case LTE -> cb.lessThanOrEqualTo(path.as(Comparable.class), (Comparable) v);
            case LIKE -> cb.like(path.as(String.class), "%" + v + "%");
            case IS_NULL -> cb.isNull(path);
            default -> throw new IllegalArgumentException("Unsupported op");
        };
    }
//    Specification<Submission> spec =
//            SubmissionSpecificationBuilder.build(formId, filters);
//
//    Page<Submission> page =
//            submissionRepository.findAll(spec, pageable);
//
//    filters = List.of(
//            SubmissionFilter.builder()
//            .fieldId(NAME_FIELD)
//        .fieldType(FieldType.TEXT)
//        .operator(Operator.LIKE)
//        .value("ritik")
//        .build(),
//
//    SubmissionFilter.builder()
//            .fieldId(AGE_FIELD)
//        .fieldType(FieldType.NUMBER)
//        .operator(Operator.GT)
//        .value(new BigDecimal("18"))
//            .build()
//);

//    MUST HAVE INDEXES
//(field_id, text_value)
//            (field_id, number_value)
//            (field_id, date_value)
//            (submission_id, field_id)


}
