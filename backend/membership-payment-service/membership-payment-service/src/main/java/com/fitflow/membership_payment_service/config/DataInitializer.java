package com.fitflow.membership_payment_service.config;

import com.fitflow.membership_payment_service.entity.MembershipPlan;
import com.fitflow.membership_payment_service.repository.MembershipPlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final MembershipPlanRepository membershipPlanRepository;

    @Override
    public void run(String... args) {
        if (membershipPlanRepository.count() == 0) {
            // BASIC Plan
            MembershipPlan basic = new MembershipPlan();
            basic.setName("BASIC");
            basic.setDescription("Plan podstawowy z dostępem do siłowni");
            basic.setPrice(new BigDecimal("99.99"));
            basic.setDurationDays(30);
            basic.setIsActive(true);
            basic.setFeatures("Dostęp do siłowni,Szafka,Wi-Fi");
            membershipPlanRepository.save(basic);

            // PREMIUM Plan
            MembershipPlan premium = new MembershipPlan();
            premium.setName("PREMIUM");
            premium.setDescription("Plan premium z dodatkowymi korzyściami");
            premium.setPrice(new BigDecimal("149.99"));
            premium.setDurationDays(30);
            premium.setIsActive(true);
            premium.setFeatures("Dostęp do siłowni,Szafka,Wi-Fi,Zajęcia grupowe,Sauna");
            membershipPlanRepository.save(premium);

            // VIP Plan
            MembershipPlan vip = new MembershipPlan();
            vip.setName("VIP");
            vip.setDescription("Plan VIP z pełnym dostępem");
            vip.setPrice(new BigDecimal("249.99"));
            vip.setDurationDays(30);
            vip.setIsActive(true);
            vip.setFeatures("Dostęp do siłowni,Szafka,Wi-Fi,Zajęcia grupowe,Sauna,Trener personalny,Spa,Drinki proteinowe");
            membershipPlanRepository.save(vip);

            System.out.println("Membership plans initialized successfully!");
        }
    }
}
