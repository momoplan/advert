package com.ruyicai.advert.service;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruyicai.advert.center.ScoreWall;

@Service
public class AdvertManager {

	@Autowired
	private Map<String, ScoreWall> map;
	
	public ScoreWall getScoreWall(String source) {
		return map.get(source);
	}
	
}
