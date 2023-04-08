package io.f12.notionlinkedblog.repository.user;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

import io.f12.notionlinkedblog.domain.user.User;

@Repository
public class UserRepository {

	@PersistenceContext
	private EntityManager em;

	public User save(final User user) {
		em.persist(user);
		return user;
	}

	public Optional<User> findByEmail(final String email) {
		List<User> users = em.createQuery("select u from User u where u.email=:email", User.class)
			.setParameter("email", email)
			.getResultList();

		return users.isEmpty() ? Optional.empty() : Optional.of(users.get(0));
	}
}
