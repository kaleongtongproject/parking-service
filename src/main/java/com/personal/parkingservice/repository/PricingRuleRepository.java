package com.personal.parkingservice.repository;

import com.personal.parkingservice.entity.PricingRule;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PricingRuleRepository extends JpaRepository<PricingRule, Long> {
}
