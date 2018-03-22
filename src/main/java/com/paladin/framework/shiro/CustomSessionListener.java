package com.paladin.framework.shiro;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.SessionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomSessionListener implements SessionListener{

    private Logger logger = LoggerFactory.getLogger(CustomSessionListener.class);
	
	@Override
	public void onStart(Session session) {		
		logger.debug("session start");
	}

	@Override
	public void onStop(Session session) {	
		logger.debug("session stop");
	}

	@Override
	public void onExpiration(Session session) {	
		logger.debug("session expiration");
	}

}
