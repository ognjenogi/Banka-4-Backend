package rs.banka4.bank_service.utils.specification;

import org.springframework.data.jpa.domain.Specification;
import rs.banka4.bank_service.domain.user.employee.db.Employee;
import rs.banka4.rafeisen.common.security.Privilege;

public class EmployeeSpecification {

    public static Specification<Employee> hasFirstName(String firstName) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(
            criteriaBuilder.lower(root.get("firstName")),
            "%" + firstName.toLowerCase() + "%"
        );
    }

    public static Specification<Employee> hasLastName(String lastName) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(
            criteriaBuilder.lower(root.get("lastName")),
            "%" + lastName.toLowerCase() + "%"
        );
    }

    public static Specification<Employee> hasEmail(String email) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(
            criteriaBuilder.lower(root.get("email")),
            "%" + email.toLowerCase() + "%"
        );
    }

    public static Specification<Employee> hasPosition(String position) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(
            criteriaBuilder.lower(root.get("position")),
            "%" + position.toLowerCase() + "%"
        );
    }

    public static Specification<Employee> hasPrivilege(Privilege privilege) {
        return (root, query, cb) -> cb.notEqual(
            cb.function(
                "bitand",
                Long.class,
                root.get("permissionBits"),
                cb.literal(privilege.bit())
            ),
            cb.literal(0L)
        );
    }

}
