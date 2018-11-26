package com.carvajal.facturaclaro.sl;

import java.io.IOException;

import com.carvajal.facturaclaro.ral.dto.ActivationDTO;
import com.carvajal.facturaclaro.ral.dto.AuthorizationDTO;
import com.carvajal.facturaclaro.utils.PATH;
import com.carvajal.facturaclaro.utils.Utils;

public class ActivationSL {

	public static ActivationDTO getActivation(String body, String token, String md5, String date, String transationId, String length) throws IOException {
		
		ActivationDTO activationDTO = new ActivationDTO(); 
		AuthorizationDTO authorizationDTO = new AuthorizationDTO();
		
		ActivationDTO activation = new Utils<ActivationDTO>().doRest(PATH.ACTIVATIONIONURL, 
				"POST", activationDTO, body, token, md5, date, transationId, length); 		
		return activation;	
	}
}
