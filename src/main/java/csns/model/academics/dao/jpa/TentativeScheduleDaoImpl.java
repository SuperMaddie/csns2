package csns.model.academics.dao.jpa;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import csns.model.academics.Department;
import csns.model.academics.TentativeSchedule;
import csns.model.academics.Term;
import csns.model.academics.dao.TentativeScheduleDao;

@Repository
public class TentativeScheduleDaoImpl implements TentativeScheduleDao{

	@PersistenceContext
	private EntityManager entityManager;
	
	@Override
	public TentativeSchedule getSchedule(Long id) {
		return entityManager.find(TentativeSchedule.class, id);
	}

	@Override
	public TentativeSchedule getSchedule(Department dept, Term term) {
		String query = "from TentativeSchedule where department = :department "
				+ "and term = :term "
				+ "and deleted = false "
				+ "order by id asc";
		List<TentativeSchedule> result = entityManager.createQuery(query, TentativeSchedule.class)
				.setParameter("department", dept)
				.setParameter("term", term)
				.getResultList();
		return result.size() == 0 ? null : result.get(0);
	}

	@Override
	public List<TentativeSchedule> getSchedules(Department dept) {
		String query = "from TentativeSchedule where department = :department "
				+ "and deleted = false "
				+ "order by id asc";
		return entityManager.createQuery(query, TentativeSchedule.class)
				.setParameter("department", dept)
				.getResultList();
	}

	@Override
	@Transactional
	@PreAuthorize("authenticated and principal.faculty")
	public TentativeSchedule saveSchedule(TentativeSchedule schedule) {
		return entityManager.merge(schedule);
	}

}
