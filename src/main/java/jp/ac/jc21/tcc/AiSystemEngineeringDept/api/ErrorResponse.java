package jp.ac.jc21.tcc.AiSystemEngineeringDept.api;


public class ErrorResponse {
    private OpenAiError error;

    public OpenAiError getError() {
        return error;
    }

    public void setError(OpenAiError error) {
        this.error = error;
    }

    public static class OpenAiError {
        private String message;
        private String type;
        private String param;
        private String code;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getParam() {
            return param;
        }

        public void setParam(String param) {
            this.param = param;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }
    }
}