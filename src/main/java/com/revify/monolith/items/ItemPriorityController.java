package com.revify.monolith.items;

import com.revify.monolith.commons.items.Duration;
import com.revify.monolith.items.service.item.ItemService;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/priority")
@RequiredArgsConstructor
public class ItemPriorityController {

    private final ItemService itemService;

    //count duration for item as PLUS, if duration is 7DAyS (default) and items duration is less than 7days so expand it
    @PostMapping("/create")
    public void attachPriority(@RequestParam("duration") Duration duration, @RequestParam("itemId") ObjectId itemId) {
        itemService.attachPremium(duration, itemId);
    }
}
