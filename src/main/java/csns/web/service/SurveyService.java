/*
 * This file is part of the CSNetwork Services (CSNS) project.
 * 
 * Copyright 2012, Mahdiye Jamali (mjamali@calstatela.edu).
 * 
 * CSNS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 * 
 * CSNS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for
 * more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with CSNS. If not, see http://www.gnu.org/licenses/agpl.html.
 */
package csns.web.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import csns.model.academics.Department;
import csns.model.academics.dao.DepartmentDao;
import csns.model.core.User;
import csns.model.core.dao.UserDao;
import csns.model.qa.Answer;
import csns.model.qa.AnswerSection;
import csns.model.qa.AnswerSheet;
import csns.model.qa.ChoiceAnswer;
import csns.model.qa.ChoiceQuestion;
import csns.model.qa.Question;
import csns.model.qa.QuestionSection;
import csns.model.qa.QuestionSheet;
import csns.model.qa.TextAnswer;
import csns.model.qa.TextQuestion;
import csns.model.survey.Survey;
import csns.model.survey.SurveyResponse;
import csns.model.survey.SurveyType;
import csns.model.survey.dao.SurveyDao;
import csns.model.survey.dao.SurveyResponseDao;

@Controller
public class SurveyService {

	@Autowired
	private DepartmentDao departmentDao;

	@Autowired
	private SurveyDao surveyDao;
	
	@Autowired
	private SurveyResponseDao surveyResponseDao;

	@Autowired
	private UserDao userDao;
	
	private static final Logger logger = LoggerFactory.getLogger( SurveyService.class );

	@RequestMapping("/service/survey/list")
	public String list(ModelMap models, @RequestParam(name = "dept") String dept) {

		Department department = departmentDao.getDepartment(dept);
		
		List<Survey> openSurveys = null;
		openSurveys = surveyDao.getOpenSurveys(department);
		
		if(openSurveys == null || openSurveys.size() == 0) {
			User user = userDao.getUserByUsername("cysun");
			/*--------------- Test data --------------*/
			Survey testSurvey = new Survey();
			Calendar publishDate = Calendar.getInstance();
			testSurvey.setPublishDate(publishDate);
			Calendar closeDate = Calendar.getInstance();
			closeDate.add(Calendar.DATE, 3);
			testSurvey.setCloseDate(closeDate);
			testSurvey.setDepartment(department);
			testSurvey.setName("survey1");
			testSurvey.setAuthor(user);
			testSurvey.setDate(new Date());
			QuestionSheet questionSheet = new QuestionSheet();
			questionSheet.setDescription("This is a test question sheet.");
			List<QuestionSection> sections = new ArrayList<>();

			/* add section 1 */
			QuestionSection section = new QuestionSection();
			section.setDescription("section1");
			List<Question> questions = new ArrayList<>();
			ChoiceQuestion question1 = new ChoiceQuestion();
			question1.setMaxSelections(1);
			question1.setMinSelections(1);
			question1.setDescription("Which of the <i>following</i> <b>courses</b> you are going to take?");
			@SuppressWarnings("serial")
			List<String> choices = new ArrayList<String>() {
				{
					add("CS101");
					add("CS202");
					add("CS400");
				}
			};
			question1.setChoices(choices);
			questions.add(question1);

			TextQuestion question2 = new TextQuestion();
			question2.setDescription("Enter your email address here.");
			questions.add(question2);
			section.setQuestions(questions);
			sections.add(section);

			/* add section 2 */
			questions = new ArrayList<>();
			section = new QuestionSection();
			section.setDescription("section2");
			questions = new ArrayList<>();
			question1 = new ChoiceQuestion();
			question1.setMaxSelections(1);
			question1.setDescription("Which of the following courses you are going to take?");
			@SuppressWarnings("serial")
			List<String> choices2 = new ArrayList<String>() {
				{
					add("CS203");
					add("CS450");
					add("CS560");
					add("CS580");
				}
			};
			question1.setChoices(choices2);
			questions.add(question1);

			question1 = new ChoiceQuestion();
			question1.setDescription("Which of the following courses you are going to take?");
			@SuppressWarnings("serial")
			List<String> choices3 = new ArrayList<String>() {
				{
					add("CS412");
					add("CS320");
					add("CS520");
					add("CS570");
				}
			};
			question1.setChoices(choices3);
			questions.add(question1);

			question2 = new TextQuestion();
			question2.setDescription("Enter your cin here.");
			questions.add(question2);

			question2 = new TextQuestion();
			question2.setDescription("Enter your start year.");
			questions.add(question2);

			section.setQuestions(questions);
			sections.add(section);

			questionSheet.setSections(sections);
			testSurvey.setQuestionSheet(questionSheet);
			
			testSurvey = surveyDao.saveSurvey(testSurvey);
			
			openSurveys.add(testSurvey);
			
			/*----------test survey 2------------------*/
			
			testSurvey = new Survey();
			testSurvey.setType(SurveyType.RECORDED);
			testSurvey.setPublishDate(publishDate);
			testSurvey.setCloseDate(closeDate);
			testSurvey.setDepartment(department);
			testSurvey.setName("survey2");
			testSurvey.setAuthor(user);
			testSurvey.setDate(new Date());
			questionSheet = new QuestionSheet();
			questionSheet.setDescription("Another test question sheet.");
			sections = new ArrayList<>();

			/* add section 1 */
			section = new QuestionSection();
			section.setDescription("section1");
			questions = new ArrayList<>();
			question1 = new ChoiceQuestion();
			question1.setDescription("Which of the following courses you are going to take???");
			question1.setChoices(choices);
			questions.add(question1);

			question2 = new TextQuestion();
			question2.setDescription("Enter your email address here???");
			questions.add(question2);
			section.setQuestions(questions);
			sections.add(section);

			/* add section 2 */
			questions = new ArrayList<>();
			section = new QuestionSection();
			section.setDescription("section2");
			questions = new ArrayList<>();
			question1 = new ChoiceQuestion();
			question1.setDescription("Which of the following courses you are going to take???");
			question1.setChoices(choices2);
			questions.add(question1);

			question1 = new ChoiceQuestion();
			question1.setDescription("Which of the following courses you are going to take???");
			question1.setChoices(choices3);
			questions.add(question1);

			question2 = new TextQuestion();
			question2.setDescription("Enter your cin here???");
			questions.add(question2);

			question2 = new TextQuestion();
			question2.setDescription("Enter your start year???");
			questions.add(question2);

			section.setQuestions(questions);
			sections.add(section);

			questionSheet.setSections(sections);
			testSurvey.setQuestionSheet(questionSheet);
			
			testSurvey = surveyDao.saveSurvey(testSurvey);
			
			openSurveys.add(testSurvey);
			/*-----------------------------------------*/
			
			/*----------test survey 3------------------*/
			
			testSurvey = new Survey();
			testSurvey.setType(SurveyType.NAMED);
			testSurvey.setPublishDate(publishDate);
			testSurvey.setCloseDate(closeDate);
			testSurvey.setDepartment(department);
			testSurvey.setName("survey3");
			testSurvey.setAuthor(user);
			testSurvey.setDate(new Date());
			questionSheet = new QuestionSheet();
			questionSheet.setDescription("Another test question sheet.");
			sections = new ArrayList<>();

			/* add section 1 */
			section = new QuestionSection();
			section.setDescription("section1");
			questions = new ArrayList<>();
			question1 = new ChoiceQuestion();
			question1.setDescription("Which of the following courses you are going to take???");
			question1.setChoices(choices);
			questions.add(question1);
			section.setQuestions(questions);
			sections.add(section);

			/* add section 2 */
			questions = new ArrayList<>();
			section = new QuestionSection();
			section.setDescription("section2");
			questions = new ArrayList<>();
			question1 = new ChoiceQuestion();
			question1.setDescription("Which of the following courses you are going to take???");
			question1.setChoices(choices2);
			questions.add(question1);

			question2 = new TextQuestion();
			question2.setDescription("Enter your cin here???");
			questions.add(question2);

			question2 = new TextQuestion();
			question2.setDescription("Enter your start year???");
			questions.add(question2);

			section.setQuestions(questions);
			sections.add(section);

			questionSheet.setSections(sections);
			testSurvey.setQuestionSheet(questionSheet);
			
			testSurvey = surveyDao.saveSurvey(testSurvey);
			
			openSurveys.add(testSurvey);
			/*-----------------------------------------*/
		}
		
		Map<Long, Integer> sectionIndices = new HashMap<>();
		for(Survey survey: openSurveys){
			List<QuestionSection> sections = survey.getQuestionSheet().getSections();
			for(int i = 0; i<sections.size(); i++){
				sectionIndices.put(sections.get(i).getId(), i);
			}
		}

		models.put("sectionIndices", sectionIndices);
		models.put("surveys", openSurveys);
		return "jsonView";
	}
	
	@RequestMapping("/service/survey/responses")
	public String getResponses(ModelMap models, @RequestParam(name = "dept") String dept) {

		Department department = departmentDao.getDepartment(dept);
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();	
		User user = (User) authentication.getPrincipal();
		
		List<Survey> openSurveys = surveyDao.getOpenSurveys(department);
		List<SurveyResponse> responses = new ArrayList<>();
		SurveyResponse response;

		Set<Survey> surveysTaken = user.getSurveysTaken();
		Set<Long> surveysTakenIds = new HashSet<Long>();
		
		if(openSurveys != null && openSurveys.size() > 0){			
			for(Survey survey: openSurveys){
				/* for named surveys */
				response = surveyResponseDao.getSurveyResponse(survey, user);
				if(response != null){
					responses.add(response);
				}
				 
				/* for recorded surveys */
				if(surveysTaken.contains(survey)){
					surveysTakenIds.add(survey.getId());
				}
			}
		}
		
		models.put("surveysTakenIds", surveysTakenIds);
		models.put("responses", responses);
		return "jsonView";
	}

	@RequestMapping(value = "/service/survey/saveAnswers", method = RequestMethod.POST)
	public String saveAnswers(@RequestParam(name = "dept") String dept, HttpServletRequest request) {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();	
		User user = (User) authentication.getPrincipal();
		
		StringBuffer buffer = new StringBuffer();
		String line = null;
		try {
			BufferedReader reader = request.getReader();
			while ((line = reader.readLine()) != null) {
				buffer.append(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			JSONObject jsonObject = new JSONObject(buffer.toString());
			SurveyResponse response = getResponseFromJson(jsonObject);
			/* save response */
			if(response != null){
				response = surveyResponseDao.saveSurveyResponse(response);
				if( response.getSurvey().getType() == SurveyType.NAMED )
	                logger.info( user.getUsername()
	                    + " completed survey " + response.getSurvey().getId() );
	            else
	                logger.info( "A user completed survey " + response.getSurvey().getId() );
				System.out.println(user.getUsername() + " completed survey " + response.getSurvey().getId() + " responseID : " + response.getId());
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return "jsonView";
	}
	
	@RequestMapping(value = "/service/anonymous/survey/saveAnswers", method = RequestMethod.POST)
	public String saveAnonymousAnswers(@RequestParam(name = "dept") String dept, HttpServletRequest request) {	

		StringBuffer buffer = new StringBuffer();
		String line = null;
		try {
			BufferedReader reader = request.getReader();
			while ((line = reader.readLine()) != null) {
				buffer.append(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			JSONObject jsonObject = new JSONObject(buffer.toString());
			SurveyResponse response = getResponseFromJson(jsonObject);
			/* save response */
			if(response != null){
				response = surveyResponseDao.saveSurveyResponse(response);
				logger.info( "A user completed survey " + response.getSurvey().getId() );
				System.out.println("A user completed survey " + response.getSurvey().getId() + " responseID : " + response.getId());
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return "jsonView";
	}

	public SurveyResponse getResponseFromJson(JSONObject jsonObject) {
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();	
		SurveyResponse response = null;

		try {
			Survey survey = surveyDao.getSurvey(jsonObject.getLong("surveyId"));
			
			response = new SurveyResponse(survey);
			
			AnswerSheet answerSheet = response.getAnswerSheet();
			if(survey.getType().equals(SurveyType.NAMED)){
				User user = (User) authentication.getPrincipal();
				answerSheet.setAuthor(user);
			}
			if( survey.getType().equals( SurveyType.RECORDED ) ){
				User user = (User) authentication.getPrincipal();
                User recordedUser = userDao.getUser( user.getId() );
                user.getSurveysTaken().add( survey );
                userDao.saveUser( recordedUser );
            }
			answerSheet.setDate(new Date());
			
			JSONObject answerSheetJsonObject = jsonObject.getJSONObject("answerSheet");
			JSONArray sectionsJsonArray = answerSheetJsonObject.getJSONArray("sections");
			
			Map<Integer, JSONObject> sectionsMap = new HashMap<>();
			
			for (int i = 0; i < sectionsJsonArray.length(); i++) {
				JSONObject sectionJsonObject = sectionsJsonArray.getJSONObject(i);
				int index = sectionJsonObject.getInt("index");
				sectionsMap.put(index, sectionJsonObject);
			}

			List<AnswerSection> sections = answerSheet.getSections();
			for(int i = 0; i<sections.size(); i++){
				AnswerSection section = answerSheet.getSections().get(i);
				
				JSONObject sectionJsonObject = sectionsMap.get(i);
				JSONArray answersJsonArray = sectionJsonObject.getJSONArray("answers");
				
				Map<Long, JSONObject> answersMap = new HashMap<>(); 
				
				for(int j = 0; j<answersJsonArray.length(); j++) {
					JSONObject answerJsonObject = answersJsonArray.getJSONObject(j);
					Long questionId = answerJsonObject.getLong("questionId");
					
					answersMap.put(questionId, answerJsonObject);
				}
				
				List<Answer> answers = section.getAnswers();
				
				for(Answer answer: answers){
					JSONObject answerJsonObject = answersMap.get(answer.getQuestion().getId());
					
					if(answer instanceof ChoiceAnswer){
						JSONArray selectionsJsonArray = answerJsonObject.getJSONArray("selections");
						for(int sel = 0; sel<selectionsJsonArray.length(); sel++){
							((ChoiceAnswer)answer).getSelections().add(selectionsJsonArray.getInt(sel));
						}
					}else if(answer instanceof TextAnswer){
						((TextAnswer)answer).setText(answerJsonObject.getString("text"));	
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return response;
	}

}
