package validator;

import java.util.List;

public interface Validator {


  public List<ValidationResult> validate(Object validatable);


}
