package com.revify.monolith.items;

import com.revify.monolith.items.model.item.composite.CompositeItemRequestLink;
import com.revify.monolith.items.service.composite.CompositeItemConnectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/item/composite-connect")

@RequiredArgsConstructor
public class ItemCompositeConnectionController {

    private final CompositeItemConnectionService compositeItemConnectionService;

    @PostMapping("/invitation")
    public ResponseEntity<?> sendInvitation(@RequestBody Invitation invitation) {
        if (invitation.invitations().isEmpty()) {
            return ResponseEntity.badRequest().body("Users must be selected");
        }
        //create user invitations
        compositeItemConnectionService.sendInvitations(invitation.invitations, invitation.itemId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/invitation/link")
    public ResponseEntity<?> deleteInvitationsForItem(@RequestParam("itemId") String itemId) {
        compositeItemConnectionService.deleteInvitationLinksForItem(itemId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/invitation/link/{code}")
    public ResponseEntity<?> connectViaLink(@PathVariable String code) {
        compositeItemConnectionService.connectViaLink(code);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/invitation/create-link")
    public ResponseEntity<CompositeItemRequestLink.CompositeItemRequestLinkDTO> createLink(@RequestParam String itemId) {
        CompositeItemRequestLink invitationLink = compositeItemConnectionService.createInvitationLink(null, itemId);
        return ResponseEntity.ok(CompositeItemRequestLink.CompositeItemRequestLinkDTO.from(invitationLink));
    }

    public record Invitation(List<Long> invitations, String itemId) { }
}
