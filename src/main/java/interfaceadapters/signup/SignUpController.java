<<<<<<<< HEAD:src/main/java/interfaceadapters/controllers/SignUpController.java
package interfaceadapters.controllers;
========
package interfaceadapters.signup;
>>>>>>>> origin:src/main/java/interfaceadapters/signup/SignUpController.java

import usecase.signup.SignUpInputData;
import usecase.signup.SignUpInteractor;
import usecase.signup.SignUpOutputData;

public class SignUpController {

    private final SignUpInteractor signUpInteractor;

    public SignUpController(SignUpInteractor signUpInteractor) {
        this.signUpInteractor = signUpInteractor;
    }

    public SignUpOutputData signUp(String username, String password) {
        SignUpInputData input = new SignUpInputData(username, password);
        return signUpInteractor.execute(input);
    }
}
