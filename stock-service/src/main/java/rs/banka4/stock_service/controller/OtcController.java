package rs.banka4.stock_service.controller;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import retrofit2.Retrofit;
import rs.banka4.rafeisen.common.security.AuthenticatedBankUserAuthentication;
import rs.banka4.stock_service.config.clients.UserServiceClient;
import rs.banka4.stock_service.controller.docs.OtcApiDocumentation;
import rs.banka4.stock_service.domain.trading.db.OtcMapper;
import rs.banka4.stock_service.domain.trading.db.dtos.OtcRequestDto;
import rs.banka4.stock_service.service.abstraction.OtcRequestService;

@RestController
@RequestMapping("/otc")
@RequiredArgsConstructor
public class OtcController implements OtcApiDocumentation {
    private final OtcRequestService otcRequestService;
    private final Retrofit userServiceRetrofit;
    private final OtcMapper otcMapper;

    @Override
    @GetMapping("/me")
    public ResponseEntity<Page<OtcRequestDto>> getMyRequests(
        int page,
        int size,
        Authentication auth
    ) {
        return getRequestHelper(page, size, auth, false);
    }

    @Override
    @GetMapping("/me/unread")
    public ResponseEntity<Page<OtcRequestDto>> getMyRequestsUnread(
        int page,
        int size,
        Authentication auth
    ) {
        return getRequestHelper(page, size, auth, true);
    }

    @Override
    @PatchMapping("/reject/{requestId}")
    public ResponseEntity<Void> rejectOtcRequest(@PathVariable UUID requestId) {
        otcRequestService.rejectOtc(requestId);
        return ResponseEntity.ok()
            .build();
    }

    private ResponseEntity<Page<OtcRequestDto>> getRequestHelper(
        int page,
        int size,
        Authentication auth,
        boolean unread
    ) {
        UserServiceClient userServiceClient = userServiceRetrofit.create(UserServiceClient.class);
        final var ourAuth = (AuthenticatedBankUserAuthentication) auth;
        var myId =
            ourAuth.getPrincipal()
                .userId();
        String token = "Bearer " + auth.getCredentials();
        var requests =
            unread
                ? otcRequestService.getMyRequestsUnread(PageRequest.of(page, size), myId)
                : otcRequestService.getMyRequests(PageRequest.of(page, size), myId);

        Page<OtcRequestDto> dtoPage = requests.map(it -> {
            try {
                var resFor =
                    userServiceClient.getUserInfo(
                        UUID.fromString(
                            it.getMadeFor()
                                .userId()
                        ),
                        token
                    )
                        .execute();
                var resBy =
                    userServiceClient.getUserInfo(
                        UUID.fromString(
                            it.getMadeBy()
                                .userId()
                        ),
                        token
                    )
                        .execute();
                var resMod =
                    userServiceClient.getUserInfo(
                        UUID.fromString(
                            it.getModifiedBy()
                                .userId()
                        ),
                        token
                    )
                        .execute();

                if (
                    !resFor.isSuccessful()
                        || resFor.body() == null
                        || !resBy.isSuccessful()
                        || resBy.body() == null
                        || !resMod.isSuccessful()
                        || resMod.body() == null
                ) {
                    throw new RuntimeException("User info service error");
                }

                String madeByStr =
                    resBy.body()
                        .email();
                String madeForStr =
                    resFor.body()
                        .email();
                String modifiedByStr =
                    resMod.body()
                        .email();

                return otcMapper.toOtcRequestDto(it, madeByStr, madeForStr, modifiedByStr);
            } catch (Exception e) {
                throw new RuntimeException("Error mapping OTC request", e);
            }
        });

        return ResponseEntity.ok(dtoPage);
    }


}
