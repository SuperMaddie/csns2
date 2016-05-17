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

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import csns.model.academics.Department;
import csns.model.academics.dao.DepartmentDao;
import csns.model.core.User;
import csns.model.core.dao.UserDao;
import csns.model.qa.ChoiceQuestion;
import csns.model.qa.Question;
import csns.model.qa.QuestionSection;
import csns.model.qa.QuestionSheet;
import csns.model.qa.TextQuestion;
import csns.model.survey.Survey;
import csns.model.survey.dao.SurveyDao;

@Controller
public class SurveyService {

	@Autowired
	private DepartmentDao departmentDao;

	@Autowired
	private SurveyDao surveyDao;

	@Autowired
	private UserDao userDao;

	byte[] key = null;

	@PostConstruct
	public void init() {
		key = Base64.decodeBase64("dEusvsOKeGZwI2Ybuv1wZA==".getBytes());
	}

	@RequestMapping("	")
	public String list(ModelMap models, @RequestParam(name = "dept") String dept,
			@RequestParam(name = "token") String token) {

		Department department = departmentDao.getDepartment(dept);
		List<Survey> openSurveys = null;
		String status = "200";
		/* respond if token is valid */
		if (validateToken(token)) {
			openSurveys = surveyDao.getOpenSurveys(department);
			/*--------------- Test data --------------*/
			Survey testSurvey = new Survey();
			testSurvey.setName("survey1");
			QuestionSheet questionSheet = new QuestionSheet();
			questionSheet.setDescription("This is a test question sheet.");
			List<QuestionSection> sections = new ArrayList<>();

			/*add section 1*/
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
			
			/*add section 2*/
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
			openSurveys.add(testSurvey);
			/*-----------------------------------------*/
		}else{
			status = "401";
		}

		models.put("status", status);
		models.put("surveys", openSurveys);
		return "jsonView";
	}

	@RequestMapping(value="/service/survey/saveAnswers", method=RequestMethod.POST)
	public void saveAnswers(@RequestParam(name = "dept") String dept, HttpServletRequest request) {
		@SuppressWarnings("unused")
		Department department = departmentDao.getDepartment(dept);
		/* not complete */
		/*
		 * StringBuffer buffer = new StringBuffer(); String line = null; try {
		 * BufferedReader reader = request.getReader(); while((line =
		 * reader.readLine()) != null) { buffer.append(line); } } catch
		 * (IOException e) { e.printStackTrace(); }
		 * System.out.println(buffer.toString());
		 */
	}

	public boolean validateToken(String token) {

		SecretKey secKey = new SecretKeySpec(key, 0, key.length, "AES");
		byte[] cipherText = Base64.decodeBase64(token);
		User user = null;

		try {
			Cipher aesCipher = Cipher.getInstance("AES");
			aesCipher.init(Cipher.DECRYPT_MODE, secKey);
			byte[] bytePlainText = aesCipher.doFinal(cipherText);

			String tokenValue = new String(bytePlainText);
			String[] subStrings = tokenValue.split("\n");
			if (subStrings.length >= 2) {
				user = userDao.getUserByUsername(subStrings[0]);
				if (user != null && user.getCin().equalsIgnoreCase(subStrings[1])) {
					return true;
				}
			}
		} catch (Exception e) {
			return false;
		}
		return false;
	}
}
