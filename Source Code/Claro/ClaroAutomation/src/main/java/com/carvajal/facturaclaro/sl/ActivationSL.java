package com.carvajal.facturaclaro.sl;

import com.carvajal.facturaclaro.ral.dto.ActivationDTO;
import com.carvajal.facturaclaro.utils.PATH;
import com.carvajal.facturaclaro.utils.Utils;

public class ActivationSL {

	public static ActivationDTO getActivation(String body, String token, String md5, String date, String transationId,
			String length){

		ActivationDTO activationDTO = new ActivationDTO();

		ActivationDTO activation = new Utils<ActivationDTO>().doRest(PATH.ACTIVATIONIONURL, "POST", activationDTO, body,
				token, md5, date, transationId, length);
		return activation;
	}
}
