package net.obvj.junit.utils.matchers;

public class MyCustomException extends Exception
{
    private static final long serialVersionUID = 5725758277733721926L;

    private final String customString;
    private final int code;

    public MyCustomException(String message, String customString, int code)
    {
        super(message);
        this.customString = customString;
        this.code = code;
    }

    public String getCustomString()
    {
        return customString;
    }

    public int getCode()
    {
        return code;
    }

}