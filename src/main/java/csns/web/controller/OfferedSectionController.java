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
package csns.web.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.WebUtils;

import csns.model.academics.Course;
import csns.model.academics.Department;
import csns.model.academics.OfferedSection;
import csns.model.academics.TentativeSchedule;
import csns.model.academics.Term;
import csns.model.academics.dao.DepartmentDao;
import csns.model.academics.dao.OfferedSectionDao;
import csns.model.academics.dao.TentativeScheduleDao;
import csns.model.preRegistration.request.PreRegistrationRequest;
import csns.model.preRegistration.request.dao.PreRegistrationRequestDao;
import csns.security.SecurityUtils;
import csns.util.FileUtils;
import csns.web.editor.CoursePropertyEditor;
import csns.web.editor.TermPropertyEditor;

@Controller
@SessionAttributes("section")
public class OfferedSectionController {

	@Autowired
	private TentativeScheduleDao scheduleDao;

	@Autowired
	private OfferedSectionDao sectionDao;

	@Autowired
	private FileUtils fileUtils;

	@Autowired
	private OfferedSectionDao offeredSectionDao;

	@Autowired
	private PreRegistrationRequestDao requestDao;

	@Autowired
	private DepartmentDao departmentDao;

	@Autowired
	private WebApplicationContext context;

	private static final Logger logger = LoggerFactory.getLogger(OfferedSectionController.class);

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(Term.class, (TermPropertyEditor) context.getBean("termPropertyEditor"));
		binder.registerCustomEditor(Course.class, (CoursePropertyEditor) context.getBean("coursePropertyEditor"));
	}

	@RequestMapping(value = "/department/{dept}/offeredSection/getRequestContent", method = RequestMethod.GET)
	protected void getRequestContent(HttpServletRequest request, HttpServletResponse response) {
		Long id = Long.parseLong(request.getParameter("id"));
		PreRegistrationRequest req = requestDao.getRequest(id);
		SimpleDateFormat dateFormat = new SimpleDateFormat("DD/MM/yyyy");

		JSONObject json = new JSONObject();
		try {
			json.put("name", req.getRequester().getName());
			json.put("comment", req.getComment());
			json.put("email", req.getRequester().getEmail());
			json.put("cin", req.getRequester().getCin());
			json.put("date", dateFormat.format(req.getDate()));
		} catch (JSONException e1) {
			e1.printStackTrace();
		}

		PrintWriter out;
		try {
			out = response.getWriter();
			out.write(json.toString());
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@RequestMapping("/department/{dept}/offeredSection")
	public String view(@PathVariable String dept, @RequestParam Long id, @RequestParam(required = false) Term term,
			ModelMap models) {

		Term nextTerm = new Term().next();
		if (term == null) {
			term = nextTerm;
		}
		Department department = departmentDao.getDepartment(dept);
		OfferedSection section = offeredSectionDao.getSection(id);
		List<PreRegistrationRequest> requests = requestDao.getRequests(section);
		Map<Long, String> comments = new HashMap<>();
		for (PreRegistrationRequest req : requests) {
			if (req.getComment() != null) {
				comments.put(req.getRequester().getId(), req.getComment());
			}
		}

		models.put("comments", comments);
		models.put("requests", requests);
		models.put("section", section);
		models.put("department", department);
		models.put("term", term);
		return "offeredSection/view";
	}

	@RequestMapping(value = "/department/{dept}/offeredSection/import", method = RequestMethod.GET)
	public String importSection(@PathVariable String dept, @RequestParam Term term, ModelMap models) {
		Department department = departmentDao.getDepartment(dept);

		models.put("department", department);
		models.put("term", term);
		return "offeredSection/import0";
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/department/{dept}/offeredSection/import", method = RequestMethod.POST)
	public String importSection(@PathVariable String dept, @RequestParam("_page") int currentPage,
			@RequestParam("term") Term term, HttpServletRequest request,
			@RequestParam(value = "file", required = false) MultipartFile uploadedFile, ModelMap models,
			SessionStatus sessionStatus) {

		Department department = departmentDao.getDepartment(dept);
		int targetPage = WebUtils.getTargetPage(request, "_target", currentPage);
		//------ check if sent file is not empty ----//
		if (targetPage == 1 && uploadedFile != null && uploadedFile.isEmpty()) {
			models.put("message", "This field is required.");
			models.put("department", department);
			models.put("term", term);
			return "offeredSection/import0";
		}

		List<OfferedSection> sections = new ArrayList<>();
		InputStream fis;
		Map<String, List<String>> data = new HashMap<>();

		if (request.getParameter("_finish") == null) {
			if (targetPage == 1) {
				try {
					fis = uploadedFile.getInputStream();
					data = fileUtils.readExcel(fis);
				} catch (IOException e) {
					e.printStackTrace();
				}
				//----- create offeredSection objects and send to UI to be reviewed ---//
				sections = createSections(data, term, dept);
				request.getSession().setAttribute("sections", sections);
				models.put("department", department);
				models.put("sections", sections);
				models.put("term", term);
			}
			if (targetPage == 0 && currentPage == 1) {
				models.put("department", department);
				models.put("term", term);
			}
			return "offeredSection/import" + targetPage;
		}
		// save sections
		sections = (ArrayList<OfferedSection>) request.getSession().getAttribute("sections");
		TentativeSchedule schedule = scheduleDao.getSchedule(department, term);

		schedule.getSections().addAll(sections);
		schedule = scheduleDao.saveSchedule(schedule);

		/* link related sections */
		linkSections(schedule.getSections());

		logger.info(
				SecurityUtils.getUser().getName() + " imported sections to schedule of term " + term.getShortString());

		return "redirect:/department/" + dept + "/preRegistration?term=" + term.getCode();
	}

	/* create new sections from excel data */
	public List<OfferedSection> createSections(Map<String, List<String>> data, Term term, String dept) {

		List<String> headers = new ArrayList<>();
		headers.addAll(data.keySet());

		List<OfferedSection> sections = new ArrayList<>();

		int count = data.get("term").size();
		for (int i = 0; i < count; i++) {
			OfferedSection os = new OfferedSection();

			if (data.get("subj").get(i).equalsIgnoreCase(dept)) {
				os.setSubject(data.get("subj").get(i));
				os.setTerm(term);

				if (!data.get("cat").get(i).isEmpty()) {
					String tmp = data.get("cat").get(i).replaceAll("[^0-9]", "");
					os.setCourseCode(Float.valueOf(tmp).intValue());
				}

				if (!data.get("sect").get(i).isEmpty()) {
					String tmp = data.get("sect").get(i).replaceAll("[^0-9]", "");
					os.setNumber(Float.valueOf(tmp).intValue());
				}

				if (!data.get("class nbr").get(i).isEmpty())
					os.setClassNumber(Float.valueOf(data.get("class nbr").get(i)).intValue());

				os.setSectionTitle(data.get("title").get(i));
				os.setDay(data.get("day").get(i));

				if (!data.get("start").get(i).isEmpty())
					os.setStartTime(createTime(data.get("start").get(i)));

				if (!data.get("end").get(i).isEmpty())
					os.setEndTime(createTime(data.get("end").get(i)));

				os.setLocation(data.get("bldg/room").get(i));
				os.setType(data.get("type").get(i));

				// if (!data.get("instructor").get(i).isEmpty())
				// os.getInstructors()
				// .add(userDao.getUser(Long.valueOf(Float.valueOf(data.get("instructor").get(i)).intValue())));

				if (!data.get("prgrss unt").get(i).isEmpty())
					os.setUnits(Float.valueOf(data.get("prgrss unt").get(i)).intValue());
				os.setNotes(data.get("notes").get(i));

				sections.add(os);
				// do nothing for available/acad group/class type/mode
			}
		}
		return sections;
	}

	public String createTime(String s) {
		Float f = Float.valueOf(s) * 24;
		int hour = (int) (f / 1);
		int minute = ((int) Math.ceil(f % 1 * 60)) / 5 * 5;
		return hour + ":" + String.format("%02d", minute);
	}

	public void linkSections(List<OfferedSection> sections) {
		OfferedSection s1, s2;
		int i = 0;
		int j = 1;
		while (i < sections.size() && j < sections.size()) {
			s1 = sections.get(i);
			s2 = sections.get(j);
			if (!s1.getType().equalsIgnoreCase("LEC")) {
				i++;
				j = i + 1;
			} else if (s2.getType().equalsIgnoreCase("LEC")) {
				i = j;
				j = i + 1;
			} else if (s1.getCourseCode() == s2.getCourseCode() && s1.getType().equalsIgnoreCase("LEC")
					&& (s2.getType().equalsIgnoreCase("LAB") || s2.getType().equalsIgnoreCase("REC"))) {
				s1.getLinkedSections().add(s2);
				s1 = sectionDao.saveSection(s1);
				s2.getLinkedSections().add(s1);
				s2 = sectionDao.saveSection(s2);
				j++;
			} else {
				j++;
			}
		}
	}

}
