package com.carvajal.facturaclaro.sl;

import java.io.IOException;

import com.carvajal.facturaclaro.ral.dto.AuthorizationDTO;
import com.carvajal.facturaclaro.ral.dto.NotificationDTO;
import com.carvajal.facturaclaro.utils.PATH;
import com.carvajal.facturaclaro.utils.Utils;

public class NotificationSL {
	
public static NotificationDTO getNotification(String body, String token, String md5, String date, String transationId, String length) throws IOException {	
	
		NotificationDTO notificationDTO = new NotificationDTO(); 
		AuthorizationDTO authorizationDTO = new AuthorizationDTO(); 
		NotificationDTO notification = new Utils<NotificationDTO>().doRest(PATH.NOTIFICATIONURL,				
				"POST", notificationDTO, body, token, md5, date, transationId, length); 
		
		return notification;	
	}
	

}
