package cl.dp.signup.payload.response;

public class ServiceResponse {
	private String response;
	private boolean hasError;	public String getResponse() {
		return response;
	}
	public void setResponse(String response) {
		this.response = response;
	}

	public boolean isHasError() {
		return hasError;
	}
	public void setHasError(boolean hasError) {
		this.hasError = hasError;
	}
	public ServiceResponse(String response, boolean hasError)
	{
		this.response = response;
		this.hasError = hasError;
	}
}
