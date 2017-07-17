package com.xebia.bootstrap;

import com.xebia.domains.Role;
import com.xebia.domains.User;
import com.xebia.services.reposervices.role.RoleRepoService;
import com.xebia.services.reposervices.user.UserRepoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by artur.skrzydlo on 2017-07-11.
 */
@Component
public class SpringRepoBootstrap implements ApplicationListener<ContextRefreshedEvent> {

    private UserRepoService userRepoService;
    private RoleRepoService roleRepoService;

    @Autowired
    public void setUserRepoService(UserRepoService userRepoService) {
        this.userRepoService = userRepoService;
    }

    @Autowired
    public void setRoleRepoService(RoleRepoService roleRepoService) {
        this.roleRepoService = roleRepoService;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {

        loadUsers();
        loadRoles();
        assignUsersToDefalutRoles();
        assignArturAsAdmin();

    }

    private void assignUsersToDefalutRoles() {

        List<Role> roles = (List<Role>) roleRepoService.listAll();
        List<User> users = (List<User>) userRepoService.listAll();

        users.stream().forEach(user -> roles.forEach(role -> {
            if (role.getRoleName().equals("USER")) {
                user.addRole(role);
                userRepoService.saveOrUpdate(user);
            }
        }));
    }

    private void assignArturAsAdmin() {
        User userArtur = userRepoService.findUser("Artur");
        Role roleAdmin = roleRepoService.findByRoleName("ADMIN");

        userArtur.addRole(roleAdmin);
        userRepoService.saveOrUpdate(userArtur);

    }

    private void loadRoles() {

        Role roleAdmin = new Role();
        roleAdmin.setRoleName("ADMIN");

        roleRepoService.saveOrUpdate(roleAdmin);

        Role roleUser = new Role();
        roleUser.setRoleName("USER");

        roleRepoService.saveOrUpdate(roleUser);
    }

    private void loadUsers() {

        User userArtur = new User();
        userArtur.setName("Artur");
        userArtur.setPassword("Caldara12345");

        userRepoService.saveOrUpdate(userArtur);

        User userXebia = new User();
        userXebia.setName("Xebia");
        userXebia.setPassword("password");

        userRepoService.saveOrUpdate(userXebia);
    }
}
