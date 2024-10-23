package edu.uclm.esi.fakeaccountsbe.dao;

import org.springframework.data.repository.CrudRepository;

import edu.uclm.esi.fakeaccountsbe.model.User;

public interface UserDao extends CrudRepository<User, String> {

	User findByCookie(String fakeUserId);

}
