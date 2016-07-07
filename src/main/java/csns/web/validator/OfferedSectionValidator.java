package csns.web.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import csns.model.academics.OfferedSection;

@Component
public class OfferedSectionValidator  implements Validator {

	@Override
	public boolean supports(Class<?> clazz){
		return OfferedSection.class.isAssignableFrom( clazz );
	}

	@Override
	public void validate(Object target, Errors errors) {
		ValidationUtils.rejectIfEmptyOrWhitespace( errors,
	            "sectionTitle", "error.field.required" );
	}
}