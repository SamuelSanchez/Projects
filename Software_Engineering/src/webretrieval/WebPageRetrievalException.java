package webretrieval;

/**
 * This is an Exception class for WebPageRetrieval
 * 
 * @author Samuel E. Sanchez
 *
 */
public class WebPageRetrievalException extends Exception{

	private static final long serialVersionUID = 123456789L;

	public WebPageRetrievalException(){}
	
	/**
	 * Creates an exception
	 * @param error Error to be returned
	 * @param stackTrace Stack of errors to be returned
	 */
	public WebPageRetrievalException(String error, StackTraceElement[] stackTrace){
		super( error );
		this.setStackTrace(stackTrace);
	}
}
