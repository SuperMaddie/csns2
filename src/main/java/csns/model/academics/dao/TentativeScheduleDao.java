package csns.model.academics.dao;

import java.util.List;

import csns.model.academics.Department;
import csns.model.academics.TentativeSchedule;
import csns.model.academics.Term;

public interface TentativeScheduleDao {
	
	TentativeSchedule getSchedule(Long id);
	
	TentativeSchedule getSchedule(Department dept, Term term);
	
	List<TentativeSchedule> getSchedules(Department dept);
	
	TentativeSchedule saveSchedule(TentativeSchedule schedule);
}
