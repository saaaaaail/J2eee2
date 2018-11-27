package water.ustc.action;

public class LoginAction {
    public String handleLogin() {
        System.out.println("执行了handleLogin方法");
        int count = (int)(Math.random()*10);
        if(count < 4){
            return "failure";

        }else{
            return "success";
        }

    }
}
