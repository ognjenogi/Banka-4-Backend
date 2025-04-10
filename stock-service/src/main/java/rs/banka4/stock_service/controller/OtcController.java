package rs.banka4.stock_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rs.banka4.stock_service.controller.docs.OtcApiDocumentation;
import rs.banka4.stock_service.domain.listing.dtos.ListingInfoDto;

@RestController
@RequestMapping("/otc")
@RequiredArgsConstructor
public class OtcController implements OtcApiDocumentation {
    @Override
    @GetMapping("/me")
    public Page<ListingInfoDto> getMyRequests(int page, int size, Authentication auth) {
        return null;
    }

    @Override
    @GetMapping("/me/unread")
    public Page<ListingInfoDto> getMyRequestsUnread(int page, int size, Authentication auth) {
        return null;
    }
}
