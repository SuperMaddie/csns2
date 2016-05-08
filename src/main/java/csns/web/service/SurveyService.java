package csns.web.service;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;

import csns.model.academics.Department;
import csns.model.academics.dao.DepartmentDao;
import csns.model.core.User;
import csns.model.qa.ChoiceQuestion;
import csns.model.qa.Question;
import csns.model.qa.QuestionSection;
import csns.model.qa.QuestionSheet;
import csns.model.survey.Survey;
import csns.model.survey.dao.SurveyDao;

@Controller
public class SurveyService {
	
	@Autowired
	private DepartmentDao departmentDao;
	
	@Autowired
	private SurveyDao surveyDao;

	@RequestMapping("/service/survey/list")
	public String list(ModelMap models, @RequestParam(name="dept") String dept) {
		Department department = departmentDao.getDepartment(dept);
		List<Survey> openSurveys = surveyDao.getOpenSurveys(department);
		
		/*--------------- Test data --------------*/
		Survey testSurvey = new Survey();
		User user = new User();
		user.setFirstName("Maddie");
		user.setCin("1111");
		testSurvey.setId(1L);
		testSurvey.setName("test");
		QuestionSheet questionSheet = new QuestionSheet();
		questionSheet.setDescription("aaaaa");
		List<QuestionSection> sections = new ArrayList<>();
		QuestionSection section = new QuestionSection();
		section.setDescription("section1");
		List<Question> questions = new ArrayList<>();
		Question question = new ChoiceQuestion();
		question.setDescription("choice question1");
		questions.add(question);
		section.setQuestions(questions);
		sections.add(section);
		questionSheet.setSections(sections);
		testSurvey.setQuestionSheet(questionSheet);
		openSurveys.add(testSurvey);
		/*-----------------------------------------*/

		models.put("surveys", openSurveys);
		return "jsonView"; 
	}
}
