package service;

public class UnauthorizedException extends Exception{
    public UnauthorizedException() { super("Error: unauthorized"); }
}
