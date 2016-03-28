package csns.model.preRegistration.request.dao;

import java.util.List;

import csns.model.academics.Department;
import csns.model.academics.OfferedSection;
import csns.model.core.User;
import csns.model.preRegistration.request.PreRegistrationRequest;

public interface PreRegistrationRequestDao {
	
	PreRegistrationRequest getRequest( Long id );
	List<PreRegistrationRequest> getRequests( Department department );
	List<PreRegistrationRequest> getRequests( User user );
	List<PreRegistrationRequest> getRequests( OfferedSection section );
	PreRegistrationRequest saveRequest( PreRegistrationRequest request );
}
