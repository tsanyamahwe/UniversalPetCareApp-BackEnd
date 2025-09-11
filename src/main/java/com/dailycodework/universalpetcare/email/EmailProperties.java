package com.dailycodework.universalpetcare.email;

import lombok.Data;

@Data
public class EmailProperties {
    public static final String DEFAULT_HOST = "smtp.gmail.com";
    public static final  int DEFAULT_PORT = 587;
    public static final String DEFAULT_SENDER = "tsanyas@gmail.com";
    public static final String DEFAULT_USERNAME = "tsanyas@gmail.com";
    public static final String DEFAULT_PASSWORD ="vhluoxspulntziuv";
    public static final boolean DEFAULT_AUTH = true;
    public static final boolean DEFAULT_STARTTLS = true;
}
