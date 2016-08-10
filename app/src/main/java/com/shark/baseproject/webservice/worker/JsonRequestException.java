/******************************************************************
 * Copyright (C) Newegg Corporation. All rights reserved.
 * 
 * Author: 
 * Create Date: 
 * Description:
 *         
 * Revision History:
 * Date         Author           Description	
 * 2014/03/19	Luke.Y.Tsai		 catch Exception.
 *****************************************************************/
package com.shark.baseproject.webservice.worker;

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;

public class JsonRequestException extends VolleyError {

	private Exception exception;

	public JsonRequestException(NetworkResponse response, Exception exception) {
		super(response);
        exception.printStackTrace();
		this.exception = exception;
	}

	@Override
	public Throwable getCause() {
		return exception;
	}

}
