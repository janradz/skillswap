package de.fhmuenster.skillswap.dto;

import java.time.LocalDateTime;

public class ExceptionResponseDTO
 {
    private String message;
    private int errorCode;
    private LocalDateTime timestamp;
    private String path;
    private String errorName;

    public ExceptionResponseDTO(String message)
    {
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    public ExceptionResponseDTO(String message, int errorCode, String path, String errorName)
    {
        this.message = message;
        this.errorCode = errorCode;
        this.timestamp = LocalDateTime.now();
        this.path = path;
        this.errorName = errorName;
    }

     public String getMessage()
     {
         return message;
     }

     public void setMessage(String message)
     {
         this.message = message;
     }

     public int getErrorCode()
     {
         return errorCode;
     }

     public void setErrorCode(int errorCode)
     {
         this.errorCode = errorCode;
     }

     public LocalDateTime getTimestamp()
     {
         return timestamp;
     }

     public void setTimestamp(LocalDateTime timestamp)
     {
         this.timestamp = timestamp;
     }

     public String getPath()
     {
         return path;
     }

     public void setPath(String path)
     {
         this.path = path;
     }

     public String getErrorName()
     {
         return errorName;
     }

     public void setErrorName(String errorName)
     {
         this.errorName = errorName;
     }
 }
