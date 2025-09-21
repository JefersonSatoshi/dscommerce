package com.devsuperior.dscommerce.tests;

import java.time.LocalDate;

import com.devsuperior.dscommerce.entities.Role;
import com.devsuperior.dscommerce.entities.User;

public class UserFactory {
	
	public static User createClientUser() {
		
		User user = new User(1L, "Maria", "maria@gmail.com", "999554422", LocalDate.parse("2001-07-05"), "$2a$10$MEND63BhCrLIxdB8c9QfRe39bxYSkUBSZbuFS4Rt.KfqfyxissRZm");
		user.addRole(new Role(1L, "ROLE_CLIENT"));
		return user;
	}
	
	public static User createAdminUser() {
		
		User user = new User(2L, "Alex", "alex@gmail.com", "999357755", LocalDate.parse("1994-02-10"), "$2a$10$MEND63BhCrLIxdB8c9QfRe39bxYSkUBSZbuFS4Rt.KfqfyxissRZm");
		user.addRole(new Role(2L, "ROLE_ADMIN"));
		return user;
	}
	
	public static User createCustomClientUser(Long id, String username) {
		
		User user = new User(id, "Maria", username, "999554422", LocalDate.parse("2001-07-05"), "$2a$10$MEND63BhCrLIxdB8c9QfRe39bxYSkUBSZbuFS4Rt.KfqfyxissRZm");
		user.addRole(new Role(1L, "ROLE_CLIENT"));
		return user;
	}
	
	public static User createCustomAdminUser(Long id, String username) {
		
		User user = new User(id, "Alex", username, "999357755", LocalDate.parse("1994-02-10"), "$2a$10$MEND63BhCrLIxdB8c9QfRe39bxYSkUBSZbuFS4Rt.KfqfyxissRZm");
		user.addRole(new Role(2L, "ROLE_ADMIN"));
		return user;
	}
}
