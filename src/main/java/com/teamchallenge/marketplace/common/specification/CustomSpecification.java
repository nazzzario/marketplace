package com.teamchallenge.marketplace.common.specification;

import com.teamchallenge.marketplace.common.entity.BaseEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.Optional;


@Component
public class CustomSpecification {

    public static Specification<? extends BaseEntity> searchLikeString(String propertyName, String name) {
        return Optional.ofNullable(name)
                .filter(StringUtils::isNoneBlank)
                .map(n -> (Specification<? extends BaseEntity>)
                        (r, rq, cb) -> cb.like(cb.lower(r.get(propertyName)), "%" + n.toLowerCase().trim() + "%"))
                .orElse(null);
    }

    public static Specification<? extends BaseEntity> fieldEqual(String pName, Object fName) {
        return Optional.ofNullable(fName)
                .map(s -> (Specification<? extends BaseEntity>) (r, rq, cb) -> cb.equal(r.get(pName), fName))
                .orElse(null);
    }

}

