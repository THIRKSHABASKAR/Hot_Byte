package com.hotbyte.config;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.hotbyte.entity.Coupon;
import com.hotbyte.repository.CouponRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final CouponRepository couponRepository;

    @Override
    public void run(String... args) {
        if (couponRepository.count() > 0) {
            log.info("Coupons already exist - skipping seed.");
            return;
        }

        List<Coupon> coupons = List.of(

            Coupon.builder()
                .code("HOTBYTE20")
                .title("20% Off First Order")
                .description("Get 20% off your first order with HotByte!")
                .discountType("PERCENTAGE")
                .discountValue(20.0)
                .minOrderAmount(199.0)
                .maxDiscount(100.0)
                .isActive(true)
                .offerType("COUPON")
                .badgeColor("orange")
                .expiryDate(LocalDateTime.now().plusDays(30))
                .build(),

            Coupon.builder()
                .code("FLAT50")
                .title("Flat Rs.50 Off")
                .description("Flat Rs.50 off on orders above Rs.299")
                .discountType("FLAT")
                .discountValue(50.0)
                .minOrderAmount(299.0)
                .isActive(true)
                .offerType("COUPON")
                .badgeColor("green")
                .expiryDate(LocalDateTime.now().plusDays(15))
                .build(),

            Coupon.builder()
                .code("WELCOME100")
                .title("Welcome Rs.100 Off")
                .description("Rs.100 off for new users on orders above Rs.499")
                .discountType("FLAT")
                .discountValue(100.0)
                .minOrderAmount(499.0)
                .isActive(true)
                .offerType("COUPON")
                .badgeColor("blue")
                .expiryDate(LocalDateTime.now().plusDays(60))
                .build(),

            Coupon.builder()
                .code("SAVE30")
                .title("30% Weekend Deal")
                .description("30% off on weekend orders - max Rs.150 discount")
                .discountType("PERCENTAGE")
                .discountValue(30.0)
                .minOrderAmount(349.0)
                .maxDiscount(150.0)
                .isActive(true)
                .offerType("COUPON")
                .badgeColor("purple")
                .expiryDate(LocalDateTime.now().plusDays(7))
                .build(),

            Coupon.builder()
                .code("FREEDEL")
                .title("Free Delivery")
                .description("Free delivery on orders above Rs.149")
                .discountType("FLAT")
                .discountValue(40.0)
                .minOrderAmount(149.0)
                .isActive(true)
                .offerType("COUPON")
                .badgeColor("green")
                .expiryDate(LocalDateTime.now().plusDays(10))
                .build(),

            Coupon.builder()
                .code("LUNCH25")
                .title("Lunch 25% Off")
                .description("25% off on all lunch orders - max Rs.120 off")
                .discountType("PERCENTAGE")
                .discountValue(25.0)
                .minOrderAmount(249.0)
                .maxDiscount(120.0)
                .isActive(true)
                .offerType("COUPON")
                .badgeColor("orange")
                .expiryDate(LocalDateTime.now().plusDays(20))
                .build()
        );

        couponRepository.saveAll(coupons);
        log.info("Seeded {} sample coupons.", coupons.size());
    }
}