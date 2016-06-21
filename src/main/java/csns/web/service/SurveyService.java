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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;
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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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
import csns.model.qa.dao.QuestionDao;
import csns.model.survey.Survey;
import csns.model.survey.SurveyResponse;
import csns.model.survey.dao.SurveyDao;

@Controller
public class SurveyService {

	@Autowired
	private DepartmentDao departmentDao;

	@Autowired
	private SurveyDao surveyDao;

	@Autowired
	private QuestionDao questionDao;
	
	@Autowired
	private UserDao userDao;

	@RequestMapping("/service/survey/list")
	public String list(ModelMap models, @RequestParam(name = "dept") String dept) {

		Department department = departmentDao.getDepartment(dept);
		List<Survey> openSurveys = null;
		openSurveys = surveyDao.getOpenSurveys(department);
		/*--------------- Test data --------------*/
		Survey testSurvey = new Survey();
		testSurvey.setId(1L);
		testSurvey.setName("survey1");
		QuestionSheet questionSheet = new QuestionSheet();
		questionSheet.setId(1L);
		questionSheet.setDescription("This is a test question sheet.");
		List<QuestionSection> sections = new ArrayList<>();

		/* add section 1 */
		QuestionSection section = new QuestionSection();
		section.setId(1L);
		section.setDescription("section1");
		List<Question> questions = new ArrayList<>();
		ChoiceQuestion question1 = new ChoiceQuestion();
		question1.setId(1L);
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
		question2.setId(2L);
		question2.setDescription("Enter your email address here.");
		questions.add(question2);
		section.setQuestions(questions);
		sections.add(section);

		/* add section 2 */
		questions = new ArrayList<>();
		section = new QuestionSection();
		section.setId(2L);
		section.setDescription("section2");
		questions = new ArrayList<>();
		question1 = new ChoiceQuestion();
		question1.setId(3L);
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
		question1.setId(4L);
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
		question2.setId(5L);
		question2.setDescription("Enter your cin here.");
		questions.add(question2);

		question2 = new TextQuestion();
		question2.setDescription("Enter your start year.");
		questions.add(question2);

		section.setQuestions(questions);
		sections.add(section);

		questionSheet.setSections(sections);
		testSurvey.setQuestionSheet(questionSheet);
		openSurveys.add(testSurvey);
		/*-----------------------------------------*/

		models.put("surveys", openSurveys);
		return "jsonView";
	}

	@RequestMapping(value = "/service/survey/saveAnswers", method = RequestMethod.POST)
	public String saveAnswers(@RequestParam(name = "dept") String dept, HttpServletRequest request) {
		@SuppressWarnings("unused")
		Department department = departmentDao.getDepartment(dept);
		/* get current user */
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		/* get answers set author in answer sheet as current user */
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

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "jsonView";
	}

	public SurveyResponse getResponseFromJson(JSONObject jsonObject, User user) {
		SurveyResponse response = new SurveyResponse();
		AnswerSheet answerSheet = new AnswerSheet();
		answerSheet.setAuthor(user);
		Survey survey = new Survey();

		response.setAnswerSheet(answerSheet);
		response.setSurvey(survey);

		try {
			/*
			 * ObjectMapper mapper = new ObjectMapper();
			 * 
			 * @SuppressWarnings("unchecked") Map<String,Object> map =
			 * mapper.readValue(jsonObject.toString(), HashMap.class);
			 * 
			 * response = mapper.readValue(jsonObject.toString(),
			 * SurveyResponse.class);
			 * 
			 * Gson gson = new Gson(); if(jsonObject.get("id") != null)
			 * response.setId((Long)jsonObject.getLong("id"));
			 */

			JSONObject surveyObject = jsonObject.getJSONObject("survey");
			// survey = surveyDao.getSurvey(surveyObject.getLong("id"));

			/* set answer sheet */
			JSONObject answerSheetJsonObject = jsonObject.getJSONObject("answerSheet");
			JSONArray sectionsJsonArray = answerSheetJsonObject.getJSONArray("sections");

			for (int i = 0; i < sectionsJsonArray.length(); i++) {
				JSONObject sectionJsonObject = sectionsJsonArray.getJSONObject(i);
				
				AnswerSection section = new AnswerSection();
				section.setAnswerSheet(answerSheet);

				JSONArray answersJsonArray = sectionJsonObject.getJSONArray("answers");
				List<Answer> answers = new ArrayList<>();
				ChoiceAnswer choiceAnswer;
				TextAnswer textAnswer;
				for(int j = 0; j<answersJsonArray.length(); j++) {
					JSONObject answerJsonObject = answersJsonArray.getJSONObject(j);
					String type = answerJsonObject.getJSONObject("question").getString("type");
					switch(type) {
						case "CHOICE" :
							choiceAnswer = new ChoiceAnswer();
							JSONArray selectionsJsonArray = answerJsonObject.getJSONArray("selections");
							for(int sel = 0; sel<selectionsJsonArray.length(); sel++){
								choiceAnswer.getSelections().add(selectionsJsonArray.getInt(sel));
							}
							choiceAnswer.setIndex(answerJsonObject.getInt("index"));
							choiceAnswer.setQuestion(questionDao.getQuestion(answerJsonObject.getJSONObject("question").getLong("id")));
			
							answers.add(choiceAnswer);
							break;
						case "TEXT" :
							textAnswer = new TextAnswer();
							textAnswer.setText(answerJsonObject.getString("text"));
							textAnswer.setIndex(answerJsonObject.getInt("index"));
							textAnswer.setQuestion(questionDao.getQuestion(answerJsonObject.getJSONObject("question").getLong("id")));
							
							answers.add(textAnswer);
							break;
					}
				}
				section.setAnswers(answers);
				answerSheet.getSections().add(section);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return response;
	}

}
