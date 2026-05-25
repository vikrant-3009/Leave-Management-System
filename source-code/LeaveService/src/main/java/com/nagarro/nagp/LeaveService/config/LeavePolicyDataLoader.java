package com.nagarro.nagp.LeaveService.config;

import com.nagarro.nagp.LeaveService.entity.LeavePolicy;
import com.nagarro.nagp.LeaveService.enums.LeaveType;
import com.nagarro.nagp.LeaveService.repository.LeavePolicyRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LeavePolicyDataLoader {

    @Autowired
    private LeavePolicyRepository leavePolicyRepository;

    @PostConstruct
    public void loadPolicies() {
        createPolicy(LeaveType.SICK, 10, 10);
        createPolicy(LeaveType.CASUAL, 12, 0);
        createPolicy(LeaveType.PRIVILEGE, 15, 15);
    }

    private void createPolicy(
            LeaveType leaveType,
            int yearlyQuota,
            int maxCarryForward
    ) {
        if (leavePolicyRepository.existsById(leaveType)) {
            return;
        }

        LeavePolicy policy = new LeavePolicy();
        policy.setLeaveType(leaveType);
        policy.setYearlyQuota(yearlyQuota);
        policy.setMaxCarryForwardAllowed(maxCarryForward);
        leavePolicyRepository.save(policy);
    }
}
