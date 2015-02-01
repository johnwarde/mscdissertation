package com.interop.webapp;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Map;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonWrapper {
	static Logger log = Logger.getLogger(WebApp.class.getName());
	ObjectMapper objectMapper = new ObjectMapper();

	public String toJson(Map<String, Object> mapObject) {
		StringWriter json = new StringWriter();
		try {
			objectMapper.writeValue(json, mapObject);
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}		
		return json.toString();
	}
	
	public Map<String, Object> fromJson(String jsonMessage) {

		StringReader jsonString = new StringReader(jsonMessage);
		Map<String, Object> mapObject = null;
		try {
			mapObject = objectMapper.readValue(jsonString,
					new TypeReference<Map<String, Object>>() {
					});
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// Badly formatted JSON message, ignore
			log.error(String.format("Badly formatted JSON message, ignoring (%s)", e.getMessage()));
			mapObject = null;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return mapObject;
	}
}
