package com.revify.monolith.search;

import com.revify.monolith.items.model.item.Item;
import com.revify.monolith.shoplift.model.Shoplift;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final MongoTemplate mongoTemplate;

    //im tired so its search for now 1231234 but ok
    public List<Object> search(String search) {
        Query itemQuery = new Query();
        Query shopliftQuery = new Query();

        Pattern pattern = Pattern.compile("^:(\\w+):([A-Z]{2,3})");
        Matcher matcher = pattern.matcher(search);
        if (matcher.matches()) {
            String item = matcher.group();
            String countrySelector = matcher.group(1);

            String fuzzyPattern = item.replaceAll(".", "[$0]?");
            itemQuery.addCriteria(
                    Criteria.where("itemDescription.title")
                            .regex(fuzzyPattern, "i"));
            itemQuery.addCriteria(
                    Criteria.where("itemDescription.destination.countryCode").is(countrySelector));

            itemQuery.limit(3);
            return mongoTemplate.find(itemQuery, Item.class).stream().map(Item::getId).collect(Collectors.toList());
        } else {
            String fuzzyPattern = search.replaceAll(".", "[$0]?");
            shopliftQuery.addCriteria(
                    Criteria.where("title")
                            .regex(fuzzyPattern, "i"));

            itemQuery.addCriteria(
                            Criteria.where("itemDescription.title")
                                    .regex(fuzzyPattern, "i"))
                    .limit(3);
        }
        List<Object> res = new ArrayList<>();
        res.addAll(mongoTemplate.find(shopliftQuery.limit(3), Shoplift.class).stream().map(Shoplift::getId).toList());
        res.addAll(mongoTemplate.find(itemQuery.limit(3), Item.class).stream().map(Item::getId).toList());


        return res;
    }
}
