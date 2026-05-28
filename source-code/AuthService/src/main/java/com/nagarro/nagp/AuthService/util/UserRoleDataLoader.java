package com.nagarro.nagp.AuthService.util;

import com.nagarro.nagp.AuthService.entity.Role;
import com.nagarro.nagp.AuthService.entity.User;
import com.nagarro.nagp.AuthService.enums.RoleEnum;
import com.nagarro.nagp.AuthService.repository.RoleRepository;
import com.nagarro.nagp.AuthService.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class UserRoleDataLoader {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostConstruct
    public void initData() throws Exception {
        Role employeeRole = getOrCreateRole(RoleEnum.EMPLOYEE);
        Role managerRole = getOrCreateRole(RoleEnum.MANAGER);

        // Managers
        User manager1 = createManagerIfNotExists(
                "johndoe",
                "john",
                "MGR001",
                "John Doe",
                "john.doe@test.com",
                Set.of(managerRole, employeeRole)
        );
        User manager2 = createManagerIfNotExists(
                "anjalisingh",
                "manager2",
                "MGR002",
                "Anjali Singh",
                "anjali.singh@test.com",
                Set.of(managerRole, employeeRole)
        );

        // Employees under manager1
        createEmployeeIfNotExists(
                "rahuljain",
                "rahul",
                "EMP001",
                "Rahul Jain",
                "rahul.jain@test.com",
                manager1,
                Set.of(employeeRole)
        );
        createEmployeeIfNotExists(
                "janedoe",
                "jane",
                "EMP002",
                "Jane Doe",
                "jane.doe@test.com",
                manager1,
                Set.of(employeeRole)
        );

        // Employees under manager2
        createEmployeeIfNotExists(
                "miketyson",
                "mike",
                "EMP003",
                "Mike Tyson",
                "mike.tyson@test.com",
                manager2,
                Set.of(employeeRole)
        );
        createEmployeeIfNotExists(
                "priyaverma",
                "priya",
                "EMP004",
                "Priya Verma",
                "priya.verma@test.com",
                manager2,
                Set.of(employeeRole)
        );

//        User manager;
//        if (userRepository.findByUsername("manager1").isEmpty()) {
//            manager = new User();
//            manager.setUsername("manager1");
//            manager.setPassword(passwordEncoder.encode("manager1"));
//            manager.setEmployeeCode("EMP001");
//            manager.setName("Manager One");
//            manager.setEmail("manager@test.com");
//            manager.setActive(true);
//            manager.getRoles().add(managerRole);
//            manager.getRoles().add(employeeRole);
//            userRepository.save(manager);
//        }

//        if (userRepository.findByUsername("user1").isEmpty()) {
//            manager = userRepository.findByUsername("manager1")
//                    .orElseThrow(() ->
//                            new UserNotFoundException("Manager not found"));
//            User employee = new User();
//            employee.setUsername("user1");
//            employee.setPassword(passwordEncoder.encode("user1"));
//            employee.setEmployeeCode("EMP002");
//            employee.setName("Employee One");
//            employee.setEmail("user@test.com");
//            employee.setManager(manager);
//            employee.setActive(true);
//            employee.getRoles().add(employeeRole);
//            userRepository.save(employee);
//        }
    }

    private Role getOrCreateRole(RoleEnum roleEnum) {
        return roleRepository
                .findByName(roleEnum)
                .orElseGet(() -> {
                    Role role = new Role();
                    role.setName(roleEnum);
                    return roleRepository.save(role);
                });
    }

    private User createManagerIfNotExists(
            String username,
            String password,
            String employeeCode,
            String name,
            String email,
            Set<Role> roles) {
        return userRepository
                .findByUsername(username)
                .orElseGet(() -> {
                    User manager = User.builder()
                            .username(username)
                            .password(passwordEncoder.encode(password))
                            .employeeCode(employeeCode)
                            .name(name)
                            .email(email)
                            .active(true)
                            .roles(roles)
                            .build();
                    return userRepository.save(manager);
                });
    }

    private void createEmployeeIfNotExists(
            String username,
            String password,
            String employeeCode,
            String name,
            String email,
            User manager,
            Set<Role> roles) throws Exception {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new Exception("User with this username already exists.");
        }
        User employee = User.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .employeeCode(employeeCode)
                .name(name)
                .email(email)
                .manager(manager)
                .active(true)
                .roles(roles)
                .build();
        userRepository.save(employee);
    }
}
