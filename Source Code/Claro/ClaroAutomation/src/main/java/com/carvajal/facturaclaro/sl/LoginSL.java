package com.carvajal.facturaclaro.sl;

import java.io.IOException;

import com.carvajal.facturaclaro.ral.dto.LoginDTO;
import com.carvajal.facturaclaro.utils.PATH;
import com.carvajal.facturaclaro.utils.Utils;

public class LoginSL {

	public static LoginDTO getLogin(String body, String md5, String date, String transationId, String length) throws IOException {
		
		LoginDTO loginDTO = new LoginDTO(); 
		LoginDTO login = new Utils<LoginDTO>().doRest(PATH.LOGINURL, "POST", loginDTO, body, "", md5, date, transationId, length);  
		
		return login;

	}
	
} 
