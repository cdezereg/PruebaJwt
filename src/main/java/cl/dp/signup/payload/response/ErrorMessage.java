package cl.dp.signup.payload.response;


public class ErrorMessage {
	private String timestamp;
	private int codigo;
	private String detail;
	
	public String getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	public int getCodigo() {
		return codigo;
	}
	public void setCodigo(int codigo) {
		this.codigo = codigo;
	}
	public String getDetail() {
		return detail;
	}
	public void setDetail(String detail) {
		this.detail = detail;
	}
	public ErrorMessage(String timestamp, int codigo, String detail)
	{
		this.timestamp = timestamp;
		this.codigo = codigo;
		this.detail = detail;
	}
}
