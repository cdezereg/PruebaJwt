package cl.dp.signup.payload.response;

import java.util.List;

public class ErrorResponse {
	private List<ErrorMessage> error;
	
	
	public List<ErrorMessage> getError() {
		return error;
	}

	public void setError(List<ErrorMessage> error) {
		this.error = error;
	}	
}
