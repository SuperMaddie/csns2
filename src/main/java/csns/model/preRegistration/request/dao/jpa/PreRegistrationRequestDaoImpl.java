package csns.model.preRegistration.request.dao.jpa;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import csns.model.academics.Department;
import csns.model.academics.OfferedSection;
import csns.model.academics.Term;
import csns.model.core.User;
import csns.model.preRegistration.request.PreRegistrationRequest;
import csns.model.preRegistration.request.dao.PreRegistrationRequestDao;

@Repository
public class PreRegistrationRequestDaoImpl implements PreRegistrationRequestDao{

	@PersistenceContext
	private EntityManager entityManager;
	
	@Override
	public PreRegistrationRequest getRequest(Long id) {
		return entityManager.find(PreRegistrationRequest.class, id);
	}

	@Override
	public List<PreRegistrationRequest> getRequests(Department department) {
		String query = "from PreRegistrationRequest " +
						"where department = :department";
		return entityManager.createQuery(query, PreRegistrationRequest.class)
				.setParameter("department", department)
				.getResultList();
	}

	@Override
	public List<PreRegistrationRequest> getRequests(User user) {
		String query = "from PreRegistrationRequest " +
				"where requester = :user";
		return entityManager.createQuery(query, PreRegistrationRequest.class)
				.setParameter("user", user)
				.getResultList();
	}
	
	@Override
	public PreRegistrationRequest getRequest(User user, Term term) {
		String query = "from PreRegistrationRequest " +
				"where requester = :user " + 
				"and term = :term";
		List<PreRegistrationRequest> result =  entityManager.createQuery(query, PreRegistrationRequest.class)
				.setParameter("user", user)
				.setParameter("term", term)
				.getResultList();
		return result.size() == 0 ? null : result.get(0);
	}

	@Override
	public List<PreRegistrationRequest> getRequests(OfferedSection section) {
		String query = "select req from PreRegistrationRequest req " +
				"join req.sections section " +
				"where section = :section";
		return entityManager.createQuery(query, PreRegistrationRequest.class)
				.setParameter("section", section)
				.getResultList();
	}

	@Override
	@Transactional
	public PreRegistrationRequest saveRequest(PreRegistrationRequest request) {
		return entityManager.merge(request);
	}

}
