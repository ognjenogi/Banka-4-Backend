package rs.banka4.bank_service.domain.listing.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ListingApiDto(@JsonProperty("Global Quote") GlobalQuoteDto globalQuoteDto) {
}
