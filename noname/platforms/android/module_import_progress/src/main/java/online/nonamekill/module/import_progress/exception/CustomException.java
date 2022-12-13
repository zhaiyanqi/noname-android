package online.nonamekill.module.import_progress.exception;

public class CustomException extends RuntimeException {

    private String msg;

    public CustomException(){

    }

    public CustomException(String message){
        this.msg = message;
    }


    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
