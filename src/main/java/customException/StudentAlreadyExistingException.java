package customException;

public class StudentAlreadyExistingException extends StudentException{
    public StudentAlreadyExistingException(String message){
        super(message);
    }
}
