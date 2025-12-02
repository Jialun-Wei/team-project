<<<<<<<< HEAD:src/main/java/interfaceadapters/controllers/LoginController.java
package interfaceadapters.controllers;
========
package interfaceadapters.login;
>>>>>>>> origin:src/main/java/interfaceadapters/login/LoginController.java

import usecase.login.LoginInputData;
import usecase.login.LoginInteractor;
import usecase.login.LoginOutputData;

public class LoginController {

    private final LoginInteractor loginInteractor;

    public LoginController(LoginInteractor loginInteractor) {
        this.loginInteractor = loginInteractor;
    }

    public LoginOutputData login(String username, String password) {
        LoginInputData input = new LoginInputData(username, password);
        return loginInteractor.execute(input);
    }
}
