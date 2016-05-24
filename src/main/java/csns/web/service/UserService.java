/*
 * This file is part of the CSNetwork Services (CSNS) project.
 * 
 * Copyright 2012, Chengyu Sun (csun@calstatela.edu).
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

import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import csns.model.core.User;
import csns.model.core.dao.UserDao;
import csns.security.ServiceTokenUtils;

@RestController
@SuppressWarnings("deprecation")
public class UserService {

	@Autowired
	private UserDao userDao;

	@Autowired
	private ServiceTokenUtils tokenUtils;

	@Autowired
	private PasswordEncoder passwordEncoder;

	private static final Logger logger = LoggerFactory.getLogger(UserService.class);

	byte[] key = null;

	@PostConstruct
	public void init() {
		key = Base64.decodeBase64("dEusvsOKeGZwI2Ybuv1wZA==".getBytes());
	}

	@RequestMapping("/service/user/login2")
	public String login2(@RequestParam String username, @RequestParam String password, ModelMap models) {
		User user = userDao.getUserByUsername(username);
		if (user == null || !passwordEncoder.encodePassword(password, null).equals(user.getPassword())) {
			logger.info("Username or password does not match for " + username);
			user = null;
		} else {
			logger.info("Credentials verified for " + username);
			if (user.getAccessKey() == null) {
				user.setAccessKey(UUID.randomUUID().toString());
				user = userDao.saveUser(user);
				logger.info("Access key generated for " + username);
			}
		}
		models.put("user", user);
		return "jsonView";
	}

	@RequestMapping(value = "/service/user/login", method = RequestMethod.GET)
	public ResponseEntity<String> login(@RequestParam(name = "username") String username,
			@RequestParam(name = "password") String password, ModelMap models) {

		/*check user name and password, if valid, create a token*/

		User user = userDao.getUserByUsername(username);
		if (user == null || !passwordEncoder.encodePassword(password, null).equals(user.getPassword())) {
			logger.info("Username or password does not match for " + username);
			return new ResponseEntity<String>("Unauthorized", HttpStatus.UNAUTHORIZED);
		}

		/*Reload password post-authentication so we can generate token*/
		String token = this.tokenUtils.generateToken(user);

		/* Return the token*/
		return ResponseEntity.ok(token);
	}

	public String issueToken(String cin, String username) {

		try {
			KeyGenerator keyGen = KeyGenerator.getInstance("AES");
			keyGen.init(128);

			SecretKey secKey = new SecretKeySpec(key, 0, key.length, "AES");
			String randomKey = new String(keyGen.generateKey().getEncoded());

			Cipher aesCipher = Cipher.getInstance("AES");

			byte[] byteText = (username + "\n" + cin + "\n" + randomKey).getBytes();

			aesCipher.init(Cipher.ENCRYPT_MODE, secKey);
			byte[] byteCipherText = aesCipher.doFinal(byteText);

			return new String(Base64.encodeBase64(byteCipherText));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
