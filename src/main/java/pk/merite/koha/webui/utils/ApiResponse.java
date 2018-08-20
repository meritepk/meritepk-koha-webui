package pk.merite.koha.webui.utils;

public class ApiResponse<T> {

    private T result;

    public ApiResponse(T result) {
        this.result = result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public T getResult() {
        return result;
    }

}
