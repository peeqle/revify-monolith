package com.revify.monolith.commons.items;

import lombok.Getter;

@Getter
public enum Category {
    FOOD_AND_BEVERAGE("foodAndBeverage"),
    ELECTRONICS("electronics"),
    FASHION("fashion"),
    HEALTH_AND_BEAUTY("healthAndBeauty"),
    HOME_AND_LIVING("homeAndLiving"),
    BOOKS_AND_MEDIA("booksAndMedia"),
    SPORTS_AND_OUTDOORS("sportsAndOutdoors"),
    GROCERIES("groceries"),
    BABY_AND_KIDS("babyAndKids"),
    PET_SUPPLIES("petSupplies"),
    AUTOMOTIVE("automotive"),
    OFFICE_SUPPLIES("officeSupplies"),
    HOBBIES_AND_CRAFTS("hobbiesAndCrafts"),
    TOOLS_AND_HARDWARE("toolsAndHardware"),
    TRAVEL_AND_LUGGAGE("travelAndLuggage");

    private final String camelCaseName;

    Category(String camelCaseName) {
        this.camelCaseName = camelCaseName;
    }
}