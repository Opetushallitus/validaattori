package validator.api;

import java.util.List;

public interface Validator {


  public List<ValidationResult> validate(Object validatable);


}
