package com.revify.monolith.shoplift;

import com.revify.monolith.commons.geolocation.CountryCode;
import com.revify.monolith.commons.items.Category;
import com.revify.monolith.shoplift.model.Shop;
import com.revify.monolith.shoplift.repo.ShopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ShopDataInitializer {

    private final ShopRepository shopRepository;

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        if (!shopRepository.existsByName("IKEA")) {
            Shop shop = Shop.builder()
                    .name("IKEA")
                    .URL("www.ikea.com")
                    .countries(Arrays.stream(new String[]{
                            "AT", "BE", "BG", "HR", "CY", "CY", "CZ", "DK", "EE", "FI",
                            "FR", "DE", "GR", "HU", "IS", "IE", "IT", "LV", "LT", "NL",
                            "NO", "PL", "PT", "RO", "RS", "SK", "SI", "ES", "SE", "CH",
                            "TR", "UA", "GB", "BH", "CN", "HK", "HK", "IN", "ID", "IL",
                            "JP", "JO", "KW", "MO", "MY", "OM", "PH", "QA", "SA", "SG",
                            "KR", "TW", "TH", "AE", "CA", "DO", "MX", "PR", "US", "CL",
                            "CO", "EG", "MA", "AU"
                    }).map(CountryCode::getCountryCode).collect(Collectors.toSet()))
                    .categories(new HashSet<>(Arrays.asList(Category.FASHION, Category.OFFICE_SUPPLIES, Category.HOME_AND_LIVING)))
                    .build();
            shopRepository.save(shop);
        }
        if (!shopRepository.existsByName("Apple")) {
            Shop shop = Shop.builder()
                    .name("Apple")
                    .URL("www.apple.com")
                    .countries(Arrays.stream(new String[]{
                            "US", "CA", "MX", "BR", "CL", "CO", "PE", "PR", "AR",
                            "AT", "BE", "CZ", "DK", "FI", "FR", "DE", "GR", "HU", "IE", "IT",
                            "LU", "NL", "NO", "PL", "PT", "ES", "SE", "CH", "TR", "GB",
                            "AU", "NZ", "CN", "HK", "JP", "KR", "SG", "TH", "TW", "MY", "ID", "IN", "PH", "VN",
                            "AE", "IL", "QA", "KW", "SA", "BH", "OM", "JO",
                            "ZA"
                    }).map(CountryCode::getCountryCode).collect(Collectors.toSet()))
                    .categories(new HashSet<>(Arrays.asList(Category.ELECTRONICS, Category.FASHION)))
                    .build();
            shopRepository.save(shop);
        }

        if (!shopRepository.existsByName("RTV Euro AGD")) {
            Shop shop = Shop.builder()
                    .name("RTV Euro AGD")
                    .URL("www.euro.com.pl")
                    .countries(Arrays.stream(new String[]{"PL"}).map(CountryCode::getCountryCode).collect(Collectors.toSet()))
                    .categories(new HashSet<>(Arrays.asList(Category.ELECTRONICS, Category.HOME_AND_LIVING, Category.BOOKS_AND_MEDIA)))
                    .build();
            shopRepository.save(shop);
        }

        if (!shopRepository.existsByName("MediaMarkt")) {
            Shop shop = Shop.builder()
                    .name("MediaMarkt")
                    .URL("www.mediamarkt.pl")
                    .countries(Arrays.stream(new String[]{
                            "AT", "BE", "DE", "GR", "HU", "IT", "LU", "NL", "NO",
                            "PL", "PT", "ES", "SE", "CH", "TR"
                    }).map(CountryCode::getCountryCode).collect(Collectors.toSet()))
                    .categories(new HashSet<>(Arrays.asList(Category.ELECTRONICS, Category.HOME_AND_LIVING, Category.BOOKS_AND_MEDIA)))
                    .build();
            shopRepository.save(shop);
        }

        if (!shopRepository.existsByName("Google")) {
            Shop shop = Shop.builder()
                    .name("Google")
                    .URL("store.google.com")
                    .countries(Arrays.stream(new String[]{
                            "US", "CA", "MX", "BR",
                            "GB", "DE", "FR", "ES", "IT", "IE", "NL", "BE", "DK", "SE", "FI", "NO", "AT", "CH",
                            "AU", "JP", "TW", "IN", "KR", "SG"
                    }).map(CountryCode::getCountryCode).collect(Collectors.toSet()))
                    .categories(new HashSet<>(Arrays.asList(Category.ELECTRONICS, Category.BOOKS_AND_MEDIA)))
                    .build();
            shopRepository.save(shop);
        }
        if (!shopRepository.existsByName("Amazon")) {
            Shop shop = Shop.builder()
                    .name("Amazon")
                    .URL("www.amazon.com")
                    .countries(Arrays.stream(new String[]{
                            "US", "CA", "MX", "BR",
                            "DE", "ES", "FR", "GB", "IT", "NL", "SE", "PL", "BE",
                            "AU", "CN", "IN", "JP", "SG", "AE", "TR", "SA",
                            "EG"
                    }).map(CountryCode::getCountryCode).collect(Collectors.toSet()))
                    .categories(new HashSet<>(Arrays.asList(Category.ELECTRONICS, Category.BOOKS_AND_MEDIA, Category.FASHION,
                            Category.HOME_AND_LIVING, Category.HEALTH_AND_BEAUTY, Category.BABY_AND_KIDS, Category.GROCERIES)))
                    .build();
            shopRepository.save(shop);
        }

        if (!shopRepository.existsByName("Zara")) {
            Shop shop = Shop.builder()
                    .name("Zara")
                    .URL("www.zara.com")
                    .countries(Arrays.stream(new String[]{
                            "AL", "AD", "AR", "AM", "AU", "AT", "AZ", "BH", "BY", "BE", "BO", "BA", "BR", "BG", "CA", "CL", "CO", "CR", "HR", "CY", "CZ", "DK", "DO", "EC", "EG", "SV", "EE", "FI", "FR", "GE", "DE", "GR", "GT", "HN", "HK", "HU", "IS", "IN", "ID", "IQ", "IE", "IL", "IT", "JP", "JO", "KZ", "KW", "LV", "LB", "LT", "LU", "MO", "MY", "MT", "MX", "MD", "MC", "ME", "MA", "MZ", "NL", "NZ", "NI", "MK", "NO", "OM", "PA", "PY", "PE", "PH", "PL", "PT", "QA", "RO", "RU", "SM", "SA", "SN", "RS", "SG", "SK", "SI", "ZA", "KR", "ES", "LK", "SE", "CH", "TW", "TH", "TN", "TR", "UA", "AE", "GB", "US", "UY", "UZ", "VE", "VN", "XK"
                    }).map(CountryCode::getCountryCode).collect(Collectors.toSet()))
                    .categories(new HashSet<>(Arrays.asList(Category.FASHION)))
                    .build();
            shopRepository.save(shop);
        }

        if (!shopRepository.existsByName("H&M")) {
            Shop shop = Shop.builder()
                    .name("H&M")
                    .URL("www2.hm.com")
                    .countries(Arrays.stream(new String[]{
                            "AL", "AD", "AR", "AU", "AT", "BH", "BE", "BG", "KH", "CA", "CL", "CN", "CO", "HR", "CY", "CZ", "DK", "DO", "EC", "EG", "SV", "EE", "FI", "FR", "DE", "GR", "GT", "HK", "HU", "IS", "IN", "ID", "IE", "IT", "JP", "JO", "KZ", "KW", "LV", "LB", "LT", "LU", "MO", "MY", "MT", "MX", "MN", "ME", "MA", "NL", "NZ", "NO", "OM", "PK", "PA", "PE", "PH", "PL", "PT", "QA", "RO", "RU", "SA", "SG", "SK", "SI", "ZA", "KR", "ES", "LK", "SE", "CH", "TW", "TH", "TR", "UA", "AE", "GB", "US", "UY", "VN"
                    }).map(CountryCode::getCountryCode).collect(Collectors.toSet()))
                    .categories(new HashSet<>(Arrays.asList(Category.FASHION)))
                    .build();
            shopRepository.save(shop);
        }

        if (!shopRepository.existsByName("Lidl")) {
            Shop shop = Shop.builder()
                    .name("Lidl")
                    .URL("www.lidl.com")
                    .countries(Arrays.stream(new String[]{
                            "AT", "BE", "BG", "HR", "CY", "CZ", "DK", "EE", "FI", "FR", "DE", "GR", "HU", "IE", "IT",
                            "LV", "LT", "LU", "MT", "NL", "PL", "PT", "RO", "RS", "SK", "SI", "ES", "SE", "CH", "GB",
                            "US",
                            "AD",
                            "LI",
                            "BA"
                    }).map(CountryCode::getCountryCode).collect(Collectors.toSet()))
                    .categories(new HashSet<>(Arrays.asList(Category.GROCERIES, Category.HOME_AND_LIVING, Category.ELECTRONICS, Category.FASHION, Category.TOOLS_AND_HARDWARE, Category.SPORTS_AND_OUTDOORS)))
                    .build();
            shopRepository.save(shop);
        }

        if (!shopRepository.existsByName("Aldi")) {
            Shop shop = Shop.builder()
                    .name("Aldi")
                    .URL("www.aldi.com")
                    .countries(Arrays.stream(new String[]{
                            "AU", "AT", "BE", "CN", "DK", "FR", "DE", "HU", "IE", "IT",
                            "LU", "NL", "PL", "PT", "SI", "ES", "CH", "GB", "US"
                    }).map(CountryCode::getCountryCode).collect(Collectors.toSet()))
                    .categories(new HashSet<>(Arrays.asList(Category.GROCERIES, Category.HOME_AND_LIVING, Category.ELECTRONICS, Category.TOOLS_AND_HARDWARE)))
                    .build();
            shopRepository.save(shop);
        }

        if (!shopRepository.existsByName("Carrefour")) {
            Shop shop = Shop.builder()
                    .name("Carrefour")
                    .URL("www.carrefour.com")
                    .countries(Arrays.stream(new String[]{
                            "FR", "ES", "IT", "BE", "RO", "PL", "AR", "BR", "CN", "AE", "JO", "TW",
                            "EG", "MA", "TN", "KE", "CI", "GA", "CM", "SN", "BJ", "BF", "MR", "NG"
                    }).map(CountryCode::getCountryCode).collect(Collectors.toSet()))
                    .categories(new HashSet<>(Arrays.asList(Category.GROCERIES, Category.HOME_AND_LIVING, Category.ELECTRONICS, Category.FASHION, Category.HEALTH_AND_BEAUTY, Category.BABY_AND_KIDS, Category.PET_SUPPLIES, Category.SPORTS_AND_OUTDOORS)))
                    .build();
            shopRepository.save(shop);
        }

        if (!shopRepository.existsByName("Tesco")) {
            Shop shop = Shop.builder()
                    .name("Tesco")
                    .URL("www.tesco.com")
                    .countries(Arrays.stream(new String[]{
                            "GB", "IE", "CZ", "HU", "SK"
                    }).map(CountryCode::getCountryCode).collect(Collectors.toSet()))
                    .categories(new HashSet<>(Arrays.asList(Category.GROCERIES, Category.HOME_AND_LIVING, Category.ELECTRONICS, Category.FASHION, Category.HEALTH_AND_BEAUTY, Category.BABY_AND_KIDS, Category.PET_SUPPLIES, Category.SPORTS_AND_OUTDOORS, Category.BOOKS_AND_MEDIA)))
                    .build();
            shopRepository.save(shop);
        }

        if (!shopRepository.existsByName("Leroy Merlin")) {
            Shop shop = Shop.builder()
                    .name("Leroy Merlin")
                    .URL("www.leroymerlin.com")
                    .countries(Arrays.stream(new String[]{
                            "FR", "PL", "ES", "IT", "PT", "GR", "CY", "RO", "UA", "RU",
                            "BR", "ZA", "CN", "KZ"
                    }).map(CountryCode::getCountryCode).collect(Collectors.toSet()))
                    .categories(new HashSet<>(Arrays.asList(Category.HOME_AND_LIVING, Category.TOOLS_AND_HARDWARE)))
                    .build();
            shopRepository.save(shop);
        }

        if (!shopRepository.existsByName("Decathlon")) {
            Shop shop = Shop.builder()
                    .name("Decathlon")
                    .URL("www.decathlon.com")
                    .countries(Arrays.stream(new String[]{
                            "AL", "AR", "AU", "AT", "AZ", "BD", "BE", "BA", "BR", "BG", "CA", "CL", "CN", "CO", "HR", "CY", "CZ", "DK", "EC", "EG", "EE", "FI", "FR", "GE", "DE", "GR", "HK", "HU", "IN", "ID", "IE", "IL", "IT", "JP", "JO", "KZ", "XK", "KW", "LV", "LB", "LT", "LU", "MY", "MX", "MD", "MC", "MA", "NL", "NZ", "NO", "OM", "PK", "PA", "PE", "PH", "PL", "PT", "QA", "RO", "RU", "SA", "SG", "RS", "SK", "SI", "KR", "ES", "SE", "CH", "TW", "TH", "TN", "TR", "UA", "AE", "GB", "US", "UY", "VN"
                    }).map(CountryCode::getCountryCode).collect(Collectors.toSet()))
                    .categories(new HashSet<>(Arrays.asList(Category.SPORTS_AND_OUTDOORS, Category.FASHION)))
                    .build();
            shopRepository.save(shop);
        }

        if (!shopRepository.existsByName("Rossmann")) {
            Shop shop = Shop.builder()
                    .name("Rossmann")
                    .URL("www.rossmann.de")
                    .countries(Arrays.stream(new String[]{
                            "DE", "PL", "HU", "CZ", "AL", "XK", "ES", "TR", "AZ"
                    }).map(CountryCode::getCountryCode).collect(Collectors.toSet()))
                    .categories(new HashSet<>(Arrays.asList(Category.HEALTH_AND_BEAUTY, Category.HOME_AND_LIVING, Category.BABY_AND_KIDS, Category.PET_SUPPLIES)))
                    .build();
            shopRepository.save(shop);
        }

        if (!shopRepository.existsByName("dm-drogerie markt")) {
            Shop shop = Shop.builder()
                    .name("dm-drogerie markt")
                    .URL("www.dm.de")
                    .countries(Arrays.stream(new String[]{
                            "AT", "BA", "BG", "HR", "CZ", "DE", "HU", "IT", "MK", "PL", "RO", "RS", "SK", "SI"
                    }).map(CountryCode::getCountryCode).collect(Collectors.toSet()))
                    .categories(new HashSet<>(Arrays.asList(Category.HEALTH_AND_BEAUTY, Category.HOME_AND_LIVING, Category.BABY_AND_KIDS, Category.PET_SUPPLIES)))
                    .build();
            shopRepository.save(shop);
        }

        if (!shopRepository.existsByName("Zalando")) {
            Shop shop = Shop.builder()
                    .name("Zalando")
                    .URL("www.zalando.com")
                    .countries(Arrays.stream(new String[]{
                            "AT", "BE", "BG", "CH", "CZ", "DE", "DK", "ES", "EE", "FI", "FR", "GR", "IE", "IT",
                            "LV", "LT", "LU", "NL", "NO", "PL", "PT", "RO", "SE", "SI", "SK", "GB"
                    }).map(CountryCode::getCountryCode).collect(Collectors.toSet()))
                    .categories(new HashSet<>(Arrays.asList(Category.FASHION)))
                    .build();
            shopRepository.save(shop);
        }

        if (!shopRepository.existsByName("ASOS")) {
            Shop shop = Shop.builder()
                    .name("ASOS")
                    .URL("www.asos.com")
                    .countries(Arrays.stream(new String[]{
                            "AL", "AT", "AU", "BE", "BG", "CA", "CH", "CN", "CY", "CZ", "DE", "DK", "EE", "ES",
                            "FI", "FR", "GB", "GR", "HK", "HR", "HU", "IE", "IL", "IN", "IS", "IT", "JP", "KR",
                            "KW", "LV", "LT", "LU", "MY", "MT", "MX", "NL", "NO", "NZ", "PH", "PL", "PT", "QA",
                            "RO", "RU", "SA", "SE", "SG", "SI", "SK", "TH", "TR", "UA", "US", "ZA"
                    }).map(CountryCode::getCountryCode).collect(Collectors.toSet()))
                    .categories(new HashSet<>(Arrays.asList(Category.FASHION)))
                    .build();
            shopRepository.save(shop);
        }

        if (!shopRepository.existsByName("Autodoc")) {
            Shop shop = Shop.builder()
                    .name("Autodoc")
                    .URL("www.autodoc.eu")
                    .countries(Arrays.stream(new String[]{
                            "AT", "BE", "BG", "HR", "CZ", "DK", "EE", "FI", "FR", "DE", "GR", "HU", "IE", "IT",
                            "LV", "LT", "LU", "NL", "NO", "PL", "PT", "RO", "RS", "SK", "SI", "ES", "CH", "SE", "GB",
                            "UA"
                    }).map(CountryCode::getCountryCode).collect(Collectors.toSet()))
                    .categories(new HashSet<>(Arrays.asList(Category.AUTOMOTIVE, Category.TOOLS_AND_HARDWARE)))
                    .build();
            shopRepository.save(shop);
        }

        if (!shopRepository.existsByName("Mister-Auto")) {
            Shop shop = Shop.builder()
                    .name("Mister-Auto")
                    .URL("www.mister-auto.com")
                    .countries(Arrays.stream(new String[]{
                            "AT", "BE", "DK", "FI", "FR", "DE", "GR", "IE", "IT", "NL", "NO", "PL", "PT", "ES", "SE", "CH", "GB",
                            "LU"
                    }).map(CountryCode::getCountryCode).collect(Collectors.toSet()))
                    .categories(new HashSet<>(Arrays.asList(Category.AUTOMOTIVE, Category.TOOLS_AND_HARDWARE)))
                    .build();
            shopRepository.save(shop);
        }

        if (!shopRepository.existsByName("AutoScout24")) {
            Shop shop = Shop.builder()
                    .name("AutoScout24")
                    .URL("www.autoscout24.com")
                    .countries(Arrays.stream(new String[]{
                            "AT", "BE", "DE", "DK", "FR", "IT", "LU", "NL", "NO", "PL", "ES", "SE"
                    }).map(CountryCode::getCountryCode).collect(Collectors.toSet()))
                    .categories(new HashSet<>(Arrays.asList(Category.AUTOMOTIVE)))
                    .build();
            shopRepository.save(shop);
        }

        if (!shopRepository.existsByName("Mobile.de")) {
            Shop shop = Shop.builder()
                    .name("Mobile.de")
                    .URL("www.mobile.de")
                    .countries(new HashSet<>(Arrays.asList(CountryCode.getCountryCode("DE"))))
                    .categories(new HashSet<>(Arrays.asList(Category.AUTOMOTIVE)))
                    .build();
            shopRepository.save(shop);
        }

        if (!shopRepository.existsByName("OLX Motors")) {
            Shop shop = Shop.builder()
                    .name("OLX Motors")
                    .URL("www.olx.com")
                    .countries(Arrays.stream(new String[]{
                            "PL", "PT", "RO", "BG", "BA", "UA", "KZ"
                    }).map(CountryCode::getCountryCode).collect(Collectors.toSet()))
                    .categories(new HashSet<>(Arrays.asList(Category.AUTOMOTIVE)))
                    .build();
            shopRepository.save(shop);
        }

        if (!shopRepository.existsByName("eBay Motors")) {
            Shop shop = Shop.builder()
                    .name("eBay Motors")
                    .URL("www.ebay.com/motors")
                    .countries(Arrays.stream(new String[]{
                            "US", "CA", "AU",
                            "DE", "FR", "GB", "IT", "ES", "AT", "BE", "CH", "NL", "IE", "PL", "SE"
                    }).map(CountryCode::getCountryCode).collect(Collectors.toSet()))
                    .categories(new HashSet<>(Arrays.asList(Category.AUTOMOTIVE, Category.TOOLS_AND_HARDWARE)))
                    .build();
            shopRepository.save(shop);
        }

        if (!shopRepository.existsByName("Gloria Jeans")) {
            Shop shop = Shop.builder()
                    .name("Gloria Jeans")
                    .URL("www.gloria-jeans.ru")
                    .countries(Arrays.stream(new String[]{
                            "RU", "BY", "KZ"
                    }).map(CountryCode::getCountryCode).collect(Collectors.toSet()))
                    .categories(new HashSet<>(Arrays.asList(Category.FASHION)))
                    .build();
            shopRepository.save(shop);
        }

        if (!shopRepository.existsByName("Sportmaster")) {
            Shop shop = Shop.builder()
                    .name("Sportmaster")
                    .URL("www.sportmaster.ru")
                    .countries(Arrays.stream(new String[]{
                            "RU", "BY", "KZ", "UA"
                    }).map(CountryCode::getCountryCode).collect(Collectors.toSet()))
                    .categories(new HashSet<>(Arrays.asList(Category.SPORTS_AND_OUTDOORS, Category.FASHION, Category.ELECTRONICS)))
                    .build();
            shopRepository.save(shop);
        }

        if (!shopRepository.existsByName("O'STIN")) {
            Shop shop = Shop.builder()
                    .name("O'STIN")
                    .URL("www.ostin.com")
                    .countries(Arrays.stream(new String[]{
                            "RU", "BY", "KZ", "KG", "MD"
                    }).map(CountryCode::getCountryCode).collect(Collectors.toSet()))
                    .categories(new HashSet<>(Arrays.asList(Category.FASHION)))
                    .build();
            shopRepository.save(shop);
        }

        if (!shopRepository.existsByName("Canyon")) {
            Shop shop = Shop.builder()
                    .name("Canyon")
                    .URL("www.canyon.com")
                    .countries(Arrays.stream(new String[]{
                            "DE", "AT", "BE", "DK", "ES", "FI", "FR", "GB", "IE", "IT", "LU",
                            "NL", "NO", "PT", "SE", "CH", "PL", "CZ", "SK", "HU"
                    }).map(CountryCode::getCountryCode).collect(Collectors.toSet()))
                    .categories(new HashSet<>(Arrays.asList(Category.SPORTS_AND_OUTDOORS, Category.ELECTRONICS)))
                    .build();
            shopRepository.save(shop);
        }

        if (!shopRepository.existsByName("Specialized")) {
            Shop shop = Shop.builder()
                    .name("Specialized")
                    .URL("www.specialized.com")
                    .countries(Arrays.stream(new String[]{
                            "AT", "BE", "CH", "CZ", "DE", "DK", "ES", "FI", "FR", "GB", "HU",
                            "IE", "IT", "NL", "NO", "PL", "PT", "SE", "SK"
                    }).map(CountryCode::getCountryCode).collect(Collectors.toSet()))
                    .categories(new HashSet<>(Arrays.asList(Category.SPORTS_AND_OUTDOORS, Category.ELECTRONICS)))
                    .build();
            shopRepository.save(shop);
        }

        if (!shopRepository.existsByName("Trek")) {
            Shop shop = Shop.builder()
                    .name("Trek")
                    .URL("www.trekbikes.com")
                    .countries(Arrays.stream(new String[]{
                            "AT", "BE", "CH", "CZ", "DE", "DK", "ES", "FI", "FR", "GB", "HU",
                            "IE", "IT", "NL", "NO", "PL", "PT", "SE", "SK"
                    }).map(CountryCode::getCountryCode).collect(Collectors.toSet()))
                    .categories(new HashSet<>(Arrays.asList(Category.SPORTS_AND_OUTDOORS, Category.ELECTRONICS)))
                    .build();
            shopRepository.save(shop);
        }

        if (!shopRepository.existsByName("Rolex")) {
            Shop shop = Shop.builder()
                    .name("Rolex")
                    .URL("www.rolex.com")
                    .countries(Arrays.stream(new String[]{
                            "AT", "BE", "BG", "CH", "CY", "CZ", "DE", "DK", "ES", "FI", "FR", "GB", "GR", "HU",
                            "IE", "IS", "IT", "LT", "LU", "LV", "MD", "ME", "MK", "MT", "NL", "NO", "PL", "PT",
                            "RO", "RS", "SE", "SI", "SK", "TR", "UA"
                    }).map(CountryCode::getCountryCode).collect(Collectors.toSet()))
                    .categories(new HashSet<>(Arrays.asList(Category.FASHION)))
                    .build();
            shopRepository.save(shop);
        }

        if (!shopRepository.existsByName("Swatch")) {
            Shop shop = Shop.builder()
                    .name("Swatch")
                    .URL("www.swatch.com")
                    .countries(Arrays.stream(new String[]{
                            "AL", "AT", "BA", "BE", "BG", "CH", "CY", "CZ", "DE", "DK", "EE", "ES", "FI", "FR",
                            "GB", "GR", "HR", "HU", "IE", "IS", "IT", "LT", "LV", "LU", "MD", "MK", "MT", "NL",
                            "NO", "PL", "PT", "RO", "RS", "SE", "SI", "SK", "TR", "UA"
                    }).map(CountryCode::getCountryCode).collect(Collectors.toSet()))
                    .categories(new HashSet<>(Arrays.asList(Category.FASHION)))
                    .build();
            shopRepository.save(shop);
        }

        if (!shopRepository.existsByName("LEGO")) {
            Shop shop = Shop.builder()
                    .name("LEGO")
                    .URL("www.lego.com")
                    .countries(Arrays.stream(new String[]{
                            "AT", "BE", "CH", "CZ", "DE", "DK", "ES", "FI", "FR", "GB", "HU",
                            "IE", "IT", "NL", "NO", "PL", "PT", "RO", "RU", "SE"
                    }).map(CountryCode::getCountryCode).collect(Collectors.toSet()))
                    .categories(new HashSet<>(Arrays.asList(Category.HOBBIES_AND_CRAFTS)))
                    .build();
            shopRepository.save(shop);
        }

        if (!shopRepository.existsByName("Action")) {
            Shop shop = Shop.builder()
                    .name("Action")
                    .URL("www.action.com")
                    .countries(Arrays.stream(new String[]{
                            "NL", "BE", "FR", "DE", "AT", "LU", "PL", "CZ", "SK", "IT", "ES", "IE", "HU"
                    }).map(CountryCode::getCountryCode).collect(Collectors.toSet()))
                    .categories(new HashSet<>(Arrays.asList(Category.HOME_AND_LIVING, Category.HOBBIES_AND_CRAFTS, Category.OFFICE_SUPPLIES, Category.HEALTH_AND_BEAUTY, Category.PET_SUPPLIES, Category.BABY_AND_KIDS, Category.TOOLS_AND_HARDWARE, Category.SPORTS_AND_OUTDOORS, Category.FOOD_AND_BEVERAGE)))
                    .build();
            shopRepository.save(shop);
        }

        if (!shopRepository.existsByName("Primark")) {
            Shop shop = Shop.builder()
                    .name("Primark")
                    .URL("www.primark.com")
                    .countries(Arrays.stream(new String[]{
                            "AT", "BE", "CZ", "DE", "ES", "FR", "GB", "IE", "IT", "NL", "PL", "PT", "SI", "US"
                    }).map(CountryCode::getCountryCode).collect(Collectors.toSet()))
                    .categories(new HashSet<>(Arrays.asList(Category.FASHION, Category.HOME_AND_LIVING, Category.HEALTH_AND_BEAUTY)))
                    .build();
            shopRepository.save(shop);
        }

        if (!shopRepository.existsByName("Flying Tiger Copenhagen")) {
            Shop shop = Shop.builder()
                    .name("Flying Tiger Copenhagen")
                    .URL("eu.flyingtiger.com")
                    .countries(Arrays.stream(new String[]{
                            "AT", "BE", "CZ", "DE", "DK", "ES", "FI", "FR", "GB", "GR", "HU", "IE", "IT",
                            "LT", "LV", "NL", "NO", "PL", "PT", "SE", "SK", "CH"
                    }).map(CountryCode::getCountryCode).collect(Collectors.toSet()))
                    .categories(new HashSet<>(Arrays.asList(Category.HOME_AND_LIVING, Category.HOBBIES_AND_CRAFTS, Category.OFFICE_SUPPLIES, Category.BABY_AND_KIDS)))
                    .build();
            shopRepository.save(shop);
        }

        if (!shopRepository.existsByName("Müller Drogerie")) {
            Shop shop = Shop.builder()
                    .name("Müller Drogerie")
                    .URL("www.mueller.de")
                    .countries(Arrays.stream(new String[]{
                            "DE", "AT", "CH", "HU", "HR", "SI", "ES"
                    }).map(CountryCode::getCountryCode).collect(Collectors.toSet()))
                    .categories(new HashSet<>(Arrays.asList(Category.HEALTH_AND_BEAUTY, Category.HOME_AND_LIVING, Category.BABY_AND_KIDS, Category.OFFICE_SUPPLIES, Category.TOYS_AND_GAMES, Category.PET_SUPPLIES, Category.HOBBIES_AND_CRAFTS)))
                    .build();
            shopRepository.save(shop);
        }

        if (!shopRepository.existsByName("JYSK")) {
            Shop shop = Shop.builder()
                    .name("JYSK")
                    .URL("www.jysk.com")
                    .countries(Arrays.stream(new String[]{
                            "AL", "AM", "AT", "AZ", "BA", "BE", "BG", "BY", "CH", "CY", "CZ", "DE", "DK", "EE", "ES",
                            "FI", "FR", "GB", "GE", "GR", "HR", "HU", "IE", "IS", "IT", "XK", "KZ", "LT", "LU", "LV",
                            "MD", "ME", "MK", "NL", "NO", "PL", "PT", "RO", "RS", "SE", "SI", "SK", "UA", "VN"
                    }).map(CountryCode::getCountryCode).collect(Collectors.toSet()))
                    .categories(new HashSet<>(Arrays.asList(Category.HOME_AND_LIVING)))
                    .build();
            shopRepository.save(shop);
        }

        if (!shopRepository.existsByName("OBI")) {
            Shop shop = Shop.builder()
                    .name("OBI")
                    .URL("www.obi.de")
                    .countries(Arrays.stream(new String[]{
                            "AT", "BA", "CH", "CZ", "DE", "HU", "IT", "PL", "RO", "SI", "SK"
                    }).map(CountryCode::getCountryCode).collect(Collectors.toSet()))
                    .categories(new HashSet<>(Arrays.asList(Category.HOME_AND_LIVING, Category.TOOLS_AND_HARDWARE)))
                    .build();
            shopRepository.save(shop);
        }

        if (!shopRepository.existsByName("Bauhaus")) {
            Shop shop = Shop.builder()
                    .name("Bauhaus")
                    .URL("www.bauhaus.info")
                    .countries(Arrays.stream(new String[]{
                            "AT", "BG", "CH", "CZ", "DE", "DK", "EE", "FI", "HR", "HU", "IS", "NO", "SE", "SK", "ES", "TR"
                    }).map(CountryCode::getCountryCode).collect(Collectors.toSet()))
                    .categories(new HashSet<>(Arrays.asList(Category.HOME_AND_LIVING, Category.TOOLS_AND_HARDWARE)))
                    .build();
            shopRepository.save(shop);
        }

        if (!shopRepository.existsByName("Hornbach")) {
            Shop shop = Shop.builder()
                    .name("Hornbach")
                    .URL("www.hornbach.de")
                    .countries(Arrays.stream(new String[]{
                            "AT", "CH", "CZ", "DE", "LU", "NL", "RO", "SE", "SK"
                    }).map(CountryCode::getCountryCode).collect(Collectors.toSet()))
                    .categories(new HashSet<>(Arrays.asList(Category.HOME_AND_LIVING, Category.TOOLS_AND_HARDWARE)))
                    .build();
            shopRepository.save(shop);
        }

        if (!shopRepository.existsByName("Kaufland")) {
            Shop shop = Shop.builder()
                    .name("Kaufland")
                    .URL("www.kaufland.com")
                    .countries(Arrays.stream(new String[]{
                            "DE", "BG", "HR", "CZ", "MD", "PL", "RO", "SK"
                    }).map(CountryCode::getCountryCode).collect(Collectors.toSet()))
                    .categories(new HashSet<>(Arrays.asList(Category.GROCERIES, Category.HOME_AND_LIVING, Category.ELECTRONICS, Category.FASHION, Category.HEALTH_AND_BEAUTY, Category.BABY_AND_KIDS, Category.PET_SUPPLIES)))
                    .build();
            shopRepository.save(shop);
        }

        if (!shopRepository.existsByName("Fnac")) {
            Shop shop = Shop.builder()
                    .name("Fnac")
                    .URL("www.fnac.com")
                    .countries(Arrays.stream(new String[]{
                            "FR", "BE", "ES", "PT", "CH"
                    }).map(CountryCode::getCountryCode).collect(Collectors.toSet()))
                    .categories(new HashSet<>(Arrays.asList(Category.BOOKS_AND_MEDIA, Category.ELECTRONICS, Category.HOME_AND_LIVING)))
                    .build();
            shopRepository.save(shop);
        }

        if (!shopRepository.existsByName("C&A")) {
            Shop shop = Shop.builder()
                    .name("C&A")
                    .URL("www.c-and-a.com")
                    .countries(Arrays.stream(new String[]{
                            "AT", "BE", "BG", "CH", "CZ", "DE", "ES", "FR", "HU", "IT", "LU", "NL", "PL", "PT", "RO", "RS", "SI", "SK"
                    }).map(CountryCode::getCountryCode).collect(Collectors.toSet()))
                    .categories(new HashSet<>(Arrays.asList(Category.FASHION)))
                    .build();
            shopRepository.save(shop);
        }

        if (!shopRepository.existsByName("Sephora")) {
            Shop shop = Shop.builder()
                    .name("Sephora")
                    .URL("www.sephora.com")
                    .countries(Arrays.stream(new String[]{
                            "FR", "ES", "PT", "IT", "GR", "PL", "CZ", "SK", "HU", "RO", "BG", "TR", "DK", "SE", "NO", "NL", "BE", "CH", "DE" // Germany is expanding, but it's not physical stores for now, more online focus
                    }).map(CountryCode::getCountryCode).collect(Collectors.toSet()))
                    .categories(new HashSet<>(Arrays.asList(Category.HEALTH_AND_BEAUTY)))
                    .build();
            shopRepository.save(shop);
        }

        if (!shopRepository.existsByName("Douglas")) {
            Shop shop = Shop.builder()
                    .name("Douglas")
                    .URL("www.douglas.de")
                    .countries(Arrays.stream(new String[]{
                            "DE", "AT", "CH", "FR", "NL", "BE", "LU", "PL", "ES", "IT", "CZ", "SK", "HU", "BG", "HR", "SI", "LT", "LV", "EE", "RO"
                    }).map(CountryCode::getCountryCode).collect(Collectors.toSet()))
                    .categories(new HashSet<>(Arrays.asList(Category.HEALTH_AND_BEAUTY)))
                    .build();
            shopRepository.save(shop);
        }

        if (!shopRepository.existsByName("Fressnapf")) {
            Shop shop = Shop.builder()
                    .name("Fressnapf")
                    .URL("www.fressnapf.com")
                    .countries(Arrays.stream(new String[]{
                            "DE", "AT", "CH", "FR", "HU", "IE", "IT", "LU", "PL", "DK", "ES", "CZ", "SK", "SI"
                    }).map(CountryCode::getCountryCode).collect(Collectors.toSet()))
                    .categories(new HashSet<>(Arrays.asList(Category.PET_SUPPLIES)))
                    .build();
            shopRepository.save(shop);
        }

        if (!shopRepository.existsByName("Maxi Zoo")) {
            Shop shop = Shop.builder()
                    .name("Maxi Zoo")
                    .URL("www.maxizoo.fr")
                    .countries(Arrays.stream(new String[]{
                            "FR", "BE", "IE", "DK", "SE", "NO"
                    }).map(CountryCode::getCountryCode).collect(Collectors.toSet()))
                    .categories(new HashSet<>(Arrays.asList(Category.PET_SUPPLIES)))
                    .build();
            shopRepository.save(shop);
        }

        if (!shopRepository.existsByName("Office Depot")) {
            Shop shop = Shop.builder()
                    .name("Office Depot")
                    .URL("www.officedepot.eu")
                    .countries(Arrays.stream(new String[]{
                            "FR", "DE", "GB", "NL", "BE", "LU", "ES", "IT", "IE", "AT", "CH", "PT"
                    }).map(CountryCode::getCountryCode).collect(Collectors.toSet()))
                    .categories(new HashSet<>(Arrays.asList(Category.OFFICE_SUPPLIES)))
                    .build();
            shopRepository.save(shop);
        }

        if (!shopRepository.existsByName("Samsonite")) {
            Shop shop = Shop.builder()
                    .name("Samsonite")
                    .URL("www.samsonite.eu")
                    .countries(Arrays.stream(new String[]{
                            "AT", "BE", "CH", "CZ", "DE", "DK", "ES", "FI", "FR", "GB", "GR", "HU", "IE", "IT",
                            "NL", "NO", "PL", "PT", "SE", "SK"
                    }).map(CountryCode::getCountryCode).collect(Collectors.toSet()))
                    .categories(new HashSet<>(Arrays.asList(Category.TRAVEL_AND_LUGGAGE, Category.FASHION)))
                    .build();
            shopRepository.save(shop);
        }
    }
}
