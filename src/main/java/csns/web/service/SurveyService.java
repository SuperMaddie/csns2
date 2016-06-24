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
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
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
import csns.model.qa.dao.QuestionDao;
import csns.model.survey.Survey;
import csns.model.survey.SurveyResponse;
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
	private QuestionDao questionDao;

	@RequestMapping("/service/survey/list")
	public String list(ModelMap models, @RequestParam(name = "dept") String dept) {

		Department department = departmentDao.getDepartment(dept);
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		User user = (User) authentication.getPrincipal();
		
		List<Survey> openSurveys = null;
		openSurveys = surveyDao.getOpenSurveys(department);
		
		if(openSurveys != null && openSurveys.size() == 0) {
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
			question1.setDescription("Which of the following courses you are going to take?");
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
		}

		models.put("surveys", openSurveys);
		return "jsonView";
	}

	@RequestMapping(value = "/service/survey/saveAnswers", method = RequestMethod.POST)
	public String saveAnswers(@RequestParam(name = "dept") String dept, HttpServletRequest request) {

		/* get current user */
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
			SurveyResponse response = getResponseFromJson(jsonObject, user);
			/* save response */
			response = surveyResponseDao.saveSurveyResponse(response);
			System.out.println(response.getId());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return "jsonView";
	}

	public SurveyResponse getResponseFromJson(JSONObject jsonObject, User user) {
		SurveyResponse response = null;

		try {
			Survey survey = surveyDao.getSurvey(jsonObject.getLong("surveyId"));

			response = new SurveyResponse(survey);
			
			AnswerSheet answerSheet = response.getAnswerSheet();
			answerSheet.setAuthor(user);
			answerSheet.setDate(new Date());
			
			JSONObject answerSheetJsonObject = jsonObject.getJSONObject("answerSheet");
			JSONArray sectionsJsonArray = answerSheetJsonObject.getJSONArray("sections");

			for (int i = 0; i < sectionsJsonArray.length(); i++) {
				JSONObject sectionJsonObject = sectionsJsonArray.getJSONObject(i);
				
				AnswerSection section = answerSheet.getSections().get(i);

				JSONArray answersJsonArray = sectionJsonObject.getJSONArray("answers");
				List<Answer> answers = section.getAnswers();
				
				ChoiceAnswer choiceAnswer;
				TextAnswer textAnswer;
				
				for(int j = 0; j<answersJsonArray.length(); j++) {
					JSONObject answerJsonObject = answersJsonArray.getJSONObject(j);
					Long questionId = answerJsonObject.getLong("questionId");
					Question question = questionDao.getQuestion(questionId);
					
					if(question instanceof ChoiceQuestion){
						choiceAnswer = new ChoiceAnswer((ChoiceQuestion) question);
						JSONArray selectionsJsonArray = answerJsonObject.getJSONArray("selections");
						for(int sel = 0; sel<selectionsJsonArray.length(); sel++){
							choiceAnswer.getSelections().add(selectionsJsonArray.getInt(sel));
						}
						choiceAnswer.setIndex(j);
						answers.add(choiceAnswer);
					}
					else if(question instanceof TextQuestion){
						textAnswer = new TextAnswer((TextQuestion) question);
						textAnswer.setText(answerJsonObject.getString("text"));
						textAnswer.setIndex(j);						
						answers.add(textAnswer);
					}					
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return response;
	}

}
