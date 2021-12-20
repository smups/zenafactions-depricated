package ZenaCraft.exceptions;

public class ByteOverFlowException extends Exception{

    private static String defaultMsg = "A byte overflow occured!";

    public ByteOverFlowException(String msg, Throwable err){
        super(msg, err);
    }

    public ByteOverFlowException(){
        super(defaultMsg);
    }
    
}