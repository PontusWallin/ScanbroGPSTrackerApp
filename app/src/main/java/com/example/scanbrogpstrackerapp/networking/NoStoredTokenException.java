package com.example.scanbrogpstrackerapp.networking;

public class NoStoredTokenException extends Exception {
    NoStoredTokenException() {
        super("No login token stored in app! Try logging in again.");
    }
}
