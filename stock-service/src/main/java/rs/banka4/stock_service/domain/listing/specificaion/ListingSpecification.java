package rs.banka4.stock_service.domain.listing.specificaion;

import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import rs.banka4.stock_service.domain.exchanges.db.Exchange_;
import rs.banka4.stock_service.domain.listing.db.Listing;
import rs.banka4.stock_service.domain.listing.db.Listing_;
import rs.banka4.stock_service.domain.listing.dtos.ListingFilterDto;
import rs.banka4.stock_service.domain.listing.dtos.SecurityType;
import rs.banka4.stock_service.domain.options.db.Asset_;
import rs.banka4.stock_service.domain.orders.db.Order;
import rs.banka4.stock_service.domain.orders.db.Order_;
import rs.banka4.stock_service.domain.security.Security_;
import rs.banka4.stock_service.domain.security.future.db.Future;
import rs.banka4.stock_service.domain.security.future.db.Future_;
import rs.banka4.stock_service.domain.security.stock.db.Stock;

public class ListingSpecification {
    public static Specification<Listing> getSpecification(
        ListingFilterDto filter,
        boolean isClient
    ) {
        return isLatest().and((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            var secJoin = root.join(Listing_.security);
            var exchangesJoin = root.join(Listing_.exchange);
            if (isClient) {
                if (
                    filter.getSecurityType() != null
                        && List.of(SecurityType.FUTURE, SecurityType.STOCK)
                            .contains(filter.getSecurityType())
                ) {
                    predicates.add(
                        cb.equal(
                            secJoin.type(),
                            filter.getSecurityType()
                                .getTypeClass()
                        )
                    );
                } else {
                    var p = cb.equal(secJoin.type(), cb.literal(Future.class));
                    var p2 = cb.equal(secJoin.type(), cb.literal(Stock.class));
                    predicates.add(cb.or(p, p2));
                }

            } else {
                if (filter.getSecurityType() != null) {
                    predicates.add(
                        cb.equal(
                            secJoin.type(),
                            filter.getSecurityType()
                                .getTypeClass()
                        )
                    );
                }
            }
            if (
                filter.getSearchName() != null
                    && !filter.getSearchName()
                        .isBlank()
            ) {
                predicates.add(
                    cb.like(
                        cb.lower(secJoin.get(Security_.name)),
                        "%"
                            + filter.getSearchName()
                                .toLowerCase()
                            + "%"
                    )
                );
            }
            if (
                filter.getSearchTicker() != null
                    && !filter.getSearchTicker()
                        .isBlank()
            ) {
                predicates.add(
                    cb.like(
                        cb.lower(secJoin.get(Security_.ticker)),
                        "%"
                            + filter.getSearchTicker()
                                .toLowerCase()
                            + "%"
                    )
                );
            }

            if (
                filter.getExchangePrefix() != null
                    && !filter.getExchangePrefix()
                        .isBlank()
            ) {
                predicates.add(
                    cb.like(
                        cb.lower(exchangesJoin.get(Exchange_.exchangeName)),
                        filter.getExchangePrefix()
                            .toLowerCase()
                            + "%"
                    )
                );
            }
            if (filter.getAskMin() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get(Listing_.ask), filter.getAskMin()));
            }
            if (filter.getAskMax() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get(Listing_.ask), filter.getAskMax()));
            }
            if (filter.getBidMin() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get(Listing_.bid), filter.getBidMin()));
            }
            if (filter.getBidMax() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get(Listing_.bid), filter.getBidMax()));
            }
            if (SecurityType.FUTURE.equals(filter.getSecurityType())) {
                if (
                    filter.getSettlementDateFrom() != null || filter.getSettlementDateTo() != null
                ) {
                    if (
                        filter.getSettlementDateFrom() != null
                            && filter.getSettlementDateTo() != null
                    ) {
                        predicates.add(
                            cb.between(
                                secJoin.get(Future_.SETTLEMENT_DATE),
                                filter.getSettlementDateFrom(),
                                filter.getSettlementDateTo()
                            )
                        );
                    } else if (filter.getSettlementDateFrom() != null) {
                        predicates.add(
                            cb.greaterThanOrEqualTo(
                                secJoin.get(Future_.SETTLEMENT_DATE),
                                filter.getSettlementDateFrom()
                            )
                        );
                    } else {
                        predicates.add(
                            cb.lessThanOrEqualTo(
                                secJoin.get(Future_.SETTLEMENT_DATE),
                                filter.getSettlementDateTo()
                            )
                        );
                    }
                }
            }
            OffsetDateTime now = OffsetDateTime.now();
            OffsetDateTime start = now.truncatedTo(ChronoUnit.DAYS);
            OffsetDateTime end = start.plusDays(1);

            Subquery<Long> volumeSubquery = query.subquery(Long.class);
            Root<Order> orderRoot = volumeSubquery.from(Order.class);
            volumeSubquery.select(cb.count(orderRoot));
            volumeSubquery.where(
                cb.equal(
                    orderRoot.get(Order_.asset)
                        .get(Asset_.id),
                    root.get(Listing_.security)
                        .get(Security_.id)
                ),
                cb.between(orderRoot.get(Order_.createdAt), start, end)
            );

            if (filter.getVolumeMin() != null || filter.getVolumeMax() != null) {

                if (filter.getVolumeMin() != null) {
                    predicates.add(cb.greaterThanOrEqualTo(volumeSubquery, filter.getVolumeMin()));
                }
                if (filter.getVolumeMax() != null) {
                    predicates.add(cb.lessThanOrEqualTo(volumeSubquery, filter.getVolumeMax()));
                }
            }
            if (filter.getSortBy() != null && filter.getSortDirection() != null) {
                switch (filter.getSortBy()) {
                case PRICE:
                    if (
                        filter.getSortDirection()
                            .equals(ListingFilterDto.SortDirection.ASC)
                    ) {
                        query.orderBy(cb.asc(root.get(Listing_.ask)));
                    } else {
                        query.orderBy(cb.desc(root.get(Listing_.ask)));
                    }
                    break;
                case VOLUME:
                    if (
                        filter.getSortDirection()
                            .equals(ListingFilterDto.SortDirection.ASC)
                    ) {
                        query.orderBy(cb.asc(volumeSubquery));
                    } else {
                        query.orderBy(cb.desc(volumeSubquery));
                    }
                    break;
                default:
                    break;
                }
            }


            return cb.and(predicates.toArray(new Predicate[0]));
        });
    }

    /**
     * For a given listing, accept it only if it is the latest for its security
     */
    static Specification<Listing> isLatest() {
        return ((root, query, cb) -> {
            final var subquery = query.subquery(OffsetDateTime.class);
            final var subRoot = subquery.from(Listing.class);

            subquery.select(cb.greatest(subRoot.get(Listing_.lastRefresh)))
                .where(
                    cb.equal(
                        subRoot.get(Listing_.security)
                            .get(Security_.id),
                        root.get(Listing_.security)
                            .get(Security_.id)
                    )
                );

            return cb.equal(root.get(Listing_.lastRefresh), subquery);
        });
    }

}
