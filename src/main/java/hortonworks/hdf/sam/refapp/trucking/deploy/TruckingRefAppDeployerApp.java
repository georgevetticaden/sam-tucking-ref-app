package hortonworks.hdf.sam.refapp.trucking.deploy;

import java.io.IOException;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import hortonworks.hdf.sam.sdk.app.manager.SAMAppManager;
import hortonworks.hdf.sam.sdk.app.manager.SAMAppManagerImpl;

public class TruckingRefAppDeployerApp {
	
	Logger LOG = LoggerFactory.getLogger(TruckingRefAppDeployerApp.class);
	private SAMAppManager samAppManager;
	Properties appProperties;
	private String samRestUrl;
	private String samAppName;
	private Integer deployTimeOut;
	
	public TruckingRefAppDeployerApp(String propFileLocation) {
		loadAppPropertiesFile(propFileLocation);

		samAppManager = new SAMAppManagerImpl(samRestUrl);
	}

	private void loadAppPropertiesFile(String propFileLocation) {
		appProperties = new Properties();
		Resource appPropResource = new FileSystemResource(propFileLocation);
		
		if(!appPropResource.exists()) {
			String errMsg = "App Properties file["+propFileLocation + "] doesn't exist or cannot be loaded";
			LOG.error(errMsg);
			throw new RuntimeException(errMsg);
		}

		try {
			appProperties.load(appPropResource.getInputStream());
		} catch (IOException e) {
			String errMsg = "Cannot load App Properties file["+propFileLocation + "]";
			LOG.error(errMsg);
			throw new RuntimeException(errMsg, e);
		}
		
		this.samRestUrl = appProperties.getProperty(AppPropertiesConstants.SAM_REST_URL);
		if(StringUtils.isEmpty(samRestUrl)) {
			String errMsg = "Property["+AppPropertiesConstants.SAM_REST_URL +"] is required";
			throw new RuntimeException(errMsg);
		}		
		
		this.samAppName = appProperties.getProperty(AppPropertiesConstants.SAM_APP_NAME);
		if(StringUtils.isEmpty(samAppName)) {
			String errMsg = "Property["+AppPropertiesConstants.SAM_APP_NAME +"] is required";
			throw new RuntimeException(errMsg);
		}		
		
		String deployTimeOutString = appProperties.getProperty(AppPropertiesConstants.SAM_APP_DEPLOY_TIMEOUT);
		if(StringUtils.isEmpty(deployTimeOutString)) {
			String errMsg = "Property["+AppPropertiesConstants.SAM_APP_DEPLOY_TIMEOUT +"] is required";
			throw new RuntimeException(errMsg);
		}		
		this.deployTimeOut = Integer.valueOf(deployTimeOutString);
		
		
	}

	public static void main(String args[]) {
		
		if(args == null || args.length != 1) {
			String errMsg = "One arg with location of properties file need to be passed to app";
			throw new RuntimeException(errMsg);
		}
		String propFileLocation = args[0];
		TruckingRefAppDeployerApp deployerApp = new TruckingRefAppDeployerApp(propFileLocation);
		deployerApp.deploy();
	}

	public void deploy() {
		samAppManager.deploySAMApplication(samAppName, deployTimeOut);
		
	}

}
