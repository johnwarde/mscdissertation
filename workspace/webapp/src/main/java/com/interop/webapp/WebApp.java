/*
 * Copyright 2012-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.interop.webapp;

import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@EnableAutoConfiguration
@ComponentScan
@Controller
public class WebApp extends WebMvcConfigurerAdapter {
	// TODO: Need to have the following file location in a configuration file somewhere
	static String imageFilesRoot = "file:/Users/johnwarde/Downloads/webappresources";
	static String imagesWebPath = "resources";
	static String imagesWebPathMask = "/" + imagesWebPath + "/**";
	
	static Logger log = Logger.getLogger(WebApp.class.getName());

	@RequestMapping("/")
	public String home(Map<String, Object> model) {
		//log.info("HERE!!");
	    String user = SecurityContextHolder.getContext().getAuthentication().getName();
		UserImageFileRepository store = new UserImageFileRepository(user, imageFilesRoot);
		List<ImageDetailBean> collection = store.getWebPaths();	    
		model.put("user", user);
		model.put("imagesWebPath", imagesWebPath);
		model.put("imagerefs", collection);
		model.put("count", collection.size());
		return "home";
	}

	@RequestMapping(value = "/upload", method = RequestMethod.GET)
	public String upload(Map<String, Object> model) {
		return "upload";
	}

	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	public String upload(Map<String, Object> model, 
			@RequestParam(value="imagename", defaultValue="") String imagename, 
			@RequestParam("uploadfile") MultipartFile uploadedfile) {
	    String user = SecurityContextHolder.getContext().getAuthentication().getName();
		UserImageFileRepository store = new UserImageFileRepository(user, imageFilesRoot);
		Boolean success = store.newUpload(imagename, uploadedfile);
		model.put("success", success);
		model.put("imagename", imagename);
		return "upload";
	}	

/*  // TODO: Doesn't work
	@RequestMapping("/logout")
	public String logout(Map<String, Object> model) {
		return "redirect:/";
	}
*/
	
	public static void main(String[] args) throws Exception {
		new SpringApplicationBuilder(WebApp.class).run(args);
	}

	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/login").setViewName("login");
	}
	
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(imagesWebPathMask).addResourceLocations(imageFilesRoot + '/');
    }
    
	@Bean
	public ApplicationSecurity applicationSecurity() {
		return new ApplicationSecurity();
	}

	@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
	protected static class ApplicationSecurity extends WebSecurityConfigurerAdapter {

		@Autowired
		private SecurityProperties security;

		@Autowired
		private DataSource dataSource;

		@Override
		protected void configure(HttpSecurity http) throws Exception {
			http
				.authorizeRequests()
				.antMatchers("/css/**", imagesWebPathMask).permitAll().anyRequest()
					.fullyAuthenticated().and().formLogin().loginPage("/login")
					.failureUrl("/login?error").permitAll();
		}

		@Override
		public void configure(AuthenticationManagerBuilder auth) throws Exception {
			auth.jdbcAuthentication().dataSource(this.dataSource);
		}

	}
	
	/*  May not need this ... but it works
	// ENVIRONMENT VARIABLES
	// class member
	private AnnotationConfigWebApplicationContext serverContext = new AnnotationConfigWebApplicationContext();
	// Put this in a method
	ConfigurableEnvironment ce = this.serverContext.getEnvironment();
	Map<String, Object> envVars = ce.getSystemEnvironment();
	String imageStore = (String) envVars.get("LOCALAPPDATA");
*/
	
/*  May not need this ... but it works
    // SERVLET PATH
    // class member
	@Autowired(required=true)
	private HttpServletRequest request;
	// Put this in a method
	ServletContext servletContext = request.getSession().getServletContext();
	String absoluteDiskPath = servletContext.getRealPath("/");
	Cookie[] myCookies = request.getCookies();
*/
	

}
